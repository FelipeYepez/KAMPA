package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Wishlist")
class Wishlist : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(Constantes.ID_USUARIO, _idUsuario!!)
        }

    var nombre: String?
        get() = getString(Constantes.NOMBRE)
        set(_nombre) {
            put(Constantes.NOMBRE, _nombre!!)
        }

    var fotoWishlist: ParseFile?
        get() = getParseFile(Constantes.FOTO_WISHLIST)
        set(_fotoWishlist) {
            put(Constantes.FOTO_WISHLIST, _fotoWishlist!!)
        }
}
