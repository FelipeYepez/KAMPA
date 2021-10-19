package com.example.kampa.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.kampa.Constantes
import com.example.kampa.DescubreAdapter
import com.example.kampa.R
import com.example.kampa.models.*
import com.example.kampa.models.Wishlist
import com.parse.*
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.cambiar_nombre_dialogo.view.*
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import android.graphics.Color

import android.widget.TextView
import androidx.core.content.ContextCompat

import com.google.android.material.snackbar.Snackbar





// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DescubreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DescubreFragment : Fragment(), CardStackListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var adapter:DescubreAdapter
    private lateinit var swipeCard: CardStackView
    private lateinit var data: ArrayList<Publicacion>
    private lateinit var lastPublicacionCardDisappeared: Publicacion
    private lateinit var v: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_descubre, container, false)
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeCard = view.findViewById(R.id.swipeCard)
        cardStackLayoutManager = CardStackLayoutManager(requireContext(), this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }
        v = view
        setupButton(view)
        initializeData()

    }

    private fun setupButton(view: View) {
        val dislikeBtn: ImageButton = view.findViewById(R.id.btnDescubreDislike)
        dislikeBtn.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            swipeCard.swipe()
        }

        val wishListBtn: ImageButton = view.findViewById(R.id.btnDescubreWishlist)
        wishListBtn.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Top)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            swipeCard.swipe()
        }

        val likeBtn: ImageButton = view.findViewById(R.id.btnDescubreLike)
        likeBtn.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            swipeCard.swipe()
        }
    }

    private fun initializeData(){
        data = ArrayList()

        // Obtener Publicaciones con las que ya iteractuo el usuario
        val queryOldPublicaciones: ParseQuery<PublicacionUsuario> = ParseQuery.getQuery(PublicacionUsuario::class.java)
        queryOldPublicaciones.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())

        // Obtener nuevas publicaciones que no ha visto aún el usuario
        val queryNewPublicaciones: ParseQuery<Publicacion> = ParseQuery.getQuery(Publicacion::class.java)
        queryNewPublicaciones.whereDoesNotMatchKeyInQuery(Constantes.OBJECT_ID, "${Constantes.ID_PUBLICACION}.${Constantes.OBJECT_ID}", queryOldPublicaciones)
        queryNewPublicaciones.whereEqualTo(Constantes.ELIMINADA, false)
        queryNewPublicaciones.whereEqualTo(Constantes.APROBADA, true)
        queryNewPublicaciones.include(Constantes.ID_SITIO)
        queryNewPublicaciones.findInBackground { newPublicaciones, e ->
            if (e != null) {
                Log.e("item", "Error code: ${e.code}", e)
                if(e.code == 100){
                    val snack = Snackbar.make(v, R.string.error_conexion, Snackbar.LENGTH_LONG)
                    snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
                    snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    snack.show()
                }
                return@findInBackground
            }
            else {

                // Obtener tags relacionados a cada Publicacion nueva para el usuario
                val queryTagsPublicaciones: ParseQuery<PublicacionTags> = ParseQuery.getQuery(PublicacionTags::class.java)
                queryTagsPublicaciones.whereMatchesQuery(Constantes.ID_PUBLICACION, queryNewPublicaciones)
                queryTagsPublicaciones.findInBackground(FindCallback{
                        tagsPublicaciones: List<PublicacionTags>, e ->
                    if(e != null){
                        Log.e("Error", "No carga tags de publicaciones", e)
                        return@FindCallback
                    }
                    else{
                        // Obtener puntuacion de cada tag segun usuario
                        val queryTagsUsuario: ParseQuery<UsuarioTag> = ParseQuery.getQuery(UsuarioTag::class.java)
                        queryTagsUsuario.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())
                        queryTagsUsuario.whereMatchesKeyInQuery(Constantes.ID_TAG, Constantes.ID_TAG, queryTagsPublicaciones)
                        queryTagsUsuario.findInBackground{tagsUsuario, e ->
                            if(e != null){
                                Log.e("Error", "No carga tags de usuario", e)
                                return@findInBackground
                            }
                            // Se obtuvieron todos los tags para realizar priorizacion
                            else{
                                // Crear JSON de tags usuario para acceder en tiempo constante
                                var jsonUsuarioTags = JSONObject()
                                for(tagUsuario in tagsUsuario){
                                    val dif: Int = tagUsuario.numLikes!! - tagUsuario.numDislikes!!
                                    jsonUsuarioTags.put(tagUsuario.idTag!!.objectId.toString(), dif)
                                }

                                // Crear Arreglo para almacenar tuplas
                                var publicacionesPriorityOrder: ArrayList<Tuple> = ArrayList()

                                // Almacenar tuplas de publicacion con prioridad
                                // Recorrer cada publicacion con cada uno de sus tags
                                // sumar prioridad de publicacion segun likes de usuario a tag
                                for(publicacion in newPublicaciones){
                                    // Inicializar prioridad 0
                                    var prioridadPublicacion: Int = 0
                                    for(tagPublicacion in tagsPublicaciones){
                                        // Verificar que tag pertenezca a publicacion
                                        if(tagPublicacion.idPublicacion!!.objectId == publicacion.objectId){
                                            // Comprobar si usuario cuenta con likes a tag
                                            if(jsonUsuarioTags.has(tagPublicacion.idTag!!.objectId.toString())){
                                                // Aumentar prioridad segun gusto de usuario
                                                prioridadPublicacion += jsonUsuarioTags.getInt(tagPublicacion.idTag!!.objectId.toString())
                                            }
                                        }
                                    }
                                    // agregar tupla de publicacion con prioridad
                                    publicacionesPriorityOrder.add(Tuple(publicacion, prioridadPublicacion))
                                }

                                // ordenar lista de publicacion de mayor a menor prioridad
                                val sorted: List<Tuple> = publicacionesPriorityOrder
                                    .sortedWith { t1, t2 -> t2.prioridad - t1.prioridad }

                                // Agregar publicaciones en orden de prioridad a arreglo que
                                // será enviado al adapter
                                for(element in sorted) {
                                    data.add(element.publicacion)
                                }
                                initializeList()
                            }
                        }
                    }
                })
            }
        }

    }

    private fun initializeList(){
        cardStackLayoutManager.setStackFrom(StackFrom.Top)
        cardStackLayoutManager.setTranslationInterval(4.0f)
        cardStackLayoutManager.setCanScrollHorizontal(true)
        cardStackLayoutManager.setCanScrollVertical(true)
        swipeCard.layoutManager = cardStackLayoutManager
        adapter = DescubreAdapter(requireContext(), data)
        swipeCard.adapter = adapter

        swipeCard.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DescubreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DescubreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun rewindCard(){
        val setting = RewindAnimationSetting.Builder()
            .setDirection(Direction.Bottom)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(DecelerateInterpolator())
            .build()
        cardStackLayoutManager.setRewindAnimationSetting(setting)
        swipeCard.rewind()
    }

    private fun actualizarTags(publicacion: Publicacion, direction: String){
        // Modificar Tags de Publicacion relacionadas con Usuario pertenecientes a Publicacion
        // Lista para modificar Tags de Publicacion relacionadas con usuario
        val listUsuarioTag = ArrayList<UsuarioTag>()

        // Parse Query para obtener Tags de Publicacion
        val query: ParseQuery<PublicacionTags> = ParseQuery.getQuery(PublicacionTags::class.java)
        query.whereEqualTo(Constantes.ID_PUBLICACION, publicacion)
        // Parse Query obtiene Tags relacionados a Usuario en cuestión
        val query2: ParseQuery<UsuarioTag> = ParseQuery.getQuery(UsuarioTag::class.java)
        query2.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())

        // Parse Query obtiene tags ya existentes con usuario para modificarlas
        query2.whereMatchesKeyInQuery(Constantes.ID_TAG, Constantes.ID_TAG, query)
        query2.findInBackground(FindCallback { usuarioTags: List<UsuarioTag>, e ->
            if (e != null) {
                rewindCard()
                return@FindCallback
            } else {
                // Iterar Lista de Query para almacenar Objetos Modificados
                for (usuarioTag in usuarioTags) {
                    if (direction == "Left") {
                        usuarioTag.increment(Constantes.NUM_DISLIKES)
                    } else if (direction == "Right") {
                        usuarioTag.increment(Constantes.NUM_LIKES)
                    } else if (direction == "Top") {
                        usuarioTag.increment(Constantes.NUM_LIKES, 2)
                    }
                    // Almacenar Objeto modificado en lista
                    listUsuarioTag.add(usuarioTag)
                }

                // Parse Query para relacionar nuevos tags con usuario
                val query3: ParseQuery<UsuarioTag> = ParseQuery.getQuery(UsuarioTag::class.java)
                query3.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())

                // Parse Query obtiene tags de Publicacion sin relacionar con usuario
                query.whereDoesNotMatchKeyInQuery(Constantes.ID_TAG, Constantes.ID_TAG, query3)
                query.findInBackground(FindCallback {
                        tagsPublicacionCrear: List<PublicacionTags>, e ->
                    if (e != null) {
                        rewindCard()
                        return@FindCallback
                    } else {
                        // Itera Lista de Query para crear y almacenar Objetos
                        for (tagPubli in tagsPublicacionCrear) {
                            // Crea objeto y asigna respectivas características
                            val usuarioTag = UsuarioTag()
                            usuarioTag.idUsuario = ParseUser.getCurrentUser()
                            usuarioTag.idTag = tagPubli.idTag
                            if (direction == "Left") {
                                usuarioTag.numDislikes = 1
                            } else if (direction == "Right") {
                                usuarioTag.numLikes = 1
                            } else if (direction == "Top") {
                                usuarioTag.numLikes = 2
                            }
                            // Almacenar nuevo objeto en lista
                            listUsuarioTag.add(usuarioTag)
                        }
                        // Guardar en Parse tanto objetos nuevos como modificados
                        // Guarda relación entre Usuario con Tags de Publicación
                        ParseObject.saveAllInBackground(listUsuarioTag) { e ->
                            if (e != null) {
                                Log.e("Error", "error save all", e)
                                rewindCard()
                                return@saveAllInBackground
                            }
                            else{
                                val publicacionUsuario = PublicacionUsuario()
                                publicacionUsuario.idPublicacion = publicacion
                                publicacionUsuario.idUsuario = ParseUser.getCurrentUser()
                                publicacionUsuario.saveInBackground()
                            }
                        }
                    }
                })
            }

        })
    }

    private fun actualizarConWishlist(publicacion: Publicacion, direction: String){

        // Parse Query para guardar Sitio en WishList de Usuario
        val query: ParseQuery<UsuarioSitio> = ParseQuery.getQuery(UsuarioSitio::class.java)
        query.whereEqualTo(Constantes.ID_SITIO, publicacion.idSitio)
        query.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())

        // Parse Query para obtener primer registro de Parse
        query.getFirstInBackground(GetCallback { usuarioSitio: UsuarioSitio?, e ->
            // Si registro ya existe
            if (e == null && usuarioSitio != null) {
                // Si Sitio de Publicación ya existe en Wishlist
                if(usuarioSitio.isWishlist == true){
                    return@GetCallback
                }
                // Sitio no existe en Wishlist, se lo asigna a Wishlist
                else{
                    usuarioSitio.put(Constantes.IS_WISHLIST, true)
                    usuarioSitio.saveInBackground()
                }
            }
            else {
                // No se obtuvo resultado de GetCallBack, crear registro
                if (e.code == 101) {
                    val usuarioSitioNuevo = UsuarioSitio()
                    usuarioSitioNuevo.idSitio = publicacion.idSitio
                    usuarioSitioNuevo.idUsuario = ParseUser.getCurrentUser()
                    usuarioSitioNuevo.isWishlist = true
                    usuarioSitioNuevo.isVisitado = false
                    usuarioSitioNuevo.saveInBackground()
                } else {
                    Log.e("Error", "No se guarda con wishlist", e)
                    rewindCard()
                    return@GetCallback
                }
            }
        })

        // Modificar Likes o Dislikes totales de Publicación
        publicacion.increment(Constantes.NUM_LIKES, 2)
        // Guardar en Parse Publicacion actualizada con Likes o Dislikes
        publicacion.saveInBackground()
        actualizarTags(publicacion, direction)

    }

    private fun swipeParse(publicacion: Publicacion, direction: String) {
        // Se dio like, dislike o guardar en WishList a una publicación
        if(direction == "Right" || direction == "Left" || direction == "Top"){

            // Si fue dislike
            if (direction == "Left") {
                publicacion.increment(Constantes.NUM_DISLIKES)
                // Guardar en Parse Publicacion actualizada con Likes o Dislikes
                publicacion.saveInBackground()
                actualizarTags(publicacion, direction)
            }
            // Si fue like
            else if (direction == "Right") {
                publicacion.increment(Constantes.NUM_LIKES)
                // Guardar en Parse Publicacion actualizada con Likes o Dislikes
                publicacion.saveInBackground()
                actualizarTags(publicacion, direction)
            }
            // si fue se guardó sitio en Wishlist
            else if (direction == "Top") {

                val query1: ParseQuery<Wishlist> = ParseQuery.getQuery(Wishlist::class.java)

                query1.whereEqualTo(Constantes.ID_USUARIO, ParseUser.getCurrentUser())
                query1.whereEqualTo(Constantes.IS_DELETED, false)
                query1.orderByDescending(Constantes.CREATED_AT)
                query1.findInBackground { objects: MutableList<Wishlist>?, e: ParseException? ->
                    if (e == null) {
                        if (objects != null) {
                            val listFavs : MutableList<String> = mutableListOf()
                            for(obj in objects){
                                listFavs.add(obj.nombre.toString())
                            }
                            val arr : Array<String> = listFavs.toTypedArray()
                            val builder = AlertDialog.Builder(this.context)
                                .setTitle(R.string.Lista_favoritos)
                                .setItems(arr){dialog, which ->

                                    val queryExisteEnWishlist: ParseQuery<WishlistSitio> = ParseQuery.getQuery(WishlistSitio::class.java)
                                    queryExisteEnWishlist.whereEqualTo(Constantes.ID_SITIO, publicacion.idSitio)
                                    queryExisteEnWishlist.whereEqualTo(Constantes.ID_WISHLIST, objects[which])
                                    queryExisteEnWishlist.getFirstInBackground(GetCallback { wishListSitio: WishlistSitio?, e ->
                                        if(e != null){
                                            // Si aun no existe Sitio en Wishlist crearlo
                                            if(e.code == 101){
                                                val nWishlistSitio : WishlistSitio = WishlistSitio()
                                                nWishlistSitio.idWishlist = objects[which]
                                                nWishlistSitio.idSitio = publicacion.idSitio
                                                nWishlistSitio.saveInBackground { e ->
                                                    // Si se pudo guardar
                                                    if (e == null) {
                                                        // Se marca que ya existe en wishlist y se actualizan tags de publicacion
                                                        actualizarConWishlist(publicacion, direction)
                                                        val snack = Snackbar.make(v, R.string.Lista_favoritos_exito, Snackbar.LENGTH_SHORT)
                                                        snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                                                        snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.exito))
                                                        snack.show()
                                                    } else {
                                                        rewindCard()
                                                        dialog.cancel()
                                                        val snack = Snackbar.make(v, R.string.error_conexion, Snackbar.LENGTH_LONG)
                                                        snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
                                                        snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                                        snack.show()
                                                    }
                                                }
                                            }
                                            else{
                                                rewindCard()
                                                dialog.cancel()
                                                val snack = Snackbar.make(v, R.string.error_conexion, Snackbar.LENGTH_LONG)
                                                snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
                                                snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                                snack.show()
                                            }
                                        }
                                        else{
                                            rewindCard()
                                            dialog.cancel()
                                            val snack = Snackbar.make(v, "Ya existe ${publicacion.idSitio!!.nombre} en ${objects[which].nombre}", Snackbar.LENGTH_LONG)
                                            snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.amarilloEsenciaPatrimonio))
                                            snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                            snack.show()
                                        }
                                    })
                                }
                                .setPositiveButton(R.string.crear_lista,
                                    DialogInterface.OnClickListener { dialog, id ->
                                        val myDialogView = LayoutInflater
                                            .from(this.context)
                                            .inflate(R.layout.crear_lista_favoritos_dialogo, null)

                                        val builder2 = AlertDialog.Builder(this.context)
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
                                                            if (e == null) {
                                                                val nWishlistSitio : WishlistSitio = WishlistSitio()
                                                                nWishlistSitio.idWishlist = nuevaWishlist
                                                                nWishlistSitio.idSitio = publicacion.idSitio
                                                                nWishlistSitio.saveInBackground { e ->
                                                                    if (e == null) {
                                                                        val snack = Snackbar.make(v, R.string.Lista_favoritos_exito, Snackbar.LENGTH_SHORT)
                                                                        snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.white))
                                                                        snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.exito))
                                                                        snack.show()
                                                                        actualizarConWishlist(publicacion, direction)
                                                                    } else {
                                                                        rewindCard()
                                                                        dialog.cancel()

                                                                    }
                                                                }
                                                            } else {
                                                                rewindCard()
                                                                dialog.cancel()
                                                                val snack = Snackbar.make(v, R.string.error_conexion, Snackbar.LENGTH_LONG)
                                                                snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
                                                                snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                                                snack.show()
                                                            }
                                                        }
                                                    } else {
                                                        rewindCard()
                                                        val snack = Snackbar.make(v, R.string.nombre_vacio, Snackbar.LENGTH_LONG)
                                                        snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.amarilloEsenciaPatrimonio))
                                                        snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                                        snack.show()
                                                    }
                                                })
                                            .setNegativeButton(R.string.cancelar,
                                                DialogInterface.OnClickListener { dialog, id ->
                                                    rewindCard()
                                                    dialog.cancel()
                                                })

                                        builder2.show()
                                    })
                                .setNegativeButton(R.string.cancelar,
                                    DialogInterface.OnClickListener { dialog, id ->
                                        rewindCard()
                                        dialog.cancel()
                                    })
                            builder.show()
                        }
                    }
                    else{
                        if(e.code == 100){
                            val snack = Snackbar.make(v, R.string.error_conexion, Snackbar.LENGTH_LONG)
                            snack.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
                            snack.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            snack.show()
                        }
                        rewindCard()
                    }
                }


            }

        }
        else{
            rewindCard()
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        //TODO("Not yet implemented")
    }

    override fun onCardSwiped(direction: Direction?) {
        //TODO("Not yet implemented")
        swipeParse(lastPublicacionCardDisappeared, direction.toString())
    }

    override fun onCardRewound() {
        //TODO("Not yet implemented")
    }

    override fun onCardCanceled() {
        //TODO("Not yet implemented")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        lastPublicacionCardDisappeared = data[position]
        //TODO("Not yet implemented")
    }
}

// Clase para poder crear lista de publicaciones y ordenarlas segun prioridad
class Tuple(publi: Publicacion, priori: Int){
    val publicacion: Publicacion = publi
    val prioridad: Int = priori
}