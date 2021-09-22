package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Publicacion")
class Publicacion : ParseObject() {

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

    var foto: ParseFile?
        get() = getParseFile(FOTO)
        set(_foto) {
            put(FOTO, _foto!!)
        }

    var numLikes: Int?
        get() = getInt(NUM_LIKES)
        set(_numLikes) {
            put(NUM_LIKES, _numLikes!!)
        }

    var numDislikes: Int?
        get() = getInt(NUM_DISLIKES)
        set(_numDislikes) {
            put(NUM_DISLIKES, _numDislikes!!)
        }
    var descripcion: String?
        get() = getString(DESCRIPCION)
        set(_descripcion) {
            put(DESCRIPCION, _descripcion!!)
        }

    companion object {
        const val ID_USUARIO = "idUsuario"
        const val ID_SITIO = "idSitio"
        const val FOTO = "foto"
        const val NUM_LIKES = "numLikes"
        const val NUM_DISLIKES = "numDislikes"
        const val DESCRIPCION = "descripcion"
    }
}
