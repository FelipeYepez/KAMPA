package com.example.kampa
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.kampa.models.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.*

/**
 * @author RECON
 * Actividad que inicia cuando se selecciona el marcador de un sitio en MapaFragment
 * Obtiene desde la base de datos los atributos del sitio seleccionado y los despliega en una vista
 */
class SitioActivity : AppCompatActivity() {
    val TAG = "SitioActivity"

    private lateinit var ibEditarSitio: ImageButton
    private lateinit var registrarDenuncia: Button
    private lateinit var registrarVisitado: Button

    private var rolObject: Rol? = null
    private lateinit var sitio: Sitio
    private var permission: Boolean? = null
    private var  currentLocation = LatLng(20.596478229745216, -100.38763531866927)
    private var usuarioSitio: UsuarioSitio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitio)

        //Obtiene el sitio seleccionado a través de un extra que se guardó en MapaFragment
        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("sitio") as Sitio
        } else {
            savedInstanceState.getSerializable("sitio") as Sitio
        }

        //Obtiene los permisos aceptados igualmente de un extra
        permission = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("permission") as? Boolean
        } else {
            savedInstanceState.getSerializable("permission") as? Boolean
        }

        //Obtiene la localización actual del usuario
        currentLocation = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("currentLocation") as LatLng
        } else {
            savedInstanceState.getSerializable("currentLocation") as LatLng
        }

        //Rol del usuario autenticado
        var currentRole: Rol = ParseUser.getCurrentUser().get(Constantes.ID_ROL) as Rol

        rolQuery(currentRole.objectId.toString())

        //Si el rol del usuario es administrador, se muestra un botón para añadir sitios
        if(rolObject?.descripcion == Constantes.ADMINISTRADOR){
            ibEditarSitio = findViewById(R.id.ibEditarSitio)
            ibEditarSitio.visibility = View.VISIBLE
            ibEditarSitio.setOnClickListener {
                goToEditarSitio()
            }
        }

        //On click Listener para el botón de registrar denuncia
        registrarDenuncia = findViewById(R.id.DenunciarBtn)
        registrarDenuncia.setOnClickListener{
            val intent = Intent(this, CrearDenunciaActivity::class.java)
            intent.putExtra("sitio", sitio)
            startActivity(intent)
        }

        //On click Listener para el botón de registrar visitado
        registrarVisitado = findViewById(R.id.VisitedBtn)
        verificarSitioVisitado()
        registrarVisitado.setOnClickListener{
            cambiarVisitado()
        }

        //On click Listener para agregar una nueva publicación
        val nuevaPublicacion: FloatingActionButton = findViewById(R.id.floatingActionButton)
        nuevaPublicacion.setOnClickListener{
            val i = Intent(this, NuevaPublicacionActivity::class.java)
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
        } else{
            titleHistoria.visibility = View.INVISIBLE
        }

        val paginaOficial :TextView = findViewById(R.id.paginaOficial)
        paginaOficial.text= sitio.paginaOficial

        val foto : ImageView = findViewById(R.id.foto)
        loadImages(sitio.foto, foto)

    }

    /**
     * Busca un objeto de tipo Rol con su ObjectId
     * @param id objectId del rol que tiene el usuario autenticado
     */
    private fun rolQuery(id: String){
        val query = ParseQuery<Rol>(Rol::class.java)
        try {
            rolObject = query[id]
        } catch (e: ParseException) {
            Log.d(TAG, e.toString())
        }
    }

    /**
     * Coloca una imagen tipo ParseFile en el ImageView del sitio
     * @param foto imagen que se obtuvo de la base de datos
     * @param imgView vista en la que se coloca la imagen
     */
    private fun loadImages(foto: ParseFile?, imgView: ImageView){
        if (foto != null) {
            foto.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    imgView.setImageBitmap(bmp)
                } else{
                    Log.d(TAG, e.toString())
                }
            })
        } else{
            Log.d(TAG, "No hay imagen")
        }
    }

    /**
     * Intent para editar el sitio correspondiente
     */
    private fun goToEditarSitio() {
        val editarSitio = Intent(this, EditarSitio::class.java)
        editarSitio.putExtra(Constantes.PERMISSION, permission)
        editarSitio.putExtra(Constantes.CURRENT_LOCATION, currentLocation)
        editarSitio.putExtra(Constantes.SITIO, sitio)
        startActivity(editarSitio)
    }

    /**
     * Marcar un sitio como visitado
     */
    private fun cambiarVisitado(){
        if (usuarioSitio != null) {
            // Si Sitio ya fue visitado por usuario marcar como no visitado
            if(usuarioSitio!!.isVisitado == true){
                usuarioSitio!!.isVisitado = false
                usuarioSitio!!.saveInBackground()
                UIVisitado(false)
            }
            // Sitio no ha sido marcado como visitado, marcarlo.
            else if(usuarioSitio!!.isVisitado == false){
                usuarioSitio!!.isVisitado = true
                usuarioSitio!!.saveInBackground()
                UIVisitado(true)
            }
        }
        else {
            usuarioSitio = UsuarioSitio()
            usuarioSitio!!.idSitio = sitio
            usuarioSitio!!.idUsuario = ParseUser.getCurrentUser()
            usuarioSitio!!.isWishlist = false
            usuarioSitio!!.isVisitado = true
            usuarioSitio!!.saveInBackground()
        }
    }

    /**
     * Verificar si un sitio ya está visitado
     */
    private fun verificarSitioVisitado(){
        // Parse Query para marcar Sitio como visitado por Usuario
        val querySitioVisitado: ParseQuery<UsuarioSitio> = ParseQuery.getQuery(UsuarioSitio::class.java)
        querySitioVisitado.whereEqualTo(Constantes.ID_SITIO, sitio)
        querySitioVisitado.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())

        // Parse Query para obtener primer registro de Parse
        querySitioVisitado.getFirstInBackground(GetCallback { usuarioSitioRegistro: UsuarioSitio?, e ->
            // Si registro ya existe
            if (e == null && usuarioSitioRegistro != null) {
                // Almacenar usuarioSitio global
                usuarioSitio = usuarioSitioRegistro
                // Si Sitio ya fue visitado por usuario
                if(usuarioSitioRegistro.isVisitado == true){
                    UIVisitado(true)
                }
                // Sitio no ha sido marcado como visitado, marcarlo.
                else if(usuarioSitioRegistro.isVisitado == false){
                    UIVisitado(false)
                }
            }
            else{
                // Si no se encontró resultado de get en parse
                if(e.code == 101){
                    usuarioSitio = null
                }
                else{
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_LONG).show()
                    registrarVisitado.visibility = View.INVISIBLE
                }
            }
        })
    }

    /**
     * Cambiar el color del botón "Visitado" dependiendo de si está visitado o no
     */
    private fun UIVisitado(visitado: Boolean){
        if(visitado){
            registrarVisitado.setTextColor(ContextCompat.getColor(this,
                R.color.white))
            registrarVisitado.setBackgroundColor(ContextCompat.getColor(this,
                R.color.quantum_lightgreen900))
        }
        else{
            registrarVisitado.setTextColor(ContextCompat.getColor(this,
                R.color.black))
            registrarVisitado.setBackgroundColor(ContextCompat.getColor(this,
                R.color.white))
        }
    }

}