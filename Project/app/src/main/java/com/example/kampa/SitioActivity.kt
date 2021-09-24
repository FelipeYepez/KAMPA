package com.example.kampa
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.kampa.models.Sitio

//import com.denzcoskun.imageslider.ImageSlider
//import com.denzcoskun.imageslider.models.SlideModel


class SitioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitio)
        val sitio: Sitio?
        sitio = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("sitio") as Sitio
        } else {
            savedInstanceState.getSerializable("sitio") as Sitio
        }

        val title:TextView = findViewById(R.id.title)
        title.text = sitio.nombre

        val descripcion : TextView = findViewById(R.id.descripcion)
        descripcion.text = sitio.descripcion

        val historia : TextView = findViewById(R.id.historia)
        historia.text = sitio.historia

//        val imageSlider : ImageSlider = findViewById(R.id.slider)
//        val slideModels : List<SlideModel> = listOf(SlideModel(R.drawable.arcos1), SlideModel(R.drawable.arcos2), SlideModel(R.drawable.arcos3), SlideModel(R.drawable.arcos2))
//
//        imageSlider.setImageList(slideModels, true)

    }



}