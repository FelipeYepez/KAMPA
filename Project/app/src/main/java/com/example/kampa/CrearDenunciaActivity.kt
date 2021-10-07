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
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class CrearDenunciaActivity : AppCompatActivity() {
    private lateinit var imagen: ImageView
    private lateinit var uploadImageBtn: Button
    private var selectedImage: Bitmap? = null
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
                selectedImage = bitmapFromUri(intent?.data)
                imagen!!.setImageBitmap(selectedImage)
            }
        }

        uploadImageBtn.setOnClickListener{
            checkPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE, 1)
            Log.d("ImageBtn", permissionBool.toString())
            if(permissionBool){
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT

                startForResult.launch(Intent.createChooser(intent, "Select Picture"))
            }
            else{
                Snackbar.make(findViewById(android.R.id.content), "KAMPA no tiene acceso a Galería", Snackbar.LENGTH_SHORT).show()
            }
        }


        val tomarImagenButton: Button = findViewById(R.id.tomarFotoBtn)
        tomarImagenButton.setOnClickListener{
            startForResult.launch(Intent(this, UploadImageActivity::class.java))

        }


        enviarDenuncia = findViewById(R.id.enviarDenunciaBtn)
        descripcion = findViewById(R.id.descripcion)
        Log.d("sitio", sitio.toString())

        enviarDenuncia.setOnClickListener{
            var denuncia = Denuncia()

            denuncia.idSitio = sitio
            denuncia.descripcion = descripcion.text.toString()

            if(selectedImage != null){
                val stream = ByteArrayOutputStream()
                selectedImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                denuncia.fotos = ParseFile("Denuncia" + (0..1000000000).random().toString() + "-" +".png" , byteArray)
            }


            denuncia.estado = "sinResolver"
            //Log.d("usuario", ParseUser.getCurrentUser().toString())
            //denuncia.idUsuario = ParseUser.getCurrentUser()
            //denuncia.put("idUsuario", ParseUser.getCurrentUser().objectId)

            denuncia.saveInBackground { e ->
                if (e == null) {
                    Log.d("denuncia", "siu")
                    Snackbar.make(findViewById(android.R.id.content), "Se creó correctamente la denuncia", Snackbar.LENGTH_SHORT).show()
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

    fun bitmapFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source: ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver, photoUri!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }
}