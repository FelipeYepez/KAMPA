package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("UsuarioSitio")
class UsuarioSitio : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(ID_USUARIO)
        set(_idUsuario) {
            put(ID_USUARIO, _idUsuario!!)
            }

    var idSitio: Sitio?
        get() = getParseObject(ID_SITIO) as Sitio?
        set(_idSitio) {
            put(ID_SITIO, _idSitio!!)
        }

    var isVisitado: Boolean?
        get() = getBoolean(IS_VISITADO)
        set(_isVisitado) {
            put(IS_VISITADO, _isVisitado!!)
        }

    var isWishlist: Boolean?
        get() = getBoolean(IS_WISHLIST)
        set(_isWishlist) {
            put(IS_WISHLIST, _isWishlist!!)
        }

    companion object {
        const val ID_USUARIO = "idUsuario"
        const val ID_SITIO = "idSitio"
        const val IS_VISITADO = "isVisitado"
        const val IS_WISHLIST = "isWishlist"
    }
}
