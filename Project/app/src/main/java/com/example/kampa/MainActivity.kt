package com.example.kampa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.kampa.fragments.DescubreFragment
import com.example.kampa.fragments.FavoritosFragment
import com.example.kampa.fragments.MapaFragment
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