package com.descubre.kampa.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.descubre.kampa.R
import com.descubre.kampa.SwipeGestureDelete
import com.descubre.kampa.adapters.FavoritosAdapter
import com.descubre.kampa.interfaces.SitiosFavoritosInterface
import com.descubre.kampa.models.Wishlist
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

/** * @author RECON
 *  Fragmento para visualizar las listas de favoritos de un usuario.
 *  @version 1.0
 */
class FavoritosFragment : Fragment(), SitiosFavoritosInterface {

    val TAG = "FavoritosFragment"

    private lateinit var tvTitle: TextView
    private lateinit var rvFavoritos: RecyclerView
    private lateinit var fabAddWishlist: FloatingActionButton

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var favoritosAdapter: FavoritosAdapter

    /**
     * Se llama cuando el fragmento se crea, en esta función se
     * crea el layout y se infla la vista
     * @param inflater inflador de la vista
     * @param container es el que contiene las vistas para inflar el layout
     * @param savedInstanceState representa una instancia creada previamente
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        return inflater.inflate(R.layout.fragment_favoritos, container, false)
    }

    /**
     * Se llama después de que se crea la vista, se incializan los componentes
     * y manda llamar la función getFavoritosList
     * @param view la vista inflada
     * @param savedInstanceState representa una instancia creada previamente
     */
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

    /**
     * Método que muestra un diálogo en el que el usuario puede crear una nueva lista de deseos
     * con solo ingresar el nombre de esta nueva lista.
     */
    private fun addWishlist() {
        val myDialogView = LayoutInflater
            .from(this.context)
            .inflate(R.layout.crear_lista_favoritos_dialogo, null)
        val etNuevoNombre = myDialogView.findViewById(R.id.etNuevoNombre) as EditText
        val builder = AlertDialog.Builder(this.context)
            .setView(myDialogView)
            .setTitle(R.string.nueva_lista_favoritos)
            .setPositiveButton(R.string.crear,
                DialogInterface.OnClickListener { dialog, id ->
                    val nuevoNombre = etNuevoNombre.text.toString()

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
                        Toast.makeText(this.context, R.string.nombre_vacio, Toast.LENGTH_SHORT).show()
                    }
                })
            .setNegativeButton(R.string.cancelar,
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

        builder.show()
    }

    /**
     * Se llma a la base de datos y se obtienen todas las listas de sitios favoritos para
     * posteriormente desplegarlas en el layout.
     */
    private fun getFavoritosList() {
        val query: ParseQuery<Wishlist> = ParseQuery.getQuery(Wishlist::class.java)

        query.whereEqualTo(com.descubre.kampa.Constantes.ID_USUARIO, ParseUser.getCurrentUser())
        query.whereEqualTo(com.descubre.kampa.Constantes.IS_DELETED, false)
        query.orderByDescending(com.descubre.kampa.Constantes.CREATED_AT)
        query.findInBackground { objects: MutableList<Wishlist>?, e: ParseException? ->
            if (e == null) {
                if (objects != null) {
                    initializeList(objects)
                }
            } else if(e.code == 100){
                Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Función que recibe una lista de listas de desos, crea el adaptador con ellos y
     * los despliega en la recycler view del layout.
     * @param favoritosList es la lista de listas de favoritos obtenidas en la base de datos
     */
    private fun initializeList(favoritosList: MutableList<Wishlist>) {
        linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        favoritosAdapter = FavoritosAdapter(
            this.context,
            favoritosList,
            this@FavoritosFragment)

        rvFavoritos.layoutManager = linearLayoutManager
        rvFavoritos.adapter = favoritosAdapter
        rvFavoritos.itemAnimator = DefaultItemAnimator()

        initializeGesture()
    }

    /**
     * Función que inicializa la gesture para deslizar una lista de favoritos
     * y al hacerlo, eliminarla.
     */
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

    /**
     * Función que despliega un diálogo donde se podrá cambiar el nombre de la lista de favoritos
     * en la que nos encontramos
     */
    override fun passData(wishlist: Wishlist) {
        val bundle = Bundle()

        bundle.putParcelable(com.descubre.kampa.Constantes.WISHLIST, wishlist)

        val transaction = this.parentFragmentManager.beginTransaction()
        val sitiosFavoritosFragment = SitiosFavoritosFragment()
        sitiosFavoritosFragment.arguments = bundle

        transaction.replace(R.id.fragmentContainer, sitiosFavoritosFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}