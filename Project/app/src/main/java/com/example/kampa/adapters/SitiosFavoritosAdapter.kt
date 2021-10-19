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
import com.example.kampa.models.Wishlist
import com.example.kampa.models.WishlistSitio
import com.parse.ParseFile
import com.parse.ParseObject

class SitiosFavoritosAdapter(private val context: Context?,
                             private var data: MutableList<WishlistSitio>)
    : RecyclerView.Adapter<SitiosFavoritosAdapter.ViewHolder>() {

    private val TAG = "SitiosFavoritosAdapter"

    override fun onCreateViewHolder(
        parent: android.view.ViewGroup,
        viewType: Int
    ): ViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(
                R.layout.item_sitios_favoritos,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        data.removeAt(position)
        notifyDataSetChanged()
    }

    fun getItem(position : Int): WishlistSitio {
        return data.get(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: WishlistSitio = data[position]
        if (context != null) {
            holder.bind(item, context)
        }
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private var ivFotoSitio: ImageView = view.findViewById(R.id.ivFotoSitio)
        private var tvSitioTitle: TextView = view.findViewById(R.id.tvSitioTitle)
        private var tvSitioDescription: TextView = view.findViewById(R.id.tvSitioDescription)
        private var ibDirections: ImageButton = view.findViewById(R.id.ibDirections)
        private lateinit var mainActivity: MainActivity

        init {
            view.setOnClickListener(this)
        }

        fun bind(item: WishlistSitio, context: Context) {
            tvSitioTitle.text = item.idSitio?.nombre
            tvSitioDescription.text = item.idSitio?.descripcion

            // TODO: Click Listener to ibDirections and to the item

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

        override fun onClick(v: View?) {
            val position = adapterPosition
            val sitio = data[position]
            val context = v?.context
            val i = Intent(context, SitioActivity::class.java)
            i.putExtra("permission", mainActivity.isPermissionGranted)
            i.putExtra("currentLocation", sitio.idSitio?.ubicacion)
            i.putExtra("sitio", sitio.idSitio)
            context?.startActivity(i)
        }
    }
}