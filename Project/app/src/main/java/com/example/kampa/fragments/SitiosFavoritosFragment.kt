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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.Constantes
import com.example.kampa.MainActivity
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

/** * @author RECON
 *  Fragmento para visualizar los sitios de una lista de favoritos seleccionada
 *  previamente por el usuario.
 *  @version 1.0
 */
class SitiosFavoritosFragment : Fragment(), SitioInterface {

    val TAG = "SitiosFavoritosFragment"

    private lateinit var tvTitle: TextView
    private lateinit var rvSitiosFavoritos: RecyclerView
    private lateinit var ibChangeName: ImageButton

    private lateinit var wishlist: Wishlist
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var sitiosFavoritosAdapter: SitiosFavoritosAdapter

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
        return inflater.inflate(R.layout.fragment_sitios_favoritos, container, false)
    }

    /**
     * Se llama después de que se crea la vista, se incializan los componentes
     * y manda llamar la funciones initializeArguments y getSitiosFavoritosList
     * @param view la vista inflada
     * @param savedInstanceState representa una instancia creada previamente
     */
    override fun onViewCreated( view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTitle = view.findViewById(R.id.tvTitle)
        rvSitiosFavoritos = view.findViewById(R.id.rvSitiosFavoritos)
        ibChangeName = view.findViewById(R.id.ibChangeName)

        initializeArguments()
        getSitiosFavoritosList()
    }

    /**
     * Obtenemos la wishlist (lista de favoritos) seleccionada por el usuario
     * y le damos título a la vista. La lista de favoritos viene en los argumentos
     * del fragmento
     */
    private fun initializeArguments() {
        wishlist = arguments?.getParcelable<Wishlist>(Constantes.WISHLIST)!!
        tvTitle.text = wishlist.nombre.toString()

        /**
         * OnClickListener para cambiar el nombre de la actual lista de favoritos
         */
        ibChangeName.setOnClickListener {
            changeName()
        }
    }

    /**
     * Función que llama a la base de datos y obtiene todos los sitios relacionados
     * con la lista de deseos seleccionada previamente. Después de esto, se llama al adaptador
     * para desplegar los sitios en el layout
     */
    private fun getSitiosFavoritosList() {
        val query: ParseQuery<WishlistSitio> = ParseQuery.getQuery(WishlistSitio::class.java)

        query.include(Constantes.ID_SITIO)
        query.whereEqualTo(Constantes.ID_WISHLIST, wishlist)
        query.findInBackground { objects: MutableList<WishlistSitio>?, e: ParseException? ->
            if (e == null) {
                if (objects != null) {
//                    for (objeto in objects) {
//                        if (objeto.idSitio?.objectId == null) {
//                            objects.remove(objeto)
//                        }
//                    }
                    initializeList(objects)
                }
            }
        }
    }

    /**
     * Función que recibe una lista de sitios, crea el adaptador con ellos y
     * los despliega en la recycler view del layout.
     * @param sitiosFavoritosList es la lista con los sitios relacionados a la lista
     * de favoritos que el usuario dio click
     */
    private fun initializeList(sitiosFavoritosList: MutableList<WishlistSitio>) {
        linearLayoutManager = LinearLayoutManager(this.context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        sitiosFavoritosAdapter = SitiosFavoritosAdapter(
            this.context,
            sitiosFavoritosList,
            this@SitiosFavoritosFragment,
        requireActivity() as MainActivity)

        rvSitiosFavoritos.layoutManager = linearLayoutManager
        rvSitiosFavoritos.adapter = sitiosFavoritosAdapter
        rvSitiosFavoritos.itemAnimator = DefaultItemAnimator()

        initializeGesture()
    }

    /**
     * Función que inicializa la gesture para deslizar un sitio de la lista
     * y al hacerlo, eliminarlo.
     */
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

    /**
     * Función que despliega un diálogo donde se podrá cambiar el nombre de la lista de favoritos
     * en la que nos encontramos
     */
    private fun changeName() {
        val myDialogView = LayoutInflater
            .from(this.context)
            .inflate(R.layout.cambiar_nombre_dialogo, null)
        val etNuevoNombre = myDialogView.findViewById(R.id.etNuevoNombre) as EditText
        val builder = AlertDialog.Builder(this.context)
            .setView(myDialogView)
            .setTitle(R.string.cambiar_nombre_lista_deseos)
            .setPositiveButton(R.string.aceptar,
                DialogInterface.OnClickListener { dialog, id ->
                    val nuevoNombre = etNuevoNombre.text.toString()

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

    /**
     * Método abstracto de la interfaz SitioInterface. Mostrará un diálogo y el usuario
     * podrá seleccionar si ir o no a la aplicación de Google Maps para saber cómo
     * llegar a un sitio seleccionado.
     * @param sitio es el sitio seleccionado por el usuario
     */
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
