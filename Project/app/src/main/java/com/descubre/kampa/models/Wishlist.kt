package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Wishlist")
class Wishlist : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(com.descubre.kampa.Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(com.descubre.kampa.Constantes.ID_USUARIO, _idUsuario!!)
        }

    var nombre: String?
        get() = getString(com.descubre.kampa.Constantes.NOMBRE)
        set(_nombre) {
            put(com.descubre.kampa.Constantes.NOMBRE, _nombre!!)
        }

    var fotoWishlist: ParseFile?
        get() = getParseFile(com.descubre.kampa.Constantes.FOTO_WISHLIST)
        set(_fotoWishlist) {
            put(com.descubre.kampa.Constantes.FOTO_WISHLIST, _fotoWishlist!!)
        }

    var isDeleted: Boolean?
        get() = getBoolean(com.descubre.kampa.Constantes.IS_DELETED)
        set(_isDeleted) {
            put(com.descubre.kampa.Constantes.IS_DELETED, _isDeleted!!)
        }
}
