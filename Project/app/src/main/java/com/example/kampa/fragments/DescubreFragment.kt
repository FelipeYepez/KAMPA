package com.example.kampa.fragments

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
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.kampa.Constantes
import com.example.kampa.DescubreAdapter
import com.example.kampa.R
import com.example.kampa.models.*
import com.google.android.material.snackbar.Snackbar
import com.parse.*
import com.yuyakaido.android.cardstackview.*


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
        val query: ParseQuery<Publicacion> = ParseQuery.getQuery(Publicacion::class.java)
        data = ArrayList()
        query.include(Constantes.ID_SITIO)
        query.findInBackground { itemList, e ->
            if (e == null) {
                for(element in itemList) {
                    data.add(element)
                }
                initializeList()
            } else {
                Log.d("item", "Error: " + e.message)
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

    private fun swipeParse(publicacion: Publicacion, direction: String) {
        // Se dio like, dislike o guardar en WishList a una publicación
        if(direction == "Right" || direction == "Left" || direction == "Top"){

            // Si fue dislike
            if (direction == "Left") {
                publicacion.increment(Constantes.NUM_DISLIKES)
            }
            // Si fue like
            else if (direction == "Right") {
                publicacion.increment(Constantes.NUM_LIKES)
            }
            // si fue se guardó sitio en Wishlist
            else if (direction == "Top") {
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
                            Toast.makeText(requireContext(), "Ya existe en tu Wishlist", Toast.LENGTH_LONG).show()
                            rewindCard()
                            return@GetCallback
                        }
                        // Sitio no existe en Wishlist, se lo asigna a Wishlist
                        else{
                            // Modificar Likes o Dislikes totales de Publicación
                            publicacion.increment(Constantes.NUM_LIKES, 2)
                            usuarioSitio.put(Constantes.IS_WISHLIST, true)
                            usuarioSitio.saveInBackground()
                        }
                    }
                    else {
                        // No se obtuvo resultado de GetCallBack
                        if (e.code == 101) {
                            // Modificar Likes o Dislikes totales de Publicación
                            publicacion.increment(Constantes.NUM_LIKES, 2)

                            val usuarioSitioNuevo = UsuarioSitio()
                            usuarioSitioNuevo.idSitio = publicacion.idSitio
                            usuarioSitioNuevo.idUsuario = ParseUser.getCurrentUser()
                            usuarioSitioNuevo.isWishlist = true
                            usuarioSitioNuevo.saveInBackground()
                        } else {
                            Log.e("Error", "No se guarda con wishlist", e)
                            rewindCard()
                            return@GetCallback
                        }
                    }
                })
            }
            // Guardar en Parse Publicacion actualizada con Likes o Dislikes
            publicacion.saveInBackground()


            // Modificar Tags de Publicacion relacionadas con Usuario pertenecientes a Publicacion
            // Lista para modificar Tags de Publicacion relacionadas con usuario
            val listUsuarioTag = ArrayList<UsuarioTag>()

            // Parse Query para obtener Tags de Publicacion
            val query: ParseQuery<PublicacionTag> = ParseQuery.getQuery(PublicacionTag::class.java)
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
                            tagsPublicacionCrear: List<PublicacionTag>, e ->
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
                            }
                        }
                    })
                }

            })
        }
        else{
            rewindCard()
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        //TODO("Not yet implemented")
        //Log.d("Dragging", direction.toString())
    }

    override fun onCardSwiped(direction: Direction?) {
        //TODO("Not yet implemented")
        swipeParse(lastPublicacionCardDisappeared, direction.toString())
        //Log.d("direction", direction.toString())
        //lastPublicacionCardDissapeared = null

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
        // Log.d("Publicacion", data[position].descripcion.toString())
        //TODO("Not yet implemented")
    }
}