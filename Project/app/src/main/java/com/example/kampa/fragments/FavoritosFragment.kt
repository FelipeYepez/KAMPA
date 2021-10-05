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
import com.example.kampa.R
import com.example.kampa.adapters.FavoritosAdapter
import com.example.kampa.models.Wishlist
import com.example.kampa.Constantes
import com.example.kampa.interfaces.SitiosFavoritosInterface
import com.parse.*


class FavoritosFragment : Fragment(), SitiosFavoritosInterface {

    val TAG = "FavoritosFragment"

    private lateinit var tvTitle: TextView
    private lateinit var rvFavoritos: RecyclerView

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var favoritosAdapter: FavoritosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle = view.findViewById(R.id.tvTitle)
        rvFavoritos = view.findViewById(R.id.rvFavoritos)

        getFavoritosList()
    }

    private fun getFavoritosList() {
        val query: ParseQuery<Wishlist> = ParseQuery.getQuery(Wishlist::class.java)

        // query.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser().toString())
        query.findInBackground { objects: List<Wishlist>?, e: ParseException? ->
            if (e == null) {
                if (objects != null) {
                    initializeList(objects)
                }
            }
        }
    }

    private fun initializeList(favoritosList: List<Wishlist>) {
        linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        favoritosAdapter = FavoritosAdapter(this.context, favoritosList, this@FavoritosFragment)

        rvFavoritos.layoutManager = linearLayoutManager
        rvFavoritos.adapter = favoritosAdapter
        rvFavoritos.itemAnimator = DefaultItemAnimator()
    }

    override fun passData(wishlist: Wishlist) {
        val bundle = Bundle()

        bundle.putParcelable(Constantes.WISHLIST, wishlist)

        val transaction = this.parentFragmentManager.beginTransaction()
        val sitiosFavoritosFragment = SitiosFavoritosFragment()
        sitiosFavoritosFragment.arguments = bundle

        transaction.replace(R.id.fragmentContainer, sitiosFavoritosFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}