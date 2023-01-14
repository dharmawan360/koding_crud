package com.example.uas_mpl_2011500095

import android.view.*
import android.widget.*
import android.content.*
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.android.volley.Request
import com.android.volley.toolbox.*

class AdapterBuku(val listBuku: ArrayList<Buku>, val context: Context):
    RecyclerView.Adapter<AdapterBuku.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_buku, parent,
            false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lb = listBuku[position]
        val isbn = lb.isbn
        val jdlBuku = lb.jdlBuku
        val nmPengarang = lb.nmPengarang
        val penerbit = lb.penerbit
        val thnTerbit = lb.thnTerbit
        val tmptTerbit = lb.tmptTerbit
        val noCetak = lb.noCetak
        val jmlhHal = lb.jmlhHal
        val klasifikasi = lb.klasifikasi
        val warnaBuku = Color.parseColor(when(klasifikasi) {
            "Umum" -> "#FF7F00"
            "Filsafat" -> "#0000FF"
            "Agama" -> "#FFFF00"
            "Sosial" -> "#4B3621"
            "Bahasa" -> "#FFFF00"
            "Ilmu Murni/Sains" -> "#4B3621"
            "Teknologi" -> "#FFFF00"
            "Seni" -> "#4B3621"
            "Sastra" -> "#4B3621"
            else -> "#800000"
        })
        val databuku = """
            ISBN: $isbn
            Judul Buku: $jdlBuku
            Pengarang: $nmPengarang
	        Penerbit: $penerbit
	        Tahun Terbit = $thnTerbit
	        Tempat Terbit = $tmptTerbit    
            Cetakan Ke-: $noCetak
            Jumlah Halaman: $jmlhHal
            Klasifikasi: $klasifikasi
        """.trimIndent()
        val baseUrl = "http://$ip/uasmpl_2011500095/foto/"
        with(holder) {
            cvBuku.setCardBackgroundColor(warnaBuku)
            tvJdlBuku.text = jdlBuku
            tvJdlBuku.setTextColor(if(klasifikasi != "Umum") Color.WHITE else Color.BLACK)
            tvNmPengarang.text = "$nmPengarang $noCetak"
            tvNmPengarang.setTextColor(if(klasifikasi != "Umum") Color.WHITE else Color.BLACK)
            Picasso.get().load("$baseUrl$isbn.jpeg").fit().into(imgBuku)
            itemView.setOnClickListener {
                val alb = AlertDialog.Builder(context)
                with(alb) {
                    setCancelable(false)
                    setTitle("Data buku")
                    setMessage(databuku)
                    setPositiveButton("Ubah") { _, _ ->
                        val i = Intent(context, EntriBuku::class.java)
                        with(i) {
                            putExtra("isbn", isbn)
                            putExtra("jdl_buku", jdlBuku)
                            putExtra("pengarang", nmPengarang)
                            putExtra("penerbit", penerbit)
                            putExtra("thn_terbit", thnTerbit)
                            putExtra("tmpt_terbit", tmptTerbit)
                            putExtra("cetakan_ke", noCetak)
                            putExtra("jmlh_hal", jmlhHal)
                            putExtra("klasifikasi", klasifikasi)
                        }
                        context.startActivity(i)
                    }
                    setNegativeButton("Hapus") { _, _ ->
                        val url = "http://$ip/uasmpl_2011500095/hapus.php?isbn=$isbn"
                        val sr = StringRequest(Request.Method.GET, url, {
                            Toast.makeText(
                                context,
                                "Data buku [$isbn] $it dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            if(it == "berhasil") {
                                listBuku.removeAt(position)
                                notifyItemRemoved(position)
                            }
                        }, null)
                        val rq = Volley.newRequestQueue(context)
                        rq.add(sr)
                    }
                    setNeutralButton("Tutup", null)
                    create().show()
                }
            }
        }
    }

    override fun getItemCount() = listBuku.size



    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cvBuku = itemView.findViewById<CardView>(R.id.cvBuku)
        val imgBuku = itemView.findViewById<ImageView>(R.id.imgBuku)
        val tvJdlBuku = itemView.findViewById<TextView>(R.id.tvJdlBuku)
        val tvNmPengarang = itemView.findViewById<TextView>(R.id.tvNmPengarang)
    }
}