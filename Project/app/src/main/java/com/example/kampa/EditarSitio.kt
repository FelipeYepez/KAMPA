package com.example.kampa

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kampa.models.Sitio
import com.example.kampa.models.TipoSitio
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.parse.GetDataCallback
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseQuery
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * @author RECON
 * Actividad que inicia cuando se selecciona el floating action button del mapa.
 * Crea un nuevo sitio en la base de datos.
 * @version 1.0
 */
class EditarSitio : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "EditarSitio"

    private lateinit var etNombre: EditText
    private lateinit var btnGaleria: Button
    private lateinit var btnCamara: Button
    private lateinit var ivFotoSitio: ImageView
    var mvLocalizacion: MapView? = null
    private lateinit var ivPinPoint: ImageView
    private lateinit var etDescripcion: EditText
    private lateinit var etHistoria: EditText
    private lateinit var rgCategoria: RadioGroup
    private lateinit var etPaginaOficial: EditText
    private lateinit var btnConfirmarCambios: Button
    private lateinit var locationMap: GoogleMap

    var listTipoSitio = mutableListOf<TipoSitio>()
    private var selectedBitmapImage: Bitmap? = null
    private var selectedUriImage: Uri? = null
    private var permission: Boolean? = null
    private var  currentLocation = LatLng(20.596478229745216, -100.38763531866927)
    private var  sitioUbicacion = LatLng(20.596478229745216, -100.38763531866927)
    private lateinit var setPhotoForResult: ActivityResultLauncher<Intent>
    private lateinit var setLocationForResult: ActivityResultLauncher<Intent>

    private lateinit var sitio: Sitio

    /**
     * Crea la actividad y llama a funciones
     * @param savedInstanceState para que la actividad pueda restaurarse a un estado anterior
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_sitio)

        getExtras(savedInstanceState)
        initMap()
        initializeVariables()
    }

    /**
     * Obtiene los valores del sitio, el permiso para la ubicación, la ubicación actual
     * @param savedInstanceState que guarda los valores que se le pasaron a esta actividad
     */
    private fun getExtras(savedInstanceState: Bundle?) {
        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get(Constantes.SITIO) as Sitio
        } else {
            savedInstanceState.getSerializable(Constantes.SITIO) as Sitio
        }

        permission = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("permission") as? Boolean
        } else {
            savedInstanceState.getSerializable("permission") as? Boolean
        }

        sitioUbicacion = currentLocation
        if (sitio.ubicacion != null) {
            sitioUbicacion= LatLng(sitio.ubicacion!!.latitude,sitio.ubicacion!!.longitude)
        }
    }

    /**
     * Función para inicializar variables del layout como el nombre del sitio, la foto,
     * botones para tomar fotos y acceder a la galería, la descripción y el botón para confirmar
     * cambios.
     */
    private fun initializeVariables() {
        setLocationForResult = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                val latLngLocation = LatLng(
                    resultData!!.getDoubleExtra("latitude",0.0),
                    resultData!!.getDoubleExtra("longitude",0.0)
                )

                sitioUbicacion = latLngLocation

                locationMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLocation, 18f))
            }
        }

        etNombre = findViewById(R.id.etNombre)
        etNombre.setText(sitio.nombre)

        ivFotoSitio = findViewById(R.id.ivFotoSitio)
        loadFoto(sitio.foto)
        setPhotoForResult = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data
                if (resultData?.data != null && "content".equals(resultData.data?.getScheme())) {
                    selectedBitmapImage = bitmapFromUri(resultData.data)!!
                    ivFotoSitio.setImageBitmap(selectedBitmapImage)
                } else {
                    selectedUriImage = resultData?.data!!
                    ivFotoSitio.setImageURI(selectedUriImage)
                }
            }
        }

        btnGaleria = findViewById(R.id.btnGaleria)
        btnGaleria.setOnClickListener {
            val galeria = Intent()
            galeria.type = "image/*"
            galeria.action = Intent.ACTION_GET_CONTENT

            setPhotoForResult.launch(Intent.createChooser(galeria, "Select Picture"))
        }

        btnCamara = findViewById(R.id.btnCamara)
        btnCamara.setOnClickListener {
            setPhotoForResult.launch(Intent(this, TakePictureActivity::class.java))
        }

        rgCategoria = findViewById(R.id.rgCategoria)
        desplegarTipoSitio()

        etDescripcion = findViewById(R.id.etDescripcion)
        etDescripcion.setText(sitio.descripcion)

        etHistoria = findViewById(R.id.etHistoria)
        etHistoria.setText(sitio.historia)

        etPaginaOficial = findViewById(R.id.etPaginaOficial)
        etPaginaOficial.setText(sitio.paginaOficial)

        btnConfirmarCambios = findViewById(R.id.btnConfirmarCambios)
        btnConfirmarCambios.setOnClickListener {
            confirmarCambios()
        }
    }

    /**
     * Función para inicializar el mapa con el contexto actual y poner la ubicación
     * actual del usuario en el mapa.
     */
    private fun initMap() {
        mvLocalizacion = findViewById(R.id.mvLocalizacion) as MapView
        mvLocalizacion?.onCreate(null)
        mvLocalizacion?.getMapAsync(this)
    }

    /**
     * Función que obtiene de la base de datos los tipos de sitio que hay registrados y el
     * tipo de sitio del actual sitio.
     */
    private fun desplegarTipoSitio() {
        val query: ParseQuery<TipoSitio> = ParseQuery.getQuery(TipoSitio::class.java)
        query.findInBackground { itemList, e ->
            if (e == null) {
                var id = 0
                for (item in itemList) {
                    var radioButton = RadioButton(this)
                    radioButton.text = item.descripcion
                    radioButton.id = id
                    if (item.objectId == sitio.idTipoSitio?.objectId.toString()) {
                        radioButton.setChecked(true)
                    }

                    rgCategoria.addView(radioButton, -1)
                    listTipoSitio.add(id, item)
                    id = id + 1
                }
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }
    }

    /**
     * Función que carga la foto del sitio en su layout correspondiente
     * @param fotoSitio es la foto del Sitio
     */
    private fun loadFoto(fotoSitio: ParseFile?){
        if (fotoSitio != null) {
            fotoSitio.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    ivFotoSitio.setImageBitmap(bmp)
                } else{
                    Log.d(TAG, e.toString())
                }
            })
        } else {
            Log.d(TAG, "No hay foto registrada de este lugar")
        }
    }

    /**
     * Función que transforma un Uri a Bitmap para desplegar la imagen del sitio
     * @param photoUri es el Uri de la imagen del sitio
     * @return image retorna el Bitmap de la imagen
     */
    private fun bitmapFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            image = if (Build.VERSION.SDK_INT > 27) {
                val source: ImageDecoder.Source = ImageDecoder.createSource(
                    this.contentResolver,
                    photoUri!!
                )
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return image
    }

    /**
     * Cuando el mapa ha sido creado se coloca un marcador en la ubicación del usuario o en la
     * ubicación que seleccionó el usuario.
     * @param googleMap mapa que ya ha sido inicializado
     * @suppress si falta el permiso
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(_locationMap: GoogleMap) {
        locationMap = _locationMap
        locationMap.setMyLocationEnabled(permission!!)

        locationMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                sitioUbicacion,
                18f
            )
        )

        locationMap.setOnMapClickListener(GoogleMap.OnMapClickListener {
            val newLocation = Intent(this, NuevaUbicacion::class.java)
            newLocation.putExtra(Constantes.CURRENT_LOCATION, sitioUbicacion)
            setLocationForResult.launch(newLocation)
        })
    }

    /**
     * Toda la información escrita por el usuario se registra en la base de datos para guardar
     * los cambios al sitio.
     */
    private fun confirmarCambios() {
        sitio.nombre = etNombre.text.toString()
        sitio.descripcion = etDescripcion.text.toString()
        sitio.historia = etHistoria.text.toString()
        sitio.paginaOficial = etPaginaOficial.text.toString()

        val isUrlValid = Patterns.WEB_URL.matcher(sitio.paginaOficial).matches()

        if (selectedBitmapImage != null){
            val stream = ByteArrayOutputStream()
            selectedBitmapImage!!.compress(Bitmap.CompressFormat.PNG, 100, stream)

            val byteArray = stream.toByteArray()
            val nombreFoto = "Sitio" + (0..1000000000).random().toString() + "-" +".png"
            sitio.foto = ParseFile(nombreFoto, byteArray)
        } else if (selectedUriImage != null){
            sitio.foto = ParseFile(File(selectedUriImage?.path))
        }

        val ubicacion = ParseGeoPoint(sitioUbicacion!!.latitude, sitioUbicacion!!.longitude)
        sitio.ubicacion = ubicacion

        val idCheckedButton = rgCategoria.checkedRadioButtonId
        if (idCheckedButton != -1) {
            sitio.idTipoSitio = listTipoSitio[idCheckedButton]
        }

        val isInputComplete = EditarSitioUtils.validateInputs(sitio.nombre!!,
            sitio.descripcion!!, sitio.ubicacion!!, idCheckedButton!!)

        if (isInputComplete == "Valores completos" && isUrlValid) {
            sitio.saveInBackground { e ->
                if (e == null) {
                    Toast.makeText(
                        this,
                        "Sitio modificado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Log.d(TAG, e.toString())
                }
            }
        } else if (!isUrlValid) {
            Toast.makeText(
                this,
                "La página web ingresada no existe",
                Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                isInputComplete,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


object EditarSitioUtils {
    /**
     * Valida que los campos obligatorios para editar un sitio no estén vacíos o hayan sido
     * seleccionados.
     * @param nombre nuevo nombre del sitio
     * @param descripcion nueva descripción del sitio
     * @param idCheckedButton tipo de sitio seleccionado
     */
    fun validateInputs(nombre:String,
                       descripcion:String,
                       ubicacion:ParseGeoPoint,
                       idCheckedButton:Int)
        :String {

        if(nombre.isEmpty()){
            return "Escribe el nombre del sitio"
        } else if(descripcion.isEmpty()){
            return "Escribe la descripción del sitio"
        } else if(ubicacion.latitude == 0.0 && ubicacion.longitude == 0.0){
            return "Elige la ubicación del sitio"
        } else if(idCheckedButton == -1){
            return "Selecciona una categoría"
        }

        return "Valores completos"
    }
}