package com.example.kampa

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kampa.models.Denuncia
import com.example.kampa.models.Sitio
import java.util.jar.Manifest
import com.parse.ParseUser




class CrearDenunciaActivity : AppCompatActivity() {
    private lateinit var imagen: ImageView
    private lateinit var uploadImageBtn: Button
    private lateinit var selectedImage: Uri
    private var permissionBool: Boolean = false
    private lateinit var enviarDenuncia: Button
    private lateinit var descripcion: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_denuncia)
        val sitio: Sitio?
        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("sitio") as Sitio
        } else {
            savedInstanceState.getSerializable("sitio") as Sitio
        }
        var title: TextView = findViewById(R.id.title)
        title.text = sitio.nombre

        // Seleccionar imagen
        imagen = findViewById(R.id.imagenDenuncia)
        uploadImageBtn = findViewById(R.id.subirImagenBtn)

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                imagen.setImageURI(uri)
                selectedImage = uri
            }
        )

        uploadImageBtn.setOnClickListener{
            checkPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, 1)
            if(permissionBool){
                getAction.launch("image/*")
            }
            else{
                Toast.makeText(this, "KAMPA no tiene acceso a Galería", Toast.LENGTH_LONG).show()
            }
        }

        enviarDenuncia = findViewById(R.id.enviarDenunciaBtn)
        descripcion = findViewById(R.id.descripcion)

        enviarDenuncia.setOnClickListener{
            val denuncia = Denuncia()
            //denuncia.idSitio = sitio.
            denuncia.descripcion = descripcion.text.toString()
            //denuncia.fotos = uri
            denuncia.estado = "sinResolver"
            denuncia.saveInBackground()

        }
    }

    private fun checkPermissions(permission: String, requestCode: Int){
        if(ContextCompat.checkSelfPermission(this@CrearDenunciaActivity, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this@CrearDenunciaActivity, arrayOf(permission), requestCode)
            permissionBool = false
        }
        else{
            permissionBool = true
        }
    }
}