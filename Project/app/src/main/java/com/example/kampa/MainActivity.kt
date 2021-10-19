package com.example.kampa

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kampa.databinding.ActivityAprobarFragmentBinding
import com.example.kampa.fragments.*
import com.example.kampa.models.Rol
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser


/**
 * @author RECON
 *
 * Esta actividad se encarga de controlar la navegacion principal de el usuario dependiendo de su rol
 * En base a su rol cambia tdoa la barra de navegacion y los fragmentos conectados a esta
 */
class MainActivity : AppCompatActivity() {

    // Variables para navegación en el Menú
    private val descubreFragment = DescubreFragment()
    private val mapaFragment = MapaFragment()
    private val favoritosFragment = FavoritosFragment()
    private val aprobarFragment = AprobarFragment()
    private val denunciasFragment = DenunciasFragment()
    var isPermissionGranted : Boolean? = false
    private var roleObject: Rol? = null
    var currentRole:Rol = ParseUser.getCurrentUser().get("idRol") as Rol
    val TAG : String = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roleQuery(currentRole.objectId.toString())
        if(roleObject?.descripcion == "administrador"){
            setContentView(R.layout.activity_admin_menu)
            //Pide permiso para el mapa
            checkMyPermission()

            // Abrir Actividad con fragmento Aprobar
            replaceFragment(aprobarFragment)

            // Crear listener de Menu Nav para intercambiar fragmentos
            val menuNav = findViewById<BottomNavigationView>( R.id.menuAdmin)

            menuNav.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.iconoAprobar -> replaceFragment(aprobarFragment)//colocar fragmento Aprobar en la primera posicion
                    R.id.iconoMapa -> replaceFragment(mapaFragment)//colocar fragmento Mapa en la segunda posicion
                    R.id.iconoDenuncias -> replaceFragment(denunciasFragment)//colocar fragmento Denuncias en la tercera posicion
                }
                true
            }

        }
        else {
            setContentView(R.layout.activity_main)

            //Pide permiso para el mapa
            checkMyPermission()

            // Abrir Actividad con fragmento Descubre
            replaceFragment(descubreFragment)

            // Crear listener de Menu Nav para intercambiar fragmentos
            val menuNav = findViewById<BottomNavigationView>(R.id.menuNav)


            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled)
            )

            val colors = intArrayOf(
                Color.parseColor("#D83670"),
                Color.parseColor("#15CDCD"),
                Color.parseColor("#ECB518")
            )


            val myList = ColorStateList(states, colors)
            //menuNav.menu.findItem(R.id.iconoDescubre).iconTintList
            //menuNav.itemIconTintList = myList

            menuNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.iconoDescubre -> replaceFragment(descubreFragment)//colocar fragmento Descubre en la primera posicion
                    R.id.iconoMapa -> replaceFragment(mapaFragment)//colocar fragmento Mapa en la segunda posicion
                    R.id.iconoFavoritos -> replaceFragment(favoritosFragment)//colocar fragmento Favoritos en la tercera posicion
                }
                true
            }
        }
    }
    /**
     * @author RECON
     *
     * Funcion para obtener el rol de el usuario actual
     *
     * @param id identificador unico del usuario actual para poder consultar su rol
     */

    private fun roleQuery(id:String){
        val query = ParseQuery<Rol>("Rol")
        try {
            roleObject = query[id]
        } catch (e: ParseException) {
            Log.d(TAG, e.toString())
        }
    }

    /**
     * @author RECON
     *
     * Función para intercambiar entre fragmentos en actividad
     *
     * @param fragment Fragmeto a colocar en la pantalla
     */
    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()

        }

    }
    /**
     * @author RECON
     *
     * Esta funcion se encarga de pedir el permiso de ubicacion nesesario para poder abrir el mapa
     *
     * guarda la seleccion de el usuario en la variable isPermissionGranted
     * que es accedida en todos los mapas de la aplicacion
     *
     *
     */
    private fun checkMyPermission() {
        Dexter.withContext(this).withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION).withListener(object :
            PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                isPermissionGranted = true
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                isPermissionGranted = false
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                p1?.continuePermissionRequest()
            }
        }).check()

    }
}