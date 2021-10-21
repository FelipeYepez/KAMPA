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
import com.example.kampa.adapters.DescubreAdapter
import com.example.kampa.R
import com.example.kampa.models.*
import com.parse.*
import com.yuyakaido.android.cardstackview.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "AprobarFragment"

/**
 * A simple [Fragment] subclass.
 * Use the [DescubreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AprobarFragment : Fragment(), CardStackListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var cardStackLayoutManager: CardStackLayoutManager
    private lateinit var adapter: DescubreAdapter
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
        return inflater.inflate(R.layout.fragment_aprobar, container, false)
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
        val eliminarBtn: ImageButton = view.findViewById(R.id.btnAprobarEliminar)
        eliminarBtn.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            swipeCard.swipe()
        }


        val aprobarBtn: ImageButton = view.findViewById(R.id.btnAprobarCheck)
        aprobarBtn.setOnClickListener {
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
        var elements:Int = 0
        query.include(Constantes.ID_SITIO)
        query.findInBackground { itemList, e ->
            if (e == null) {
                for(element in itemList) {
                    if(element.eliminada == false && element.aprobada == false){
                        data.add(element)
                        elements += 1
                    }
                }
                if(elements == 0){
                    Toast.makeText(requireContext(), "No hay publicaciones pendientes", Toast.LENGTH_SHORT).show()
                }
                initializeList()
            } else {
                Log.e("item", "Error code: ${e.code}", e)
                if(e.code == 100){
                    Toast.makeText(requireContext(), "No hay conexión a Internet", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun initializeList(){
        cardStackLayoutManager.setStackFrom(StackFrom.Top)
        cardStackLayoutManager.setTranslationInterval(4.0f)
        cardStackLayoutManager.setCanScrollHorizontal(true)
        cardStackLayoutManager.setCanScrollVertical(true)
        swipeCard.layoutManager = cardStackLayoutManager
        adapter = DescubreAdapter( data)
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
        // Se aprueba o elimina a una publicación
        if(direction == "Right" || direction == "Left"){

            // Si se elimina
            if (direction == "Left") {
                publicacion.eliminada = true
            }
            // Si se aprueba
            else if (direction == "Right") {
                publicacion.aprobada = true
            }

            // Guardar en Parse publicacion actualizada
            publicacion.saveInBackground()

        }
        else{
            rewindCard()
        }
    }


    override fun onCardSwiped(direction: Direction?) {
        swipeParse(lastPublicacionCardDisappeared, direction.toString())
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        lastPublicacionCardDisappeared = data[position]
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
    }
}