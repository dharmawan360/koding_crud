package com.example.uas_mpl_2011500095

import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.*
import com.squareup.picasso.Picasso
import androidx.activity.result.contract.ActivityResultContracts.*
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.*
import androidx.annotation.RequiresApi
import java.util.*
import android.text.InputType
import com.android.volley.*
import com.android.volley.toolbox.*

class EntriBuku : AppCompatActivity() {
    private lateinit var url: String
    private lateinit var sr: StringRequest
    private lateinit var rq: RequestQueue

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entri_buku)

        val modeEdit = intent.hasExtra("isbn")

        title = "${if(!modeEdit) "Tambah" else "Ubah"} Data buku"

        val etIsbn = findViewById<EditText>(R.id.etIsbn)
        val etJdlBuku = findViewById<EditText>(R.id.etJdlBuku)
        val etNmPengarang = findViewById<EditText>(R.id.etNmPengarang)
        val etPenerbit = findViewById<EditText>(R.id.etPenerbit)
        val etThnterbit = findViewById<EditText>(R.id.etThnterbit)
        val etTmptterbit = findViewById<EditText>(R.id.etTmptterbit)
        val etNocetak = findViewById<EditText>(R.id.etNocetak)
        val etJmlhHal = findViewById<EditText>(R.id.etJmlhHal)
        val spnKlasifikasi = findViewById<Spinner>(R.id.spnKlasifikasi)
        val btnFoto = findViewById<Button>(R.id.btnFoto)
        val imgFoto = findViewById<ImageView>(R.id.imgFoto)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        val arrKlasifikasi = arrayOf(
            "Umum","Filsafat","Agama","Sosial","Bahasa","Ilmu Murni/Sains","Teknologi","Seni",
            "Sastra","Geografi/Sejarah"
        )
        spnKlasifikasi.adapter = ArrayAdapter(
            this@EntriBuku,
            android.R.layout.simple_spinner_dropdown_item,
            arrKlasifikasi
        )

        if(modeEdit) {
            etIsbn.inputType = InputType.TYPE_NULL
            with(intent) {
                etIsbn.setText(getStringExtra("isbn"))
                etJdlBuku.setText(getStringExtra("jdl_buku"))
                etNmPengarang.setText(getStringExtra("pengarang"))
                etPenerbit.setText(getStringExtra("penerbit"))
                etThnterbit.setText("${getIntExtra("thn_terbit", 0)}")
                etTmptterbit.setText(getStringExtra("tmpt_terbit"))
                etNocetak.setText("${getIntExtra("cetakan_ke", 0)}")
                etJmlhHal.setText(getStringExtra("jmlh_hal"))
                spnKlasifikasi.setSelection(arrKlasifikasi.indexOf(getStringExtra("klasifikasi")))
                Picasso.get().load(
                    "http://$ip/uasmpl_2011500095/foto/${getStringExtra("isbn")}.jpeg"
                ).into(imgFoto)
            }
            btnSimpan.text = "Ubah"
        } else {
            etIsbn.inputType = InputType.TYPE_CLASS_NUMBER
            btnSimpan.text = "Simpan"
        }

        var foto = ""
        val ambilFoto = registerForActivityResult(GetContent()) {
            if(it != null) {
                val source = ImageDecoder.createSource(contentResolver, it)
                foto = imgToString(ImageDecoder.decodeBitmap(source))
                imgFoto.setImageURI(it)
            }
        }
        btnFoto.setOnClickListener { ambilFoto.launch("image/*") }

        btnSimpan.setOnClickListener {
            val isbn = "${etIsbn.text}"
            val JdlBuku = "${etJdlBuku.text}"
            val pengarang = "${etNmPengarang.text}"
            val penerbit = "${etPenerbit.text}"
            val thnterbit = "${etThnterbit.text}"
            val tmptterbit = "${etTmptterbit.text}"
            val nocetak = "${etNocetak.text}"
            val jmlhhal = "${etJmlhHal.text}"
            val klasifikasi = "${spnKlasifikasi.selectedItem}"
            if(btnSimpan.text == "Simpan") {
                url = "http://$ip/uasmpl_2011500095/simpan.php"
                sr = object: StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntriBuku,
                        "Data buku $it disimpan",
                        Toast.LENGTH_SHORT
                    ).show()
                    if(it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "isbn" to isbn, "jdl_buku" to JdlBuku, "pengarang" to pengarang,
                        "penerbit" to penerbit, "thn_terbit" to thnterbit,
                        "tmpt_terbit" to tmptterbit, "cetakan_ke" to nocetak,
                        "jmlh_hal" to jmlhhal,"klasifikasi" to klasifikasi, "foto" to foto
                    )
                }
            } else {
                url = "http://$ip/uasmpl_2011500095/ubah.php?isbn=$isbn"
                sr = object: StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntriBuku,
                        "Data buku [$isbn] $it diubah",
                        Toast.LENGTH_SHORT
                    ).show()
                    if(it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "isbn" to isbn, "jdl_buku" to JdlBuku, "pengarang" to pengarang,
                        "penerbit" to penerbit, "thn_terbit" to thnterbit,
                        "tmpt_terbit" to tmptterbit, "cetakan_ke" to nocetak,
                        "jmlh_hal" to jmlhhal,"klasifikasi" to klasifikasi, "foto" to foto
                    )
                }
            }
            rq = Volley.newRequestQueue(this@EntriBuku)
            rq.add(sr)
        }
    }

    private fun imgToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imgbytes = baos.toByteArray()
        return Base64.encodeToString(imgbytes, Base64.DEFAULT)
    }
}