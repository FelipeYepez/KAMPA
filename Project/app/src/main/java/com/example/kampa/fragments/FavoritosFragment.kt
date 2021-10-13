package com.example.kampa.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.R
import com.example.kampa.adapters.FavoritosAdapter
import com.example.kampa.models.Wishlist
import com.example.kampa.Constantes
import com.example.kampa.SwipeGestureDelete
import com.example.kampa.interfaces.SitiosFavoritosInterface
import com.example.kampa.models.WishlistSitio
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.*
import kotlinx.android.synthetic.main.cambiar_nombre_dialogo.view.*
import kotlinx.android.synthetic.main.fragment_favoritos.*
import com.parse.Parse





class FavoritosFragment : Fragment(), SitiosFavoritosInterface {

    val TAG = "FavoritosFragment"

    private lateinit var tvTitle: TextView
    private lateinit var rvFavoritos: RecyclerView
    private lateinit var fabAddWishlist: FloatingActionButton

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
        fabAddWishlist = view.findViewById(R.id.fabAdd)

        fabAddWishlist.setOnClickListener {
            addWishlist()
        }

        getFavoritosList()
    }

    private fun addWishlist() {
        val myDialogView = LayoutInflater
            .from(this.context)
            .inflate(R.layout.crear_lista_favoritos_dialogo, null)

        val builder = AlertDialog.Builder(this.context)
            .setView(myDialogView)
            .setTitle(R.string.nueva_lista_favoritos)
            .setPositiveButton(R.string.crear,
                DialogInterface.OnClickListener { dialog, id ->
                    val nuevoNombre = myDialogView.etNuevoNombre.text.toString()

                    if (nuevoNombre.isNotEmpty()) {
                        val nuevaWishlist: Wishlist = Wishlist()
                        nuevaWishlist.nombre = nuevoNombre
                        nuevaWishlist.idUsuario = ParseUser.getCurrentUser()

                        nuevaWishlist.saveInBackground { e ->
                            // Si se pudo guardar
                            if (e == null) {
                                favoritosAdapter.addToTop(nuevaWishlist)

                                var mensajeExito = getString(R.string.lista_favoritos_creada)
                                mensajeExito += nuevoNombre

                                Toast.makeText(this.context,
                                    mensajeExito,
                                    Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(this.context, R.string.error_conexion, Toast.LENGTH_SHORT).show()
                                dialog.cancel()
                            }
                        }
                    } else {
                        // Se queda el dialogo? o se va?
                            // si se queda mejor para que el usuario siga intentando
                        Toast.makeText(this.context, R.string.nombre_vacio, Toast.LENGTH_SHORT).show()
                    }
                })
            .setNegativeButton(R.string.cancelar,
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

        builder.show()
    }

    private fun getFavoritosList() {
        val query: ParseQuery<Wishlist> = ParseQuery.getQuery(Wishlist::class.java)

        query.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())
        query.whereEqualTo(Constantes.IS_DELETED, false)
        query.orderByDescending(Constantes.CREATED_AT)
        query.findInBackground { objects: MutableList<Wishlist>?, e: ParseException? ->
            if (e == null) {
                if (objects != null) {
                    initializeList(objects)
                }
            }
            else{
                if(e.code == 100){
                    Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializeList(favoritosList: MutableList<Wishlist>) {
        linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        favoritosAdapter = FavoritosAdapter(this.context, favoritosList, this@FavoritosFragment)

        rvFavoritos.layoutManager = linearLayoutManager
        rvFavoritos.adapter = favoritosAdapter
        rvFavoritos.itemAnimator = DefaultItemAnimator()

        initializeGesture()
    }

    private fun initializeGesture() {
        val swipeGesture = object : SwipeGestureDelete(this.context) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val position : Int = viewHolder.adapterPosition
                        val wishlistEliminada : Wishlist = favoritosAdapter.getItem(position)
                        favoritosAdapter.deleteItem(position)
                        wishlistEliminada.isDeleted = true
                        wishlistEliminada.saveInBackground()
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(rvFavoritos)
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