package com.example.kampa

import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.kampa.models.Denuncia
import com.parse.GetDataCallback
import com.parse.ParseFile
import kotlinx.android.synthetic.main.activity_login.*

class DenunciaActivity() : AppCompatActivity() {
    private lateinit var denuncia: Denuncia
    private lateinit var sitioName: TextView
    private lateinit var imagenDenuncia: ImageView
    private lateinit var descripcionDenuncia: TextView
    private lateinit var btnInvalida: Button
    private lateinit var btnProcesada: Button

    val TAG = "DenunciaActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_denuncia)

        denuncia = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("denuncia") as Denuncia
        }
        else {
            savedInstanceState.getSerializable("denuncia") as Denuncia
        }

        initializeComponents()
        initializeListeners()

        sitioName.text = denuncia.idSitio?.nombre
        loadImages(denuncia.fotos, imagenDenuncia)
        descripcionDenuncia.text = denuncia.descripcion


    }

    private fun loadImages(foto: ParseFile?, imgView: ImageView) {
        if (foto != null) {
            foto.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    imgView.setImageBitmap(bmp)
                }
                else {
                    imgView.setImageResource(R.drawable.esencia_patrimonio)
                }
            })

        }
    }

    fun initializeComponents() {
        sitioName = findViewById(R.id.sitioName)
        imagenDenuncia = findViewById(R.id.imagenDenuncia)
        descripcionDenuncia = findViewById(R.id.descripcionDenuncia)
        btnInvalida = findViewById(R.id.btnInvalida)
        btnProcesada = findViewById(R.id.btnProcesada)

        if(denuncia.estado != "sinResolver") {
            btnInvalida.visibility = View.INVISIBLE
            btnProcesada.visibility = View.INVISIBLE
        }
        else {
            btnInvalida.visibility = View.VISIBLE
            btnProcesada.visibility = View.VISIBLE
        }
    }

    fun initializeListeners() {
        btnInvalida.setOnClickListener {
            denuncia.estado = "invalida"
            denuncia.saveInBackground()
            Toast.makeText(this, "Denuncia Inválida", Toast.LENGTH_LONG).show()
            finish()
        }
        btnProcesada.setOnClickListener {
            Toast.makeText(this, "Procesada", Toast.LENGTH_LONG).show()
        }

    }


}