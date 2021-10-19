package com.example.kampa
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.kampa.models.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.parse.*
import kotlinx.android.synthetic.main.cambiar_nombre_dialogo.view.*


class SitioActivity : AppCompatActivity() {


    private lateinit var ibEliminarSitio: ImageButton
    private lateinit var ibEditarSitio: ImageButton
    private lateinit var registrarDenuncia: Button
    private lateinit var ListaDeseos: Button
    private lateinit var registrarVisitado: Button


    private var rolObject: Rol? = null
    private lateinit var sitio: Sitio
    private var permission: Boolean? = null
    private var currentLocation: Location? = null
    private var usuarioSitio: UsuarioSitio? = null

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
            ibEliminarSitio = findViewById(R.id.ibEliminarSitio)
            ibEliminarSitio.visibility = View.VISIBLE
            ibEliminarSitio.setOnClickListener {
                eliminarSitio()
            }

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
        ListaDeseos = findViewById(R.id.addWishListBtn)
        ListaDeseos.setOnClickListener{
            val query1: ParseQuery<Wishlist> = ParseQuery.getQuery(Wishlist::class.java)

        /*
         * On click Listener para el botón de registrar visitado
         */
        registrarVisitado = findViewById(R.id.VisitedBtn)
        verificarSitioVisitado()
        registrarVisitado.setOnClickListener{
            cambiarVisitado()
        }

            query1.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())
            query1.whereEqualTo(Constantes.IS_DELETED, false)
            query1.orderByDescending(Constantes.CREATED_AT)
            query1.findInBackground { objects: MutableList<Wishlist>?, e: ParseException? ->
                if (e == null) {
                    if (objects != null) {
                        val listFavs : MutableList<String> = mutableListOf()
                        for(obj in objects){
                            listFavs.add(obj.nombre.toString())
                        }
                        val arr : Array<String> = listFavs.toTypedArray()
                        val builder = AlertDialog.Builder(this)
                            .setTitle(R.string.Lista_favoritos)
                            .setItems(arr){dialog, which ->

                                val queryExisteEnWishlist: ParseQuery<WishlistSitio> = ParseQuery.getQuery(WishlistSitio::class.java)
                                queryExisteEnWishlist.whereEqualTo(Constantes.ID_SITIO, sitio)
                                queryExisteEnWishlist.whereEqualTo(Constantes.ID_WISHLIST, objects[which])
                                queryExisteEnWishlist.getFirstInBackground(GetCallback { wishListSitio: WishlistSitio?, e ->
                                    if(e != null){
                                        // Si aun no existe Sitio en Wishlist crearlo
                                        if(e.code == 101){
                                            val nWishlistSitio : WishlistSitio = WishlistSitio()
                                            nWishlistSitio.idWishlist = objects[which]
                                            nWishlistSitio.idSitio = sitio
                                            nWishlistSitio.saveInBackground { e ->
                                                // Si se pudo guardar
                                                if (e == null) {
                                                    val snack = Snackbar.make(it, R.string.Lista_favoritos_exito, Snackbar.LENGTH_SHORT)
                                                    snack.setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                                                    snack.setTextColor(ContextCompat.getColor(this, R.color.exito))
                                                    snack.show()
                                                } else {
                                                    val snack = Snackbar.make(it, R.string.error_conexion, Snackbar.LENGTH_LONG)
                                                    snack.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
                                                    snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                                                    snack.show()
                                                }
                                            }
                                        }
                                        else{
                                            val snack = Snackbar.make(it, R.string.error_conexion, Snackbar.LENGTH_LONG)
                                            snack.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
                                            snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                                            snack.show()
                                        }
                                    }
                                    else{
                                        val snack = Snackbar.make(it, "Ya existe ${sitio.nombre} en ${objects[which].nombre}", Snackbar.LENGTH_LONG)
                                        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.amarilloEsenciaPatrimonio))
                                        snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                                        snack.show()
                                    }
                                })
                            }
                            .setPositiveButton(R.string.crear_lista,
                                DialogInterface.OnClickListener { dialog, id ->
                                    val myDialogView = LayoutInflater
                                        .from(this)
                                        .inflate(R.layout.crear_lista_favoritos_dialogo, null)

                                    val builder2 = AlertDialog.Builder(this)
                                        .setView(myDialogView)
                                        .setTitle(R.string.nueva_lista_favoritos)
                                        .setPositiveButton(R.string.crear,
                                            DialogInterface.OnClickListener { dialog, id ->
                                                val nuevoNombre = myDialogView.etNuevoNombre.text.toString()

                                                if (nuevoNombre.isNotEmpty()) {
                                                    val nuevaWishlist: Wishlist = Wishlist()
                                                    nuevaWishlist.nombre = nuevoNombre
                                                    nuevaWishlist.idUsuario = ParseUser.getCurrentUser()

                                                    nuevaWishlist.saveInBackground { e ->
                                                        if (e == null) {
                                                            val nWishlistSitio : WishlistSitio = WishlistSitio()
                                                            nWishlistSitio.idWishlist = nuevaWishlist
                                                            nWishlistSitio.idSitio = sitio
                                                            nWishlistSitio.saveInBackground { e ->
                                                                if (e == null) {
                                                                    val snack = Snackbar.make(it, R.string.Lista_favoritos_exito, Snackbar.LENGTH_SHORT)
                                                                    snack.setBackgroundTint(ContextCompat.getColor(this, R.color.white))
                                                                    snack.setTextColor(ContextCompat.getColor(this, R.color.exito))
                                                                    snack.show()
                                                                } else {
                                                                    val snack = Snackbar.make(it, R.string.error_conexion, Snackbar.LENGTH_LONG)
                                                                    snack.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
                                                                    snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                                                                    snack.show()
                                                                    dialog.cancel()
                                                                }
                                                            }
                                                        } else {
                                                            val snack = Snackbar.make(it, R.string.error_conexion, Snackbar.LENGTH_LONG)
                                                            snack.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
                                                            snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                                                            snack.show()
                                                            dialog.cancel()
                                                        }
                                                    }
                                                } else {
                                                    val snack = Snackbar.make(it, R.string.nombre_vacio, Snackbar.LENGTH_LONG)
                                                    snack.setBackgroundTint(ContextCompat.getColor(this, R.color.amarilloEsenciaPatrimonio))
                                                    snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                                                    snack.show()
                                                }
                                            })
                                        .setNegativeButton(R.string.cancelar,
                                            DialogInterface.OnClickListener { dialog, id ->
                                                dialog.cancel()
                                            })

                                    builder2.show()
                                })
                            .setNegativeButton(R.string.cancelar,
                                DialogInterface.OnClickListener { dialog, id ->
                                    dialog.cancel()
                                })
                        builder.show()
                    }
                }
                else{
                    if(e.code == 100){
                        val snack = Snackbar.make(it, R.string.error_conexion, Snackbar.LENGTH_LONG)
                        snack.setBackgroundTint(ContextCompat.getColor(this, R.color.error))
                        snack.setTextColor(ContextCompat.getColor(this, R.color.white))
                        snack.show()
                    }
                }
            }
        }


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

    private fun eliminarSitio() {
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.confirmar_eliminar))
            .setMessage(resources.getString(R.string.seguro_eliminar_sitio))
            .setNegativeButton(resources.getString(R.string.cancelar)) { dialog, which ->
            }
            .setPositiveButton(resources.getString(R.string.eliminar)) { dialog, which ->
                sitio.deleteInBackground { e ->
                    if (e == null) {
                        onDestroy()
                    } else {
                        Toast.makeText(
                            this,
                            "No se pudo eliminar este sitio",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            .show()
    }

    private fun goToEditarSitio() {
        val editarSitio = Intent(this, EditarSitio::class.java)
        editarSitio.putExtra(Constantes.PERMISSION, permission)
        editarSitio.putExtra(Constantes.CURRENT_LOCATION, currentLocation)
        editarSitio.putExtra(Constantes.SITIO, sitio)
        startActivity(editarSitio)
    }

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