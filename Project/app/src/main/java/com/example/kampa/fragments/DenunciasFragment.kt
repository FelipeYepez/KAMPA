package com.example.kampa.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kampa.Constantes
import com.example.kampa.DenunciaActivity
import com.example.kampa.R
import com.example.kampa.adapters.DenunciasAdapter
import com.example.kampa.models.Denuncia
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseQuery

/** * @author RECON
 *  Fragmento para visualizar las denuncias de mal uso desde el rol de administrador,
 *  las denuncias se agrupan de acuerdo a su estado: Sin Resolver, Inválidas, Procesadas.
 *  @version 1.0
 */

class DenunciasFragment : Fragment() {

    val TAG = "DenunciasFragment"

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnSinResolver: Button
    private lateinit var btnProcesadas: Button
    private lateinit var btnInvalidas: Button
    private lateinit var data: ArrayList<Denuncia>

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var denunciasAdapter: DenunciasAdapter

    /**
     * Se llama cuando el fragmento se crea, en esta función se
     * crea el layour y se infla la vista
     * @param inflater
     * @param savedInstanceState
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_denuncias, container, false)
    }

    /**
     * Se llama después de que se crea la vista, se incializan los componentes
     * y los listeners; manda llamar la función initialize data con la query para
     * obtener las denuncias sin resolver.
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.viewDenuncias)
        btnSinResolver = view.findViewById(R.id.btnSinResolver)
        btnProcesadas = view.findViewById(R.id.btnProcesadas)
        btnInvalidas = view.findViewById(R.id.btnInvalidas)

        btnSinResolver.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#828282"))
        btnSinResolver.setTextColor(Color.WHITE)

        initializeListeners()
    }

    override fun onResume() {
        super.onResume()
        initializeData("sinResolver")
    }

    /**
     * Función que inicializa los listeners de los botones para visualizar las
     * denuncias dependiendo de su estado: Sin resolver, Procesadas, Inválidas.
     * En el listener de cada botón se manda llamar la función de initializeData
     * que realiza la query para obtener las denuncias dependiendo del estaod seleccionado.
     */
    fun initializeListeners() {
        btnSinResolver.setOnClickListener {
            originalBackroundTint()
            btnSinResolver.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#828282"))
            btnSinResolver.setTextColor(Color.WHITE)
            initializeData("sinResolver")
        }

        btnProcesadas.setOnClickListener {
            originalBackroundTint()
            btnProcesadas.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#828282"))
            btnProcesadas.setTextColor(Color.WHITE)
            initializeData("procesada")
        }

        btnInvalidas.setOnClickListener {
            originalBackroundTint()
            btnInvalidas.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#828282"))
            btnInvalidas.setTextColor(Color.WHITE)
            initializeData("invalida")
        }

    }

    /**
     * Función que crea la query para obtener todas las denuncias que se encuentren
     * en el estado recibido. Se obtienen las denuncias de la base de datos de
     * Parse y se agrega cada una a la lista "data" que se usa para desplegar los
     * datos con el adaptador.
     * Al finalizar, se manda llamar la función initializeList que crea un
     * LinearLayoutManager y un adaptador para la recycler view.
     * @param state  string con el estado de las denuncias que se quieren obtener
     */
    private fun initializeData(state: String) {
        val query: ParseQuery<Denuncia> = ParseQuery.getQuery(Denuncia::class.java)
        data = ArrayList()
        query.whereEqualTo("estado", state)
        query.include(Constantes.ID_SITIO)
        query.findInBackground { itemList, e ->
            if (e == null) {
                /*if (itemList.isEmpty()) {
                    //Toast.makeText(requireContext(), "No hay denuncias con estado" + state, Toast.LENGTH_LONG).show()
                    view?.let { Snackbar.make(it.findViewById(android.R.id.content), "No hay denuncias con estado" + state, Snackbar.LENGTH_SHORT).show() }
                }*/
                for (element in itemList) {
                    data.add(element)
                }
                initializeList()
            }
            else {
                view?.let { Snackbar.make(it.findViewById(R.id.content), "Error al obtener las denuncias", Snackbar.LENGTH_SHORT).show() }
            }
        }
    }

    /**
     * Función que crea un LinearLayoutManager y un adaptador (de tipo denunciasAdapter)
     * y lo asigna a la recyclerView.
     */
    fun initializeList() {
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.scrollToPosition(0)

        denunciasAdapter = DenunciasAdapter(requireContext(), data) { position ->
            onListItemClick(
                position
            )
        }

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = denunciasAdapter
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    /**
     * Función auxiliar que sirve para regresar los botones a su color original
     * después de que se selecciona otro.
     */
    private fun originalBackroundTint() {
        btnSinResolver.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3E3E3"))
        btnSinResolver.setTextColor(Color.BLACK)

        btnProcesadas.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3E3E3"))
        btnProcesadas.setTextColor(Color.BLACK)

        btnInvalidas.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3E3E3"))
        btnInvalidas.setTextColor(Color.BLACK)
    }

    /**
     * Función que devuleve la información de una denuncia en una posición específica
     * de la lista. Se usa para que al hacer click en un elemento de la lista, se
     * cree la actividad Denuncia que mostrará la información completa de la denuncia seleccionada
     * @param position  Posición de la denuncia dentro de la lista "data"
     */
    private fun onListItemClick(position: Int) {
        val denuncia: Denuncia = data[position] as Denuncia
        val i = Intent(activity, DenunciaActivity::class.java)

        i.putExtra("denuncia", denuncia)
        startActivity(i)
    }

}