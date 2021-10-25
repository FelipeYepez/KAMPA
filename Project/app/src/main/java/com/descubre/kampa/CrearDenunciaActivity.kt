package com.descubre.kampa

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.descubre.kampa.models.Denuncia
import com.descubre.kampa.models.Sitio
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.io.IOException


/** * @author RECON
 *
 *  Actividad para crear denuncias de mal uso desde el rol de usuario,
 *  añadiendo descripción y una imagen tomada con la cámara o eligiendo
 *  del carrete.
 *
 *  @version 1.0
 */

class CrearDenunciaActivity : AppCompatActivity() {
    private lateinit var imagen: ImageView
    private lateinit var uploadImageBtn: Button
    private var selectedImage: Bitmap? = null
    private var permissionBool: Boolean = false
    private lateinit var enviarDenuncia: Button
    private lateinit var descripcion: EditText
    private lateinit var tomarImagenButton: Button
    private lateinit var sitio: Sitio
    private lateinit var title: TextView

    /**
     * Se llama cuando la actividad se crea; obtiene el sitio de la denuncia
     * de los extras, inicializa componentes y listeners.
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_denuncia)

        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("sitio") as Sitio
        } else {
            savedInstanceState.getSerializable("sitio") as Sitio
        }

        initializeComponents()
        title.text = sitio.nombre

        initializeListeners()
        enviarDenuncia()
    }

    /**
     * Función que inicializa los componentes, buscándolos en la view
     * con su respectivo id.
     */
    private fun initializeComponents() {
        title = findViewById(R.id.title)
        imagen = findViewById(R.id.imagenDenuncia)
        uploadImageBtn = findViewById(R.id.subirImagenBtn)
        enviarDenuncia = findViewById(R.id.enviarDenunciaBtn)
        descripcion = findViewById(R.id.descripcion)
        tomarImagenButton = findViewById(R.id.tomarFotoBtn)
    }

    /**
     * Función que inicializa los listeners del botón de elegir foto desde
     * la galería y tomar foto.
     */
    private fun initializeListeners() {
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
        tomarImagenButton.setOnClickListener{
            startForResult.launch(Intent(this, com.descubre.kampa.TakePictureActivity::class.java))

        }
    }

    /**
     * Función para enviar denuncia, inicializa el listener del botón de enviar,
     * crea la denuncia y la guarda en la base de datos.
     */
    private fun enviarDenuncia() {
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
            denuncia.idUsuario = ParseUser.getCurrentUser()

            denuncia.saveInBackground { e ->
                if (e == null) {
                    Snackbar.make(findViewById(android.R.id.content), "Se creó correctamente la denuncia", Snackbar.LENGTH_SHORT).show()
                } else {
                    Log.e("e", "Failed to save denuncia", e)
                }
            }

            finish()

        }
    }

    /**
     * Función para revisar si el usuario ya accedió a dar permiso para usar las imagenes de
     * la galería.
     *
     * @param permission  permiso que vav a verificar si se tiene el el Manifest
     * @param requestCode
     */
    private fun checkPermissions(permission: String, requestCode: Int){
        if(ContextCompat.checkSelfPermission(this@CrearDenunciaActivity, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this@CrearDenunciaActivity, arrayOf(permission), requestCode)
            permissionBool = false
        }
        else{
            permissionBool = true
        }
    }

    /**
     * Función para convertir una foto en formato Uri a Bitmap
     *
     * @param photoUri  uri de la foto a convertir
     * @return image   imagen en formato de Bitmap
     */
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