package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("UsuarioSitio")
class UsuarioSitio : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(com.descubre.kampa.Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(com.descubre.kampa.Constantes.ID_USUARIO, _idUsuario!!)
        }

    var idSitio: Sitio?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_SITIO) as Sitio?
        set(_idSitio) {
            put(com.descubre.kampa.Constantes.ID_SITIO, _idSitio!!)
        }

    var isVisitado: Boolean?
        get() = getBoolean(com.descubre.kampa.Constantes.IS_VISITADO)
        set(_isVisitado) {
            put(com.descubre.kampa.Constantes.IS_VISITADO, _isVisitado!!)
        }

    var isWishlist: Boolean?
        get() = getBoolean(com.descubre.kampa.Constantes.IS_WISHLIST)
        set(_isWishlist) {
            put(com.descubre.kampa.Constantes.IS_WISHLIST, _isWishlist!!)
        }
}
