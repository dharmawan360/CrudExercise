package com.example.dataalumni

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.*
import com.android.volley.toolbox.*
import java.net.*
import androidx.recyclerview.widget.*
import org.json.*

class MainActivity : AppCompatActivity() {
    private lateinit var url:String
    private lateinit var sr: StringRequest
    private lateinit var rq: RequestQueue

    private lateinit var rvAlumni: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title="E-Book Alumni Universitas XYZ"

        rvAlumni = findViewById(R.id.rvAlumni)
        val btnTambah = findViewById<Button>(R.id.btnTambah)

        rvAlumni.setHasFixedSize(true)
        rvAlumni.layoutManager = LinearLayoutManager(this@MainActivity)

        btnTambah.setOnClickListener{
            startActivity(Intent(this@MainActivity, EntriAlumni::class.java))
        }
    }

    private fun getDefaultGateway():String?{
        var defaultGateway:String? = null
        try{
            val enumNetworkInterface = NetworkInterface.getNetworkInterfaces()
            while(enumNetworkInterface.hasMoreElements()){
                val networkInterface = enumNetworkInterface.nextElement()
                val enumInetAddress = networkInterface.inetAddresses
                    while (enumInetAddress.hasMoreElements()){
                        val inetAddress = enumInetAddress.nextElement()
                        if (inetAddress.isSiteLocalAddress)defaultGateway = inetAddress.hostAddress
                    }
                }
        } catch (_: SocketException){
            defaultGateway = null
        }
        return  defaultGateway
}

    override fun onStart(){
    super.onStart()
    val ipSebelumnya = ip
    if(getDefaultGateway() != null) {
        try{
            for(i in 0..255){
                val kepalaIp =
                    getDefaultGateway()?.substring(0, getDefaultGateway()?.lastIndexOf(".")?: -1)
                val ipTemp = "$kepalaIp.$i"
                url = "http://$ipTemp/latihan_crud/koneksi.php"
                sr = StringRequest(Request.Method.GET, url,{
                    if (it.isNotEmpty()) {
                    ip = ipTemp
                    if(ip != ipSebelumnya){
                        Toast.makeText(
                            this@MainActivity,
                            "Terhubung ke $ip",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    }
                }, null)
                rq = Volley.newRequestQueue(this@MainActivity)
                rq.add(sr)
            }
        }catch (_: Exception){
            ip ="10.0.2.2"
        }
    }else ip ="10.0.2.2"
        tampilData()
    }

    override fun onResume() {
        super.onResume()
        tampilData()
    }
    private fun tampilData(){
        val listAlumni = arrayListOf<Alumni>()
        val adapter = AdapterAlumni(listAlumni, this@MainActivity)

        url ="http://$ip/latihan_crud/tampil.php"
        sr = StringRequest(Request.Method.GET, url,{
            try {
                val obj = JSONObject(it)
                val array = obj.getJSONArray("data")
                for(i in 0 until array.length()){
                    val ob = array.getJSONObject(i)
                    with(ob){
                        listAlumni.add(Alumni(
                            getString("nim"),
                            getString("nm_alumni"),
                            getString("prodi"),
                            getString("tmpt_lahir"),
                            getString("tgl_lahir"),
                            getString("alamat"),
                            getString("no_hp"),
                            getInt("thn_lulus"),
                        ))
                    }
                }
                rvAlumni.adapter = adapter
            }catch (_: JSONException){
                Toast.makeText(this@MainActivity, "Tidak ada data...", Toast.LENGTH_LONG).show()
            }
        }, null)
        rq = Volley.newRequestQueue(this@MainActivity)
        rq.add(sr)
    }
}

