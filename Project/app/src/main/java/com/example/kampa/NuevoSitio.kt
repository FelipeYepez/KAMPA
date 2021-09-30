package com.example.kampa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.kampa.models.Sitio
import com.google.android.gms.maps.model.LatLng
import com.parse.ParseGeoPoint
import com.example.kampa.Constantes
import com.example.kampa.models.TipoSitio
import com.parse.ParseQuery

class NuevoSitio : AppCompatActivity() {
    val TAG = "NuevoSitio"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_sitio)

        var listTipoSitio = mutableListOf<TipoSitio>()
        desplegarTipoSitio(listTipoSitio)

        var sitio = Sitio()

        val submitButtonSitio:Button = findViewById(R.id.submitButtonSitio)
        submitButtonSitio.setOnClickListener{

            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()

            val inputNombre:EditText = findViewById(R.id.inputNombre)
            sitio.nombre = inputNombre.text.toString()
            Log.d(TAG, sitio.nombre.toString())

            val inputDescripcion:EditText = findViewById(R.id.inputDescripcion)
            sitio.descripcion = inputDescripcion.text.toString()

            val inputHistoria:EditText = findViewById(R.id.inputHistoria)
            sitio.historia = inputHistoria.text.toString()

            val inputPagina:EditText = findViewById(R.id.inputPagina)
            sitio.paginaOficial = inputPagina.text.toString()

            val ubicacion = ParseGeoPoint(-30.0, 40.0)
            sitio.ubicacion = ubicacion

            val tipoSitio:RadioGroup = findViewById(R.id.radioGroupTipo)
            val idCheckedButton = tipoSitio.checkedRadioButtonId
            val checkedBtn:RadioButton = findViewById(idCheckedButton)


            //sitio.idTipoSitio = getIdTipoSitio(checkedBtn.text.toString())

            sitio.saveInBackground { e ->
                if (e == null) {
                    Log.d(TAG, "saved")
                } else {
                    Log.d(TAG, e.toString())
                }
            }


        }

    }

    fun desplegarTipoSitio(listTipoSitio:MutableList<TipoSitio>){

        var radioGroup:RadioGroup = findViewById(R.id.radioGroupTipo)
        val query: ParseQuery<TipoSitio> = ParseQuery.getQuery(TipoSitio::class.java)
        query.findInBackground { itemList, e ->
            if (e == null) {
                var id = 0
                for (el in itemList ) {
                    var radioButton = RadioButton(this)
                    radioButton.text = el.descripcion
                    radioButton.id = id
                    radioGroup.addView(radioButton, -1)
                    listTipoSitio.add(id, el)
                    id = id + 1
                }
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }

    }

//    fun getIdTipoSitio(nameSitio:String, query: ParseQuery<TipoSitio>){
//        query.findInBackground { itemList, e ->
//            if (e == null) {
//                var id = 0
//                for (el in itemList ) {
//                    if(el.)
//                }
//            } else {
//                Log.d("item", "Error: " + e.message)
//            }
//        }
//    }






}