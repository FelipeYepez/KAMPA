package com.example.kampa

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kampa.models.Publicacion
import com.example.kampa.models.Sitio
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class NuevaPublicacion : AppCompatActivity() {
    val TAG = "NuevaPublicacion"
    var imagenPublicacion: ImageView? = null
    var selectedBitmapImage: Bitmap? = null
    var selectedUriImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_nueva_publicacion)

        val sitio: Sitio?
        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("sitio") as Sitio
        } else {
            savedInstanceState.getSerializable("sitio") as Sitio
        }

        var publicacion = Publicacion()
        imagenPublicacion = findViewById(R.id.imagenPublicacion)

        var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val intent = result.data

                if (intent?.data != null && "content".equals(intent!!.data?.getScheme())) {
                    selectedBitmapImage = bitmapFromUri(intent?.data)
                    imagenPublicacion!!.setImageBitmap(selectedBitmapImage)
                }
                else{
                    selectedUriImage = intent?.data
                    imagenPublicacion!!.setImageURI(selectedUriImage)
                }
            }
        }

        val capturarImagenButton: Button = findViewById(R.id.capturarImagenButton)
        capturarImagenButton.setOnClickListener{

            startForResult.launch(Intent(this, UploadImageActivity::class.java))
        }

        val galeriaImagenButton: Button = findViewById(R.id.galeriaImagenButton)
        galeriaImagenButton.setOnClickListener{
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT

            startForResult.launch(Intent.createChooser(i, "Select Picture"))
        }

        val submitButtonPublicacion: Button = findViewById(R.id.submitButtonSitio)
        submitButtonPublicacion.setOnClickListener{

            if(selectedBitmapImage != null){
                val stream = ByteArrayOutputStream()
                selectedBitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                publicacion.foto = ParseFile("Publicacion" + (0..1000000000).random().toString() + "-" +".png" , byteArray)
            }

            else if(selectedUriImage != null){
                publicacion.foto = ParseFile(File(selectedUriImage?.path))
            }

            val inputDescripcion: EditText = findViewById(R.id.inputDescripcion)
            publicacion.descripcion = inputDescripcion.text.toString()

            publicacion.idSitio = sitio
            publicacion.idUsuario = ParseUser.getCurrentUser()

            if(selectedUriImage != null || selectedBitmapImage != null){
                publicacion.saveInBackground { e ->
                    if (e == null) {
                        Log.d(TAG, "saved")
                    } else {
                        Log.d(TAG, e.toString())
                    }
                }
                finish()
            }
            else{
                Toast.makeText(this, "Llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            }

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