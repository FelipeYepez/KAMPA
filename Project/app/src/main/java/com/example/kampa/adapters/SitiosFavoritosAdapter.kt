package com.example.kampa.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kampa.MainActivity
import com.example.kampa.R
import com.example.kampa.SitioActivity
import com.example.kampa.interfaces.SitioInterface
import com.example.kampa.models.Sitio
import com.example.kampa.models.Wishlist
import com.example.kampa.models.WishlistSitio

/** * @author RECON
 *  Adapter para hacer binding entre el arreglo de data a las views
 *  que tienen que mostrarse dentro del recycler view de sitios de una lista
 *  de favoritos.
 *  @param context contexto de la actividad
 *  @param data  MutableList que contiene las listas de favoritos a mostrar
 *  @param listener llama al método passData cuando se da click a un sitio de la lista
 */
class SitiosFavoritosAdapter(private val context: Context?,
                             private var data: MutableList<WishlistSitio>,
                             private val sitioInterfaceListener: SitioInterface,
                             private val mainActivity: MainActivity)
    : RecyclerView.Adapter<SitiosFavoritosAdapter.ViewHolder>() {


    private val TAG = "SitiosFavoritosAdapter"

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
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(
                R.layout.item_sitios_favoritos,
                parent,
                false
            )
        )
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
     * @return el WishlistSitio en la posición solicitada
     */
    fun getItem(position : Int): WishlistSitio {
        return data.get(position)
    }

    /**
     * Función llamada por el RecyclerView para mostrar el dato en una
     * posición específica. Actualiza los contenidos del itemView para mostrar
     * el item en una posición dada.
     * @param holder  El ViewHolder que debe actualizarse
     * @param position  La posición del item dentro del data set del adaptador.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: WishlistSitio = data[position]
        if (context != null) {
            holder.bind(item, context)
        }
    }

    /** * @author RECON
     *  Inner class de View Holder, donde se van a mostrar los datos de la lista
     *  @param view la vista donde se mostrarán los datos
     *  @version 1.0
     */
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private var ivFotoSitio: ImageView = view.findViewById(R.id.ivFotoSitio)
        private var tvSitioTitle: TextView = view.findViewById(R.id.tvSitioTitle)
        private var tvSitioDescription: TextView = view.findViewById(R.id.tvSitioDescription)
        private var ibDirections: ImageButton = view.findViewById(R.id.ibDirections)

        init {
            view.setOnClickListener(this)
        }

        /**
         * Función para hacer binding de los elementos en la vista.
         * Pone el título, la descripción, la foto y un listener para obtener
         * las direcciones del sitio.
         * @param item un sitio con los datos que queremos mostrar
         * @param context de la actividad
         */
        fun bind(item: WishlistSitio, context: Context) {
            tvSitioTitle.text = item.idSitio?.nombre
            tvSitioDescription.text = item.idSitio?.descripcion

            /**
             * Función que al dar click en el botón abre la aplicación de Google Maps y te
             * muestra cómo llegar a ese sitio, a través de la interfaz.
             */
            ibDirections.setOnClickListener {
                val sitio: Sitio? = item.idSitio
                if (sitio != null) {
                    sitioInterfaceListener.passSitio(sitio)
                }
            }

            val urlFotoSitio = if (item.idSitio?.foto == null) {
                R.drawable.buildings
            } else {
                item.idSitio?.foto?.url.toString()
            }

            Glide.with(context)
                .load(urlFotoSitio)
                .placeholder(R.drawable.buildings)
                .error(R.drawable.buildings)
                .into(ivFotoSitio)
        }

        /**
         * Función que sobrescribe OnClick y manda llamar la función recibida como parámatro
         * por el adaptador, para que al dar click en un item,
         * se abra la información de ese sitio.
         * @param view vista donde se dio click
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            val sitio = data[position]
            val context = v?.context

            val i = Intent(context, SitioActivity::class.java)
            i.putExtra("sitio", sitio.idSitio)
            i.putExtra("permission",mainActivity.isPermissionGranted)
            context?.startActivity(i)
        }
    }
}