package com.example.kampa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kampa.fragments.DescubreFragment
import com.example.kampa.fragments.FavoritosFragment
import com.example.kampa.fragments.MapaFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity() {

    // Variables para navegación en el Menú
    private val descubreFragment = DescubreFragment()
    private val mapaFragment = MapaFragment()
    private val favoritosFragment = FavoritosFragment()
    var isPermissionGranted : Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        checkMyPermission()


        // Abrir Actividad con fragmento Descubre
        replaceFragment(descubreFragment)

        // Crear listener de Menu Nav para intercambiar fragmentos
        val menuNav = findViewById<BottomNavigationView>( R.id.menuNav)
        menuNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.iconoDescubre -> replaceFragment(descubreFragment)
                R.id.iconoMapa -> replaceFragment(mapaFragment)
                R.id.iconoFavoritos -> replaceFragment(favoritosFragment)
            }
            true
        }
    }

    // Función para intercambiar entre fragmentos en actividad
    private fun replaceFragment(fragment: Fragment){
        if(fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()

        }
    }
    private fun checkMyPermission() {
        Dexter.withContext(this).withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION).withListener(object :
            PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
//                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                isPermissionGranted = true
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
//                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
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