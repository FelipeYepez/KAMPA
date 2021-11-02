package com.descubre.kampa.interfaces

import com.descubre.kampa.models.Sitio

/** * @author RECON
 *  Interfaz para comunicar a al adaptador SitiosFavoritosAdapter y al fragmento
 *  SitiosFavoritosFragment
 *  @version 1.0
 */
interface SitioInterface {

    /**
     * Método abstracto para implementarlo en alguna clase.
     * @param sitio para utilizarlo o redirigir al usuario a alguna actividad
     */
    fun passSitio(sitio: Sitio)
}