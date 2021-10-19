package com.example.kampa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kampa.models.Sitio
import com.example.kampa.models.TipoSitio
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseQuery
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * Actividad que inicia cuando se selecciona el floating action button del mapa.
 * Crea un nuevo sitio en la base de datos.
 */
class NuevoSitioActivity : AppCompatActivity(), OnMapReadyCallback{
    val TAG = "NuevoSitio"
    lateinit var startForResultLocation : ActivityResultLauncher<Intent>
    lateinit var startForResultImage : ActivityResultLauncher<Intent>
    var inputNombre:EditText? = null
    var inputDescripcion:EditText? = null
    var inputHistoria:EditText? = null
    var inputPagina:EditText? = null
    var ubicacion:ParseGeoPoint? = null
    var tipoSitio:RadioGroup? = null
    var idCheckedButton:Int? = null
    var listTipoSitio = mutableListOf<TipoSitio>()
    var map :MapView? = null
    var gMap : GoogleMap? = null
    var imagenSitio:ImageView? = null
    var selectedBitmapImage: Bitmap? = null
    var selectedUriImage: Uri? = null
    var permission: Boolean? = null
    var currentLocation: Location? = null
    var nuevoSitio = Sitio()

    /**
     * Obtiene el permiso y ubicación para cargar el mapa.
     * Utiliza intents para seleccionar una ubicación exacta y una imagen de la galería o capturada
     * por el usuario.
     * Se utiliza un onClickListener para enviar los atributos del nuevo lugar en la base de datos.
     * @param savedInstanceState para que la actividad pueda restaurarse a un estado anterior
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_sitio)

        initMap()
        displayTipoSitio()
        imagenSitio = findViewById(R.id.imagenSitio)

        permission = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("permission") as Boolean
        } else {
            savedInstanceState.getSerializable("permission") as Boolean
        }

        currentLocation = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("currentLocation") as Location
        } else {
            savedInstanceState.getSerializable("currentLocation") as Location
        }

        startForResultImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val intent = result.data

                if (intent?.data != null && "content".equals(intent!!.data?.getScheme())) {
                    selectedBitmapImage = bitmapFromUri(intent?.data)
                    imagenSitio!!.setImageBitmap(selectedBitmapImage)
                }
                else{
                    selectedUriImage = intent?.data
                    imagenSitio!!.setImageURI(selectedUriImage)
                }
            }
        }

        startForResultLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val latLngLocation = LatLng(intent!!.getDoubleExtra("latitude",0.0),intent!!.getDoubleExtra("longitude",0.0))
                Log.d(TAG,latLngLocation.toString())
                currentLocation!!.latitude =latLngLocation.latitude
                currentLocation!!.longitude =latLngLocation.longitude
                gMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 18f))
            }
        }

        val capturarImagenButton: Button = findViewById(R.id.capturarImagenButton)
        capturarImagenButton.setOnClickListener{
            startForResultImage.launch(Intent(this, UploadImageActivity::class.java))
        }

        val galeriaImagenButton: Button = findViewById(R.id.galeriaImagenButton)
        galeriaImagenButton.setOnClickListener{
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startForResultImage.launch(Intent.createChooser(i, "Select Picture"))
        }

        val submitButtonSitio:Button = findViewById(R.id.submitButtonSitio)
        submitButtonSitio.setOnClickListener{
            inputNombre = findViewById(R.id.inputNombre)
            inputDescripcion = findViewById(R.id.inputDescripcion)
            inputHistoria = findViewById(R.id.inputHistoria)
            inputPagina = findViewById(R.id.inputPagina)
            ubicacion = ParseGeoPoint(currentLocation!!.latitude,currentLocation!!.longitude)
            tipoSitio = findViewById(R.id.radioGroupTipo)
            idCheckedButton = tipoSitio?.checkedRadioButtonId
            saveNuevoSitio()
        }
    }

    /**
     * Realiza una consulta a la base de datos para mostrar la lista de tipos de sitio que hay.
     * Los despliega en forma de radio buttons.
     */
    private fun displayTipoSitio(){
        var radioGroup:RadioGroup = findViewById(R.id.radioGroupTipo)
        val query: ParseQuery<TipoSitio> = ParseQuery.getQuery(TipoSitio::class.java)
        query.findInBackground { itemList, e ->
            if (e == null) {
                var id = 0
                for (el in itemList ) {
                    var radioButton = RadioButton(this)
                    radioButton.text = el.descripcion
                    radioButton.id = id
                    radioGroup.addView(radioButton, -1)
                    listTipoSitio.add(id, el)
                    id = id + 1
                }
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }
    }

    /**
     * Inicializa el mapa de la actividad
     */
    private fun initMap() {
        map = findViewById(R.id.mapCreate) as MapView
        map?.onCreate(null)
        map?.getMapAsync(this)
    }

    /**
     * Cuando el mapa ha sido creado se coloca un marcador en la ubicación del usuario o en la
     * ubicación que seleccionó el usuario.
     * @param googleMap mapa que ya ha sido inicializado
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap?.setMyLocationEnabled(permission!!)

       val latLng = LatLng(currentLocation!!.getLatitude(), currentLocation!!.getLongitude())
        gMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                18f
            )
        )

        gMap?.setOnMapClickListener(GoogleMap.OnMapClickListener {
            val i = Intent(this, NuevaUbicacion::class.java)
            i.putExtra("currentLocation", currentLocation)
            startForResultLocation.launch(i)
        })
    }

    /**
     * Guarda el nuevo sitio en la base de datos
     */
    private fun saveNuevoSitio(){
        nuevoSitio.nombre = inputNombre?.text.toString()
        nuevoSitio.descripcion = inputDescripcion?.text.toString()
        nuevoSitio.historia = inputHistoria?.text.toString()
        nuevoSitio.paginaOficial = inputPagina?.text.toString()
        nuevoSitio.ubicacion = ubicacion

        if(selectedBitmapImage != null){
            val stream = ByteArrayOutputStream()
            selectedBitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            nuevoSitio.foto = ParseFile("Sitio" + (0..1000000000).random().toString() + "-" +".png" , byteArray)
        } else if(selectedUriImage != null){
            nuevoSitio.foto = ParseFile(File(selectedUriImage?.path))
        }

        if(idCheckedButton != -1){
            nuevoSitio.idTipoSitio = listTipoSitio[idCheckedButton!!]
        }

        if(nuevoSitio.nombre!!.length > 0 || nuevoSitio.descripcion!!.length > 0 || idCheckedButton != -1){
            nuevoSitio.saveInBackground { e ->
                if (e == null) {
                    Log.d(TAG, "saved")
                } else {
                    Log.d(TAG, e.toString())
                }
            }
            finish()
        } else{
            Toast.makeText(this, "Llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
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