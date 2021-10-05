package com.example.kampa

import android.R.id
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kampa.models.Denuncia
import com.example.kampa.models.Sitio
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File
import android.widget.Toast
import com.parse.SaveCallback
import android.R.id.message
import android.app.Activity
import android.content.Intent
import com.parse.ParseObject


class CrearDenunciaActivity : AppCompatActivity() {
    private lateinit var imagen: ImageView
    private lateinit var uploadImageBtn: Button
    private var selectedImage: Uri? = null
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

        var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                selectedImage = intent?.data
                imagen!!.setImageURI(selectedImage)
            }
        }

        uploadImageBtn.setOnClickListener{
            checkPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, 1)
            Log.d("ImageBtn", permissionBool.toString())
            if(permissionBool){
                val i = Intent()
                i.type = "image/*"
                i.action = Intent.ACTION_GET_CONTENT

                startForResult.launch(Intent.createChooser(i, "Select Picture"))
            }
            else{
                Snackbar.make(findViewById(android.R.id.content), "KAMPA no tiene acceso a Galería", Snackbar.LENGTH_SHORT).show()
            }
        }


        enviarDenuncia = findViewById(R.id.enviarDenunciaBtn)
        descripcion = findViewById(R.id.descripcion)
        Log.d("sitio", sitio.toString())

        enviarDenuncia.setOnClickListener{
            var denuncia = Denuncia()

            denuncia.idSitio = sitio
            denuncia.descripcion = descripcion.text.toString()

            //if(selectedImage != null){
                //denuncia.foto = ParseFile(File(selectedImage?.path))
            //}

            denuncia.estado = "sinResolver"
            //denuncia.put("idUsuario", ParseUser.getCurrentUser().objectId)

            denuncia.saveInBackground { e ->
                if (e == null) {
                    Snackbar.make(
                        findViewById(android.R.id.content), "Se creó correctamente la denuncia",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("e", "Failed to save denuncia", e)
                }
            }

            Log.d("enviar", "click")

           val i = Intent(this, SitioActivity::class.java)
           i.putExtra("sitio", sitio)
           startActivity(i)
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