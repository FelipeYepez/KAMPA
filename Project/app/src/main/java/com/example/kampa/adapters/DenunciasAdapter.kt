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

class DenunciasAdapter(private val context: Context,
                       private val data: ArrayList<Denuncia>,
                       private val onItemClicked: (position: Int) -> Unit):
    RecyclerView.Adapter<DenunciasAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DenunciasAdapter.ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_denuncias, parent, false), onItemClicked)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Denuncia = data[position]
        holder.bind(item, context)
    }

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

        fun bind(item: Denuncia, context: Context) {
            loadImages(item.fotos, fotoDenuncia)
            denunciasTitle.setText(item.idSitio?.nombre)
            denunciaDescripcion.setText(item.descripcion)
        }

        override fun onClick(v: View) {
            val position = adapterPosition
            onItemClicked(position)
        }

    }

}
