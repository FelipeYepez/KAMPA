package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("UsuarioSitio")
class UsuarioSitio : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(Constantes.ID_USUARIO, _idUsuario!!)
        }

    var idSitio: Sitio?
        get() = getParseObject(Constantes.ID_SITIO) as Sitio?
        set(_idSitio) {
            put(Constantes.ID_SITIO, _idSitio!!)
        }

    var isVisitado: Boolean?
        get() = getBoolean(Constantes.IS_VISITADO)
        set(_isVisitado) {
            put(Constantes.IS_VISITADO, _isVisitado!!)
        }

    var isWishlist: Boolean?
        get() = getBoolean(Constantes.IS_WISHLIST)
        set(_isWishlist) {
            put(Constantes.IS_WISHLIST, _isWishlist!!)
        }
}
