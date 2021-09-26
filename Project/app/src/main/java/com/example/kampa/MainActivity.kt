package com.example.kampa

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kampa.fragments.DescubreFragment
import com.example.kampa.fragments.FavoritosFragment
import com.example.kampa.fragments.MapaFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    // Variables para navegación en el Menú
    private val descubreFragment = DescubreFragment()
    private val mapaFragment = MapaFragment()
    private val favoritosFragment = FavoritosFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Abrir Actividad con fragmento Descubre
        replaceFragment(descubreFragment)

        // Crear listener de Menu Nav para intercambiar fragmentos
        val menuNav = findViewById<BottomNavigationView>( R.id.menuNav)


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
}