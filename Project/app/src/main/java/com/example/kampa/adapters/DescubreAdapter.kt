package com.example.kampa.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.models.Publicacion
import com.parse.GetDataCallback
import com.parse.ParseFile
import android.graphics.BitmapFactory
import com.example.kampa.R
import java.util.*

/**
 * Adaptador para vincular Publicaciones en tarjetas dentro de un recycler view, en este caso
 * en un stack de tarjetas.
 *
 * @see <a href="https://github.com/yuyakaido/CardStackView">CardStackView</a>
 * @author RECON
 * @param context recibe contexto de vista de fragmento dentro de actividad
 * @param data arreglo de Publicaciones priorizadas para Usuario
 */
class DescubreAdapter( private val data: ArrayList<Publicacion>):
    RecyclerView.Adapter<DescubreAdapter.CardViewHolder>() {

    /**
     * Crea contenedor de vista cuando RecyclerView ya no puede reutilizar más.
     * Infla layout de Publicación con su información dentro de tarjeta.
     *
     * @param parent framelayout con id de contenedores de Publicaciones
     * @param viewType número entero que representa id de vista almacenado en Resources
     * @return contenedor inflado con imagen, nombre y descripcion de publicación.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return CardViewHolder(layoutInflater.inflate(R.layout.swipe_card_layout, parent, false))
    }

    /**
     * Obtener tamaño de colección de datos
     *
     * @return número entero de Publicaciones totales
     */
    override fun getItemCount(): Int {
        return data.size;
    }

    /**
     * Adapta cada Publicación en contenedor de tarjeta
     *
     * @param holder contenedor, tarjeta en la que se infla Publicacion
     * @param position índice de Publicación en arreglo de Publicaciones
     */
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        var item: Publicacion = data[position]
        holder.bind(item)
    }

    /**
     * Contenedor para vista de Publicación
     *
     * @param view vista padre de diseño de tarjeta para Publicación
     * @author RECON
     */
    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Obtener elementos de tarjeta de Publicación
        private var swipeCardImage: ImageView = view.findViewById(R.id.swipeCardImage)
        private var swipeCardName: TextView = view.findViewById(R.id.swipeCardName)
        private var swipeCardDescription: TextView = view.findViewById(R.id.swipeCardDescription)

        /**
         * Despliega imagen de Publicación
         *
         * @param foto imagen de publicación almacenada en base de datos.
         * @param imgView elemento para desplegar imagen en tarjeta.
         */
        private fun loadImages(foto: ParseFile?, imgView: ImageView){
            foto?.getDataInBackground(GetDataCallback { data, e ->
                if (e == null) {
                    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                    imgView.setImageBitmap(bmp)
                }
            })
        }

        /**
         * Adaptar información de Publicación en tarjeta
         *
         * @param item Publicación a desplegar obtenida en arreglo de Publicaciones priorizadas
         * @param context vista de fragmento dentro de actividad
         */
        fun bind(item: Publicacion) {
            loadImages(item.foto, swipeCardImage)

            swipeCardName.setText(item.idSitio?.nombre)
            swipeCardDescription.setText(item.descripcion.toString())
        }
    }


}