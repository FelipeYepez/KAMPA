package com.descubre.kampa.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.descubre.kampa.R
import com.descubre.kampa.interfaces.SitiosFavoritosInterface
import com.descubre.kampa.models.Wishlist
import com.descubre.kampa.adapters.FavoritosAdapter.ViewHolder

/** * @author RECON
 *  Adapter para hacer binding entre el arreglo de data a las views
 *  que tienen que mostrarse dentro del recycler view de lista de favoritos.
 *  @param context contexto de la actividad
 *  @param data  MutableList que contiene las listas de favoritos a mostrar
 *  @param listener llama al método passData cuando se da click a una lista de favoritos
 *  @version 1.0
 */
class FavoritosAdapter(private val context: Context?,
                       private val data: MutableList<Wishlist>,
                       private val listener: SitiosFavoritosInterface
)
    : RecyclerView.Adapter<ViewHolder>() {

    /**
     * Se llama para crear un ViewHolder para representar un item.
     * Se infla el xml layout para representar los items del tipo dado.
     * @param parent  ViewGroup en el que la nueva vista se va a añadir en una posiicón
     * @param viewType  el tipo de view de la nueva vista
     */
    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_favoritos, parent, false))
    }

    /**
     * Función que retorna el número total de items en el arraylist de datos.
     * @return data.size
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Función que elimina un item deslizado por el usuario de la lista
     * @suppress to the notify data set changed
     * @param position es la posición del item en la lista que se eliminará
     */
    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        data.removeAt(position)
        notifyDataSetChanged()
    }

    /**
     * Función que retorna un item de una posición de la lista
     * @param position es la posición del item en la lista que se retornará
     * @return el Wishlist en la posición solicitada
     */
    fun getItem(position : Int): Wishlist {
        return data.get(position)
    }

    /**
     * Función que agrega un dato al principio de la lista
     * @suppress to the notify data set changed
     * @param wishlist es el item que se agregará al principio
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addToTop(wishlist: Wishlist) {
        data.add(0, wishlist)
        notifyDataSetChanged()
    }

    /**
     * Función llamada por el RecyclerView para mostrar el dato en una
     * posición específica. Actualiza los contenidos del itemView para mostrar
     * el item en una posición dada.
     * @param holder  El ViewHolder que debe actualizarse
     * @param position  La posición del item dentro del data set del adaptador.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Wishlist = data[position]
        if (context != null) {
            holder.bind(item, context)
        }
    }

    /** * @author RECON
     *  Inner class de View Holder, donde se van a mostrar los datos de la lista
     *  @param view la vista donde se mostrarán los datos
     *  @version 1.0
     */
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private var ivFotoWishlist: ImageView = view.findViewById(R.id.ivFotoSitio)
        private var tvFavoritoTitle: TextView = view.findViewById(R.id.tvFavoritoTitle)

        init {
            view.setOnClickListener(this)
        }

        /**
         * Función para hacer binding de los elementos en la vista.
         * Pone el título de la lista de favoritos y una foto
         * @param item  Lista de favoritos (Wishlist) con los datos que queremos mostrar
         * @param context de la actividad
         */
        fun bind(item: Wishlist, context: Context) {
            tvFavoritoTitle.text = item.nombre

            Glide.with(context)
                .load(item.fotoWishlist)
                .placeholder(R.drawable.buildings)
                .error(R.drawable.buildings)
                .into(ivFotoWishlist)
        }

        /**
         * Función que sobrescribe OnClick y manda llamar la función recibida como parámatro
         * por el adaptador, para que al dar click en un item,
         * se abra la información de esa lista de favoritos.
         * @param view vista donde se dio click
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            val wishlist = data[position]

            if (position != RecyclerView.NO_POSITION) {
                listener.passData(wishlist)
            }
        }
    }
}