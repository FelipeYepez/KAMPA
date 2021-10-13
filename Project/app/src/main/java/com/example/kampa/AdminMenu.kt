package com.example.kampa

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.kampa.databinding.ActivityAdminMenuBinding
import com.example.kampa.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
 * Admin Menu Activity: Navigation Bottom Bar for admin role.
 */

class AdminMenu : AppCompatActivity() {

    private val aprobarFragment = AprobarFragment()
    private val mapaFragment = MapaFragment()
    private val denunciasFragment = DenunciasFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_menu)

        // Abrir Actividad con fragmento Aprobar
        replaceFragment(aprobarFragment)

        // Crear listener de Menu Nav para intercambiar fragmentos
        val menuNav = findViewById<BottomNavigationView>( R.id.menuAdmin)

        menuNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.iconoAprobar -> replaceFragment(aprobarFragment)
                R.id.iconoMapa -> replaceFragment(mapaFragment)
                R.id.iconoDenuncias -> replaceFragment(denunciasFragment)
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