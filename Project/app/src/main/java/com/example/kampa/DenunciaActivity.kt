package com.example.kampa

import android.graphics.BitmapFactory
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.kampa.models.Denuncia
import com.google.android.material.snackbar.Snackbar
import com.parse.GetDataCallback
import com.parse.ParseFile

/** * @author Andrea Piñeiro Cavazos <a01705681@itesm.mx>
 *  Actividad para visualizar la información completa de una
 *  denuncia desde el rol del administrador; esta actividad comienza
 *  al dar click en el item del recycler view.
 *  @version 1.0
 */

class DenunciaActivity() : AppCompatActivity() {
    private lateinit var denuncia: Denuncia
    private lateinit var sitioName: TextView
    private lateinit var imagenDenuncia: ImageView
    private lateinit var descripcionDenuncia: TextView
    private lateinit var btnInvalida: Button
    private lateinit var btnProcesada: Button

    val TAG = "DenunciaActivity"

    /**
     * Se llama cuando la actividad se crea; obtiene la denuncia
     * de los extras, inicializa componentes y listeners.
     * También se carga en el xml el nombre del sitio de la denuncia,
     * la descripción y la imagen.
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_denuncia)

        denuncia = if (savedInstanceState == null) {
            val extras = intent.extras
            extras?.get("denuncia") as Denuncia
        }
        else {
            savedInstanceState.getSerializable("denuncia") as Denuncia
        }

        initializeComponents()
        initializeListeners()

        sitioName.text = denuncia.idSitio?.nombre
        loadImages(denuncia.fotos, imagenDenuncia)
        descripcionDenuncia.text = denuncia.descripcion


    }

    /**
     * Función para cargar una imagen dentro de un imageView
     * @param foto  imagen de tipo parse file que queremos mostrar
     * @param imgView  imageView en el que queremos que se despliegue la imagen
     * @return void - el imageView contiene la foto
     */
    private fun loadImages(foto: ParseFile?, imgView: ImageView) {
        if (foto != null) {
            foto.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    imgView.setImageBitmap(bmp)
                }
                else {
                    imgView.setImageResource(R.drawable.esencia_patrimonio)
                }
            })

        }
    }

    /**
     * Función que inicializa los componentes, buscándolos en la view
     * con su respectivo id.
     * También se hacen viisibles los botones de inválida y procesada solo
     * si la denuncia no ha sido resulta aún.
     */
    fun initializeComponents() {
        sitioName = findViewById(R.id.sitioName)
        imagenDenuncia = findViewById(R.id.imagenDenuncia)
        descripcionDenuncia = findViewById(R.id.descripcionDenuncia)
        btnInvalida = findViewById(R.id.btnInvalida)
        btnProcesada = findViewById(R.id.btnProcesada)

        if(denuncia.estado != "sinResolver") {
            btnInvalida.visibility = View.INVISIBLE
            btnProcesada.visibility = View.INVISIBLE
        }
        else {
            btnInvalida.visibility = View.VISIBLE
            btnProcesada.visibility = View.VISIBLE
        }
    }

    /**
     * Función que inicializa los listeners de los botones para marcar la denuncia como
     * válida y como inválida.
     */
    fun initializeListeners() {
        btnInvalida.setOnClickListener {
            denuncia.estado = "invalida"
            denuncia.saveInBackground { e ->
                if(e == null){
                    Toast.makeText(this, "Denuncia Inválida", Toast.LENGTH_LONG).show()
                    finish()
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), "No se pudo registrar como inválida, intenta más tarde.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        btnProcesada.setOnClickListener {
            denuncia.estado = "procesada"
            denuncia.saveInBackground { e ->
                if(e == null){
                    Toast.makeText(this, "Denuncia Procesada", Toast.LENGTH_LONG).show()
                    finish()
                }
                else {
                    Snackbar.make(findViewById(android.R.id.content), "No se pudo registrar como procesada, intenta más tarde.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }


}