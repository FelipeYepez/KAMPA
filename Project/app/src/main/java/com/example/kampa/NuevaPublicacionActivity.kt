package com.example.kampa

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.kampa.models.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.parse.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class NuevaPublicacionActivity : AppCompatActivity() {
    private val TAG = "NuevaPublicacion"
    private var imagenPublicacion: ImageView? = null
    private var selectedBitmapImage: Bitmap? = null
    private var selectedUriImage: Uri? = null
    private lateinit var tags: Spinner
    private var selectedTag :Int = 0
    private var listTags = mutableListOf<Tag>()
    lateinit private var chips : ChipGroup
    private var addedChips = mutableListOf<Int>()





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
        tags =findViewById(R.id.tags)
        desplegarTags()
        chips = findViewById(R.id.chipGroupTag)

        var botonTag:Button = findViewById(R.id.botonTag)
        botonTag.setOnClickListener{
            Log.d(TAG, "entro a creaChipTag")
            creaChipTag()
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

            val progressDialog = ProgressDialog(this)

            if(selectedUriImage != null || selectedBitmapImage != null){
                publicacion.saveInBackground { e ->
                    progressDialog.show()
                    if (e == null) {
                        Log.d(TAG, "saved publicacion")
                        for (el in addedChips){
                            Log.d(TAG,el.toString())
                            var publicacionTag = PublicacionTags()
                            publicacionTag.idTag = listTags[el]
                            publicacionTag.idPublicacion = publicacion
                            publicacionTag.saveInBackground { err ->
                                if (err == null) {
                                    Log.d(TAG, "saved tag")


                                    finish()
                                } else {
                                    Log.d(TAG, err.toString())
                                }
                            }
                        }
                    } else {
                        Log.d(TAG, e.toString())
                    }
                }

            }
            else{
                Toast.makeText(this, "Llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            }




        }
    }
    fun desplegarTags(){
        val query: ParseQuery<Tag> = ParseQuery.getQuery(Tag::class.java)
        val adapter: ArrayAdapter<String>  =  ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item)
        query.findInBackground { itemList, e ->
            if (e == null) {
                var id = 0
                for (el in itemList ) {
                    listTags.add(el)
                    adapter.add(el.descripcion)
                    id = id + 1
                }

                tags.adapter = adapter
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }
    }
    //Añade el tag actualmente selecionado al chipgroup
    fun creaChipTag(){
        Log.d(TAG, "entro a creaChipTag")
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