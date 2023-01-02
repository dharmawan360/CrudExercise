package com.example.dataalumni

import android.view.*
import android.widget.*
import android.content.*
import android.graphics.Color
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import com.android.volley.*

class AdapterAlumni (val listAlumni: ArrayList<Alumni>, val context: Context):
    RecyclerView.Adapter<AdapterAlumni.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_alumni, parent,
            false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val la = listAlumni[position]
        val nim = la.nim
        val nmAlumni = la.nmAlumni
        val prodi = la.prodi
        val tmptLahir = la.tmptLahir
        val tglLahir = la.tglLahir
        val alamat = la.alamat
        val noHp = la.noHp
        val thnLulus = la.thnLulus
        val warnaprodi = Color.parseColor(when(prodi) {
            "Teknik Informatika" -> "#FF7f00"
            "Sistem Informatik" -> "#0000F"
            "Manajemen Informatika" -> "#FFFF00"
            "Komputerisasi Akuntansi" -> "483621"
            else -> "#800000"
        })
        val dataAlumni = """
            NIM: $nim
            Nama Alumni: $nmAlumni
            Program Studi: $prodi
            Tempat dan Tanggal Lahir: $tmptLahir, ${tglLahir.tglString()}
            Alamat: $alamat
            Nomor HP: $noHp
            Tahun Lulus: $thnLulus
        """.trimIndent()
        val baseUrl = "http://$ip/latihan_crud/foto/"

        with(holder) {
            cvAlumni.setCardBackgroundColor(warnaprodi)
            tvNmAlumni.text = nmAlumni
            tvNmAlumni.setTextColor(if(prodi != "Manajemen Informatika") Color.WHITE else Color.BLACK)
            tvProdi.text =  "$prodi $thnLulus"
            tvProdi.setTextColor(if(prodi != "Manajemen Informatika") Color.WHITE else Color.BLACK)
            Picasso.get().load("$baseUrl$nim.jpeg").fit().into(imgFoto)
            itemView.setOnClickListener {
                val alb = AlertDialog.Builder(context)
                with(alb) {
                    setCancelable(false)
                    setTitle("Data Alumni")
                    setMessage(dataAlumni)
                    setPositiveButton("Ubah"){_, _ ->
                        val i = Intent(context, EntriAlumni::class.java)
                        with(i){
                            putExtra("nim",nim)
                            putExtra("nm_alumni",nmAlumni)
                            putExtra("prodi", prodi)
                            putExtra("tmpt_lahir", tmptLahir)
                            putExtra("tgl_lahir", tglLahir)
                            putExtra("alamat" , alamat)
                            putExtra("no_hp", noHp)
                            putExtra("thn_lulus",thnLulus)
                        }
                        context.startActivity(i)
                    }
                    setNegativeButton("Hapus"){ _, _ ->
                        val url = "http://$ip/latihan_crud/hapus.php?nim=$nim"
                        val sr = StringRequest(Request.Method.GET, url, {
                            Toast.makeText(
                                context,
                                "Data Alumni [$nim] $it dihapus",
                                Toast.LENGTH_SHORT
                            ).show()
                            if(it == "berhasil"){
                                listAlumni.removeAt(position)
                            }
                        },null)
                        val rq = Volley.newRequestQueue(context)
                        rq.add(sr)
                    }
                    setNeutralButton("Tutup", null)
                    create().show()
                }
            }
        }
    }
    override fun getItemCount() = listAlumni.size

    private fun String.tglString(): String  {
        val nmBulan = listOf(
            "Jamuari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus",
            "September", "Oktober", "November", "Desember"

        )
        val tanggal = TextUtils.split(this, "-")
        val thn = tanggal[0]
        val bln = nmBulan[tanggal[1].toInt() - 1]
        val tgl = tanggal[2].toInt()
        return "$tgl $bln $thn"
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cvAlumni = itemView.findViewById<CardView>(R.id.cvAlumni)
        val imgFoto = itemView.findViewById<ImageView>(R.id.imgFoto)
        val tvNmAlumni = itemView.findViewById<TextView>(R.id.tvNmAlumni)
        val tvProdi = itemView.findViewById<TextView>(R.id.tvProdi)
    }

}