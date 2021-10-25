package com.descubre.kampa.interfaces

import com.descubre.kampa.models.Wishlist

/** * @author RECON
 *  Interfaz para comunicar a al adaptador FavoritosFragmen y al fragmento
 *  FavoritosFragment
 *  @version 1.0
 */
interface SitiosFavoritosInterface {

    /**
     * Método abstracto para implementarlo en alguna clase.
     * @param wishlist para utilizarloa o redirigir al usuario a alguna actividad
     * o fragmento
     */
    fun passData(wishlist: Wishlist)
}