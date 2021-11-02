package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject

/** * @author RECON
 *  Clase para obtener y settear los valores de la tabla PublicacionTags en la base de datos.
 */
@ParseClassName("PublicacionTags")
class PublicacionTags : ParseObject() {

    var idPublicacion: Publicacion?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_PUBLICACION) as Publicacion?
        set(_idPublicacion) {
            put(com.descubre.kampa.Constantes.ID_PUBLICACION, _idPublicacion!!)
        }

    var idTag: Tag?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_TAG) as Tag?
        set(_idTag) {
            put(com.descubre.kampa.Constantes.ID_TAG, _idTag!!)
        }
}
