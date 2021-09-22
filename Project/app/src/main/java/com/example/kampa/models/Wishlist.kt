package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Wishlist")
class Wishlist : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(ID_USUARIO)
        set(_idUsuario) {
            put(ID_USUARIO, _idUsuario!!)
            }

    var nombre: String?
        get() = getString(NOMBRE)
        set(_nombre) {
            put(NOMBRE, _nombre!!)
        }

    companion object {
        const val ID_USUARIO = "idUsuario"
        const val NOMBRE = "nombre"
    }
}
