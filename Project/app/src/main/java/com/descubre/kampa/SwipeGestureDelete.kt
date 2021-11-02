package com.descubre.kampa

import android.R
import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

/**
 * @author RECON
 * Actividad que inicia cuando se selecciona el floating action button del mapa.
 * Crea un nuevo sitio en la base de datos.
 * @param context es el contexto desde el cual se llama a la apliación
 * @version 1.0
 */
abstract class SwipeGestureDelete(context: Context?) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteColor = ContextCompat.getColor(context!!, R.color.holo_red_dark)
    private val deleteIcon = android.R.drawable.ic_menu_delete

    /**
     * Método que escucha si un elemento es deslizado.
     * En caso de que no lo sea, retorna falso.
     * @param recyclerView en la cual el elemento está siendo deslizado
     * @param viewHolder el elemento que contiene las vistas
     * @param target el elemento que está siendo deslizado
     * @return retorna falso en caso de que no se mueva.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * Dibuja un rectángulo de color y un ícono alusivo a la acción en caso de que se esté
     * eliminando un item
     * @param c el Canvas a dibujar
     * @param recyclerView es en la recyclerView donde se ejecuta la acción
     * @param viewHolder el elemento que contiene las vistas
     * @param dX flotante de la posición deslizada en x del item
     * @param dY flotante de la posición deslizada en y del item
     * @param actionState entero que identifica el estado de la acción
     * @param isCurrentlyActive booleano que indica si se está deslizando o no
     * */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            .addSwipeLeftBackgroundColor(deleteColor)
            .addSwipeLeftActionIcon(deleteIcon)
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}