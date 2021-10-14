package com.example.kampa.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.Constantes
import com.example.kampa.R
import com.example.kampa.SwipeGestureDelete
import com.example.kampa.adapters.SitiosFavoritosAdapter
import com.example.kampa.interfaces.SitioInterface
import com.example.kampa.models.Sitio
import com.example.kampa.models.Wishlist
import com.example.kampa.models.WishlistSitio
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.parse.ParseException
import com.parse.ParseQuery
import kotlinx.android.synthetic.main.cambiar_nombre_dialogo.view.*

class SitiosFavoritosFragment : Fragment(), SitioInterface {

    val TAG = "SitiosFavoritosFragment"

    private lateinit var tvTitle: TextView
    private lateinit var rvSitiosFavoritos: RecyclerView
    private lateinit var ibChangeName: ImageButton

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
        ibChangeName = view.findViewById(R.id.ibChangeName)

        initializeArguments()
        getSitiosFavoritosList()
    }

    private fun initializeArguments() {
        wishlist = arguments?.getParcelable<Wishlist>(Constantes.WISHLIST)!!
        tvTitle.text = wishlist.nombre.toString()

        ibChangeName.setOnClickListener {
            changeName()
        }
    }

    private fun getSitiosFavoritosList() {
        val query: ParseQuery<WishlistSitio> = ParseQuery.getQuery(WishlistSitio::class.java)

        query.include(Constantes.ID_SITIO)
        query.whereEqualTo(Constantes.ID_WISHLIST, wishlist)
        query.findInBackground { objects: MutableList<WishlistSitio>?, e: ParseException? ->
            if (e == null) {
                if (objects != null) {
                    initializeList(objects)
                }
            }
        }
    }

    private fun initializeList(sitiosFavoritosList: MutableList<WishlistSitio>) {
        linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        sitiosFavoritosAdapter = SitiosFavoritosAdapter(
            this.context,
            sitiosFavoritosList,
            this@SitiosFavoritosFragment)

        rvSitiosFavoritos.layoutManager = linearLayoutManager
        rvSitiosFavoritos.adapter = sitiosFavoritosAdapter
        rvSitiosFavoritos.itemAnimator = DefaultItemAnimator()

        initializeGesture()
    }

    private fun initializeGesture() {
        val swipeGesture = object : SwipeGestureDelete(this.context) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val position : Int = viewHolder.adapterPosition
                        val sitioEliminado : WishlistSitio = sitiosFavoritosAdapter.getItem(position)
                        sitiosFavoritosAdapter.deleteItem(position)
                        sitioEliminado.deleteInBackground()
                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(rvSitiosFavoritos)
    }

    private fun changeName() {
        val myDialogView = LayoutInflater
            .from(this.context)
            .inflate(R.layout.cambiar_nombre_dialogo, null)

        val builder = AlertDialog.Builder(this.context)
            .setView(myDialogView)
            .setTitle(R.string.cambiar_nombre_lista_deseos)
            .setPositiveButton(R.string.aceptar,
                DialogInterface.OnClickListener { dialog, id ->
                    val nuevoNombre = myDialogView.etNuevoNombre.text.toString()

                    if (nuevoNombre.isNotEmpty()) {
                        tvTitle.text = nuevoNombre
                        wishlist.nombre = nuevoNombre
                        wishlist.saveInBackground()
                    } else {
                        Toast.makeText(this.context, R.string.nombre_vacio, Toast.LENGTH_SHORT).show()
                    }
                })
            .setNegativeButton(R.string.cancelar,
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

        builder.show()
    }

    override fun passSitio(sitio: Sitio) {
        Log.d(TAG, sitio.nombre.toString())

        MaterialAlertDialogBuilder(this.requireContext())
            .setTitle(resources.getString(R.string.abrir_google_maps))
            .setMessage(resources.getString(R.string.ir_a_google_maps))
            .setNegativeButton(resources.getString(R.string.cancelar)) { dialog, which ->
            }
            .setPositiveButton(resources.getString(R.string.aceptar)) { dialog, which ->
                val latitude: String = (sitio.ubicacion?.latitude!!).toString()
                val longitude: String = (sitio.ubicacion?.longitude!!).toString()
                val gmmIntentUri = Uri.parse("google.navigation:q=$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            .show()
    }
}
