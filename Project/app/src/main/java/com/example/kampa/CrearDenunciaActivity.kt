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
import android.content.Intent


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
            Log.d("enviar", "click")
            checkPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, 1)
            Log.d("ImageBtn", permissionBool.toString())
            if(permissionBool){
                getAction.launch("image/*")
            }
            else{
                Snackbar.make(findViewById(android.R.id.content), "KAMPA no tiene acceso a Galería", Snackbar.LENGTH_SHORT).show()
            }
        }

        enviarDenuncia = findViewById(R.id.enviarDenunciaBtn)
        descripcion = findViewById(R.id.descripcion)
        Log.d("sitio", sitio.toString())

        enviarDenuncia.setOnClickListener{
            Log.d("enviar", "click")
            val denuncia = Denuncia()

            denuncia.idSitio = sitio
            denuncia.descripcion = descripcion.text.toString()
            //val photoFile = getPhotoFileUri(selectedImage.toString())
            //val parseFile = ParseFile(photoFile)
            //denuncia.fotos = parseFile
            denuncia.estado = "sinResolver"
            //denuncia.idUsuario = ParseUser.getCurrentUser()
            /*
            denuncia.saveInBackground(SaveCallback { e ->
                if (e == null) {
                    Toast.makeText(
                        this@CrearDenunciaActivity, "Successfully created message on Parse",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("e", "Failed to save message", e)
                }
            })
            */
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

    fun getPhotoFileUri(fileName: String): File? {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Return the file target for the photo based on filename
        //if (mediaStorageDir != null) {
        return File(mediaStorageDir?.path + File.separator + fileName)
        //}
    }
}