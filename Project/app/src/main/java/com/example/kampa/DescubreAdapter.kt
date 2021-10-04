package com.example.kampa

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.models.Publicacion
import com.parse.GetDataCallback
import com.parse.ParseFile
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kampa.models.Sitio
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.widget.Toast
import com.parse.Parse
import com.parse.ParseQuery
import java.util.*


class DescubreAdapter(private val context: Context, private val data: ArrayList<Publicacion>): RecyclerView.Adapter<DescubreAdapter.CardViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DescubreAdapter.CardViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        return CardViewHolder(layoutInflater.inflate(R.layout.swipe_card_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size;
    }

    override fun onBindViewHolder(holder: DescubreAdapter.CardViewHolder, position: Int) {
        var item: Publicacion = data[position]
        holder.bind(item, context)
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var swipeCardImage: ImageView = view.findViewById(R.id.swipeCardImage)
        private var swipeCardName: TextView = view.findViewById(R.id.swipeCardName)
        private var swipeCardDescription: TextView = view.findViewById(R.id.swipeCardDescription)

        private fun loadImages(foto: ParseFile?, imgView: ImageView){
            if (foto != null) {
                foto.getDataInBackground(GetDataCallback { data, e ->
                    if (e == null) {
                        val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
                        imgView.setImageBitmap(bmp)
                    }
                    else{

                    }
                })
            }
            else{

            }
        }

        fun bind(item: Publicacion, context: Context) {
            loadImages(item.foto, swipeCardImage)

            //var sitio = item.get("Sitio")
            //swipeCardName.setText(sitio.get("nombre").toString())

            swipeCardName.setText(item.idSitio?.nombre)
            swipeCardDescription.setText(item.descripcion.toString())
        }
    }


}