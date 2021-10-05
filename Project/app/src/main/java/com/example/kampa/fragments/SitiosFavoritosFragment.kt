package com.example.kampa.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.Constantes
import com.example.kampa.R
import com.example.kampa.adapters.SitiosFavoritosAdapter
import com.example.kampa.models.Wishlist
import com.example.kampa.models.WishlistSitio
import com.parse.ParseException
import com.parse.ParseQuery

class SitiosFavoritosFragment : Fragment() {

    val TAG = "SitiosFavoritosFragment"

    private lateinit var tvTitle: TextView
    private lateinit var rvSitiosFavoritos: RecyclerView

    private lateinit var wishlist: Wishlist
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var sitiosFavoritosAdapter: SitiosFavoritosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sitios_favoritos, container, false)
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle = view.findViewById(R.id.tvTitle)
        rvSitiosFavoritos = view.findViewById(R.id.rvSitiosFavoritos)

        initializeArguments()
        getSitiosFavoritosList()
    }

    private fun initializeArguments() {
        wishlist = arguments?.getParcelable<Wishlist>(Constantes.WISHLIST)!!
        tvTitle.text = wishlist.nombre.toString()
    }

    private fun getSitiosFavoritosList() {
        val query: ParseQuery<WishlistSitio> = ParseQuery.getQuery(WishlistSitio::class.java)

        query.include(Constantes.ID_SITIO)
        query.whereEqualTo(Constantes.ID_WISHLIST, wishlist)
        query.findInBackground { objects: List<WishlistSitio>?, e: ParseException? ->
            if (e == null) {
                if (objects != null) {
                    initializeList(objects)
                }
            }
        }
    }

    private fun initializeList(sitiosFavoritosList: List<WishlistSitio>) {
        linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        sitiosFavoritosAdapter = SitiosFavoritosAdapter(this.context, sitiosFavoritosList)

        rvSitiosFavoritos.layoutManager = linearLayoutManager
        rvSitiosFavoritos.adapter = sitiosFavoritosAdapter
        rvSitiosFavoritos.itemAnimator = DefaultItemAnimator()
    }

}