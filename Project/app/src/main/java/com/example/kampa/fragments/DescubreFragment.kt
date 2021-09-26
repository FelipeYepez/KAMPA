package com.example.kampa.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kampa.DescubreAdapter
import com.example.kampa.MainActivity
import com.example.kampa.R
import com.example.kampa.models.Publicacion
import com.example.kampa.models.Sitio
import com.parse.ParseQuery
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
    private lateinit var data: List<Publicacion>
    private lateinit var publcaci: Publicacion

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
        //cardStackLayoutManager = CardStackLayoutManager(requireContext(), this).apply {
        cardStackLayoutManager = CardStackLayoutManager(requireContext(), this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }
        initializeData()

    }

    private fun initializeData(){
        val query: ParseQuery<Publicacion> = ParseQuery.getQuery(Publicacion::class.java)
        // Execute the find asynchronously
        query.findInBackground { itemList, e ->
            if (e == null) {
                for(element in itemList){
                    publcaci = element
                }
                //data = itemList
                initializeList()
            } else {
                Log.d("item", "Error: " + e.message)
            }
        }

    }

    private fun initializeList(){
        swipeCard.layoutManager = cardStackLayoutManager
        //adapter = DescubreAdapter(requireContext(), data)
        adapter = DescubreAdapter(requireActivity(), publcaci)
        //swipeCard.adapter = adapter

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

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        TODO("Not yet implemented")
    }

    override fun onCardSwiped(direction: Direction?) {
        TODO("Not yet implemented")
    }

    override fun onCardRewound() {
        TODO("Not yet implemented")
    }

    override fun onCardCanceled() {
        TODO("Not yet implemented")
    }

    override fun onCardAppeared(view: View?, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        TODO("Not yet implemented")
    }
}