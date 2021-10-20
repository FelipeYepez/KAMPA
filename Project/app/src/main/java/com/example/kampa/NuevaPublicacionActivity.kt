package com.example.kampa

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
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
import com.example.kampa.models.Publicacion
import com.example.kampa.models.PublicacionTags
import com.example.kampa.models.Sitio
import com.example.kampa.models.Tag
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * @author RECON
 * Actividad para añadir una nueva publicación relacionada a un sitio en la base de datos
 */
class NuevaPublicacionActivity : AppCompatActivity() {
    private val TAG = "NuevaPublicacion"
    private var imagenPublicacion: ImageView? = null
    private var selectedBitmapImage: Bitmap? = null
    private var selectedUriImage: Uri? = null
    private var inputDescripcion: EditText? = null
    private var publicacion = Publicacion()
    private var sitio =  Sitio()
    private lateinit var tags: Spinner
    private var listTags = mutableListOf<Tag>()
    lateinit private var chips : ChipGroup
    private var addedChips = mutableListOf<Int>()

    /**
     * Utiliza intents para seleccionar una imagen de la galería o capturada por el usuario.
     * Se utiliza un onClickListeners para enviar los atributos de la nueva publicación en la base de datos.
     * @param savedInstanceState para que la actividad pueda restaurarse a un estado anterior
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_nueva_publicacion)
        imagenPublicacion = findViewById(R.id.imagenPublicacion)
        chips = findViewById(R.id.chipGroupTag)
        tags =findViewById(R.id.tags)

        //Obtiene el sitio de la publicación que se va a registrar
        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("sitio") as Sitio
        } else {
            savedInstanceState.getSerializable("sitio") as Sitio
        }

        //Despliega los tags existentes en la base de datos
        displayTags()

        //ClickListener para añadir tags a la publicación
        var botonTag:Button = findViewById(R.id.botonTag)
        botonTag.setOnClickListener{
            creaChipTag()
        }

        //Manejo del resultado de un intent, se recibe la imagen y se guarda en un ImageView para visualizarla
        var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent?.data != null && "content".equals(intent!!.data?.getScheme())) {
                    selectedBitmapImage = bitmapFromUri(intent?.data)
                    imagenPublicacion!!.setImageBitmap(selectedBitmapImage)
                } else{
                    selectedUriImage = intent?.data
                    imagenPublicacion!!.setImageURI(selectedUriImage)
                }
            }
        }

        //ClickListener para ir al intent TakePicture si el usuario quiere capturar la imagen él mismo
        val capturarImagenButton: Button = findViewById(R.id.capturarImagenButton)
        capturarImagenButton.setOnClickListener{
            startForResult.launch(Intent(this, TakePictureActivity::class.java))
        }

        //ClickListener para seleccionar una imagen de la galería
        val galeriaImagenButton: Button = findViewById(R.id.galeriaImagenButton)
        galeriaImagenButton.setOnClickListener{
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startForResult.launch(Intent.createChooser(i, "Select Picture"))
        }

        //ClickListener para guardar los cambios y registrar la publicación en la base de datos
        val submitButtonPublicacion: Button = findViewById(R.id.submitButtonSitio)
        submitButtonPublicacion.setOnClickListener{
            inputDescripcion = findViewById(R.id.inputDescripcion)
            savePublicacion()
        }
    }

    /**
     * Despliega los tags de la base de datos utilizando un adaptador
     */
    private fun displayTags(){
        val query: ParseQuery<Tag> = ParseQuery.getQuery(Tag::class.java)
        val adapter: ArrayAdapter<String>  =  ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item)
        query.findInBackground { itemList, e ->
            if (e == null) {
                var id = 0
                for (el in itemList ) {
                    listTags.add(el)
                    adapter.add(el.descripcion)
                    id += 1
                }
                tags.adapter = adapter
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }
    }

    /**
     * Guarda la publicación creada en la base de datos
     */
    private fun savePublicacion(){
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        publicacion.descripcion = inputDescripcion?.text.toString()
        publicacion.idSitio = sitio
        publicacion.idUsuario = ParseUser.getCurrentUser()

        if(selectedBitmapImage != null){
            val stream = ByteArrayOutputStream()
            selectedBitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            publicacion.foto = ParseFile("Publicacion" + (0..1000000000).random().toString() + "-" +".png" , byteArray)
        }
        else if(selectedUriImage != null){
            publicacion.foto = ParseFile(File(selectedUriImage?.path))
        }

        if(selectedUriImage != null || selectedBitmapImage != null){
            publicacion.saveInBackground { e ->
                if (e == null) {
                    for (el in addedChips){
                        saveTag(el)
                    }
                    Toast.makeText(this, "Publicación guardada exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else if(e != null) {
                    Log.d(TAG, e.toString())
                }
            }
        }
        else{
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
        progressDialog.hide()
    }

    /**
     * Guarda un tag y su respectiva publicación en la tabla PublicacionTags
     * @param tag elemento que se guardará en la base de datos
     */
    private fun saveTag(tag:Int){
        var publicacionTag = PublicacionTags()
        publicacionTag.idTag = listTags[tag]
        publicacionTag.idPublicacion = publicacion
        publicacionTag.saveInBackground { err ->
            if (err != null) {
                Log.d(TAG, err.toString())
            }
        }
    }

    /**
     * Añade el tag actualmente selecionado un chipGroup
     */
    private fun creaChipTag(){
        var chipTag:Chip = layoutInflater.inflate(R.layout.chip_item,null,false) as Chip
        chipTag.setOnCloseIconClickListener { view ->
            chips.removeView(view)
        }
        chipTag.text = tags.selectedItem.toString()

        val found = addedChips.contains(tags.selectedItemId.toInt())

        // validar que el tag no haya sido seleccionado antes y que ya haya seleccionado algun tag
        if(!found){
            addedChips.add(tags.selectedItemId.toInt())
            chips.addView(chipTag)
        }
    }

    /**
     * Convierte una imagen de Uri a Bitmap
     * @param photoUri la imagen en formato Uri
     * @return image la imagen en formato Bitmap
     */
    private fun bitmapFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // checa la versión del dispositivo
            image = if (Build.VERSION.SDK_INT > 27) {
                // para versiones nuevas de android
                val source: ImageDecoder.Source = ImageDecoder.createSource(this.contentResolver, photoUri!!)
                ImageDecoder.decodeBitmap(source)
            } else {
                // para versiones antiguas de android
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }
}