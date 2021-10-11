package com.example.kampa
import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.kampa.models.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.*


class SitioActivity : AppCompatActivity() {

    private lateinit var ibEditarSitio: ImageButton
    private lateinit var registrarDenuncia: Button

    private var rolObject: Rol? = null
    private lateinit var sitio: Sitio
    private var permission: Boolean? = null
    private var currentLocation: Location? = null

    val TAG = "SitioActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitio)

        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("sitio") as Sitio
        } else {
            savedInstanceState.getSerializable("sitio") as Sitio
        }

        permission = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("permission") as? Boolean
        } else {
            savedInstanceState.getSerializable("permission") as? Boolean
        }

        currentLocation = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("currentLocation") as? Location
        } else {
            savedInstanceState.getSerializable("currentLocation") as? Location
        }

        var currentRole: Rol = ParseUser.getCurrentUser().get(Constantes.ID_ROL) as Rol

        rolQuery(currentRole.objectId.toString())

        if(rolObject?.descripcion == Constantes.ADMINISTRADOR){
            ibEditarSitio = findViewById(R.id.ibEditarSitio)
            ibEditarSitio.visibility = View.VISIBLE
            ibEditarSitio.setOnClickListener {
                goToEditarSitio()
            }
        }

        /*
         * On click Listener para el botón de registrar denuncia
         */
        registrarDenuncia = findViewById(R.id.DenunciarBtn)
        registrarDenuncia.setOnClickListener{
            val intent = Intent(this, CrearDenunciaActivity::class.java)
            intent.putExtra("sitio", sitio)
            startActivity(intent)
        }

        val nuevaPublicacion: FloatingActionButton = findViewById(R.id.floatingActionButton)
        nuevaPublicacion.setOnClickListener{

            val i = Intent(this, NuevaPublicacion::class.java)

            i.putExtra("sitio", sitio)

            startActivity(i)
        }

        val title:TextView = findViewById(R.id.title)
        title.text = sitio.nombre

        val descripcion : TextView = findViewById(R.id.descripcion)
        descripcion.text = sitio.descripcion

        val historia : TextView = findViewById(R.id.historia)
        historia.text = sitio.historia

        val titleHistoria : TextView = findViewById(R.id.titleHistoria)
        if(historia.text.length > 0){
            titleHistoria.visibility = View.VISIBLE
        }
        else{
            titleHistoria.visibility = View.INVISIBLE
        }

        val paginaOficial :TextView = findViewById(R.id.paginaOficial)
        paginaOficial.text= sitio.paginaOficial

        val foto : ImageView = findViewById(R.id.foto)
        loadImages(sitio.foto, foto)
    }

    private fun rolQuery(id: String){
        val query = ParseQuery<Rol>(Rol::class.java)
        try {
            rolObject = query[id]
        } catch (e: ParseException) {
            Log.d(TAG, e.toString())
        }
    }


    private fun loadImages(foto: ParseFile?, imgView: ImageView){
        if (foto != null) {
            foto.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    imgView.setImageBitmap(bmp)
                }
                else{
                    Log.d(TAG, e.toString())

                }
            })
        }
        else{
            Log.d(TAG, "Foto = NULL")
        }

    }

    private fun goToEditarSitio() {
        val editarSitio = Intent(this, EditarSitio::class.java)
        editarSitio.putExtra(Constantes.PERMISSION, permission)
        editarSitio.putExtra(Constantes.CURRENT_LOCATION, currentLocation)
        editarSitio.putExtra(Constantes.SITIO, sitio)
        startActivity(editarSitio)
    }

}