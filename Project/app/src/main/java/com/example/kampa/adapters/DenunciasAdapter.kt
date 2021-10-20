package com.example.kampa.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.R
import com.example.kampa.models.Denuncia
import com.parse.GetDataCallback
import com.parse.ParseFile

/** * @author Andrea Piñeiro Cavazos <a01705681@itesm.mx>
 *  Adapter para hacer binding entre el arreglo de data a las views
 *  que tienen que mostrarse dentro del recycler view de denundias.
 *  @param context
 *  @param data  ArrayList que contiene las denuncias a mostrar
 *  @param onItemClicked función para mostrar la información de la denuncia al hacer click.
 *  @version 1.0
 */

class DenunciasAdapter(private val context: Context,
                       private val data: ArrayList<Denuncia>,
                       private val onItemClicked: (position: Int) -> Unit):
    RecyclerView.Adapter<DenunciasAdapter.ViewHolder>() {

    /**
     * Se llama para crear un ViewHolder para representar un item.
     * Se infla el xml layout para representar los items del tipo dado.
     * @param parent  ViewGroup en el que la nueva vista se va a añadir en una posiicón
     * @param viewType  el tipo de view de la nueva vista
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DenunciasAdapter.ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_denuncias, parent, false), onItemClicked)
    }

    /**
     * Función que retorna el número total de items en el arraylist de datos.
     * @return data.size
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Función llamada por el RecyclerView para mostrar el dato en una
     * posición específica. Actualiza los contenidos del itemView para mostrar
     * el item en una posición dada.
     * @param holder  El ViewHolder que debe actualizarse
     * @param position  La posición del item dentro del data set del adaptador.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Denuncia = data[position]
        holder.bind(item, context)
    }

    /** * @author Andrea Piñeiro Cavazos <a01705681@itesm.mx>
     *  Inner class de View Holder, donde se van a mostrar los datos
     *  @param view
     *  @param onItemClicked función para mostrar la información de la denuncia al hacer click.
     *  @version 1.0
     */
    inner class ViewHolder(
        view: View,
        private val onItemClicked: (position: Int) -> Unit
    ): RecyclerView.ViewHolder(view), View.OnClickListener {

        private var fotoDenuncia: ImageView = view.findViewById(R.id.fotoDenuncia)
        private var denunciasTitle: TextView = view.findViewById(R.id.denunciasTitle)
        private var denunciaDescripcion: TextView = view.findViewById(R.id.denunciaDescripcion)

        init {
            view.setOnClickListener(this)
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
         * Función para hacer binding de los elementos en la vista.
         * Hace set del titulo, la descripción y la imagen con los datos correspondientes
         * @param item  Denuncia con los datos que queremos mostrar
         * @param context
         */
        fun bind(item: Denuncia, context: Context) {
            loadImages(item.fotos, fotoDenuncia)
            denunciasTitle.setText(item.idSitio?.nombre)
            denunciaDescripcion.setText(item.descripcion)
        }
        /**
         * Función que sobrescribe OnClick y manda llamar la función recibida como parámatro
         * por el adaptador, para que al dar click en un item,
         * se abra la información de esa denuncia.
         * @param view
         */

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }

    }

}
