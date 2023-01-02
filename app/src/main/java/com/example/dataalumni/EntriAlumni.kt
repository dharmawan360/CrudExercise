package com.example.dataalumni

import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.*
import com.squareup.picasso.Picasso
import androidx.activity.result.contract.ActivityResultContract.*
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.*
import androidx.annotation.RequiresApi
import java.util.*
import android.app.DatePickerDialog
import android.text.InputType
import com.android.volley.*
import com.android.volley.toolbox.*
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts

class EntriAlumni : AppCompatActivity() {
    private lateinit var url: String
    private lateinit var sr: StringRequest
    private lateinit var rq: RequestQueue

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entri_alumni)


        val modeEdit = intent.hasExtra("nim")

        title = "${if (!modeEdit) "Tambah" else "ubah"} Data Alumi"

        val etNim = findViewById<EditText>(R.id.etNim)
        val etNmAlumni = findViewById<EditText>(R.id.etNmAlumni)
        val spnProdi = findViewById<Spinner>(R.id.spnProdi)
        val etTmptLahir = findViewById<EditText>(R.id.etTmptLahir)
        val etTglLahir = findViewById<EditText>(R.id.etTglLahir)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)
        val etNoHp = findViewById<EditText>(R.id.etNoHp)
        val etThnLulus = findViewById<EditText>(R.id.etThnLulus)
        val btnFoto = findViewById<Button>(R.id.btnFoto)
        val imgFoto = findViewById<ImageView>(R.id.imgFoto)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        val arrProdi = arrayOf(
            "Teknik Informatika", "Sistem Informasi", "Manajemen Informatika",
            "KOmputerisasi Akuntansi", "Bisnis Digital"
        )
        spnProdi.adapter = ArrayAdapter(
            this@EntriAlumni,
            android.R.layout.simple_spinner_dropdown_item,
            arrProdi
        )
        if (modeEdit) {
            etNim.inputType = InputType.TYPE_NULL
            with(intent) {
                etNim.setText(getStringExtra("nim"))
                etNmAlumni.setText(getStringExtra("nm_alumni"))
                spnProdi.setSelection(arrProdi.indexOf(getStringExtra("prodi")))
                etTmptLahir.setText(getStringExtra("tmpt_lahir"))
                etTglLahir.setText(getStringExtra("tgl_lahir"))
                etAlamat.setText(getStringExtra("alamat"))
                etNoHp.setText(getStringExtra("no_hp"))
                etThnLulus.setText("${getIntExtra("thn_lulus", 0)}")
                Picasso.get().load(
                    "http://$ip/latihan_crud/foto/${getStringExtra("nim")}.jpeg"
                ).into(imgFoto)
            }
            btnSimpan.text = "Ubah"
        } else {
            etNim.inputType = InputType.TYPE_CLASS_NUMBER
            btnSimpan.text = "Simpan"
        }
        etTglLahir.setOnFocusChangeListener() { _, hasFocus ->
            if (hasFocus) {
                val kalender = Calendar.getInstance()
                val dpd = DatePickerDialog(
                    this@EntriAlumni,
                    { _, y, m, d ->
                        val bln = String.format("%02d", m + 1)
                        val tgl = String.format("%02d", d)
                        etTglLahir.setText("$y-$bln-$tgl")
                    }, kalender[Calendar.YEAR], kalender[Calendar.MONTH],
                    kalender[Calendar.DAY_OF_MONTH]
                )
                dpd.show()
            }
        }

        var foto = ""
        val ambilFoto = registerForActivityResult(ActivityResultContracts.GetContent()){
            if(it !=null){
                val source = ImageDecoder.createSource(contentResolver, it)
                foto = imgToString(ImageDecoder.decodeBitmap(source))
                imgFoto.setImageURI(it)
            }
        }
        btnFoto.setOnClickListener { ambilFoto.launch("image/*") }

        btnSimpan.setOnClickListener {
            val nim = "${etNim.text}"
            val nmAlumni = "${etNmAlumni.text}"
            val prodi = "${spnProdi.selectedItem}"
            val tmptLahir = "${etTmptLahir.text}"
            val tglLahir = "${etTglLahir.text}"
            val alamat = "${etAlamat.text}"
            val noHp = "${etNoHp.text}"
            val thnLulus = "${etThnLulus.text}"
            if (btnSimpan.text == "Simpan") {
                url = "http://$ip/latihan_crud/simpan.php"
                sr = object : StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntriAlumni,
                        "Data Alumni $it disimpan",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "nim" to nim, "nm_alumni" to nmAlumni, "prodi" to prodi,
                        "tmpt_lahir" to tmptLahir, "tgl_lahir" to tglLahir,
                        "alamat" to alamat, "no_hp" to noHp,
                        "thn_lulus" to thnLulus, "foto" to foto
                    )
                }
            } else {
                url = "http://$ip/latihan_crud/ubah.php?nim=$nim"
                sr = object : StringRequest(Method.POST, url, {
                    Toast.makeText(
                        this@EntriAlumni,
                        "Data alumni [$nim] $it diubah",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (it == "berhasil") finish()
                }, null) {
                    override fun getParams() = mutableMapOf(
                        "nim" to nim, "nm_alumni" to nmAlumni, "prodi" to prodi,
                        "tmpt_lahir" to tmptLahir, "tgl_lahir" to tglLahir,
                        "alamat" to alamat, "no_hp" to noHp,
                        "thn_lulus" to thnLulus, "foto" to foto
                    )
                }
            }
            rq = Volley.newRequestQueue(this@EntriAlumni)
            rq.add(sr)
        }
    }
    private fun imgToString(bitmap: Bitmap):String{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imgbytes = baos.toByteArray()
        return Base64.encodeToString(imgbytes, Base64.DEFAULT)
    }

}