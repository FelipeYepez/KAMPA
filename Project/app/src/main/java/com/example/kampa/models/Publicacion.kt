package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Publicacion")
class Publicacion : ParseObject() {

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

    var foto: ParseFile?
        get() = getParseFile(Constantes.FOTO)
        set(_foto) {
            put(Constantes.FOTO, _foto!!)
        }

    var numLikes: Int?
        get() = getInt(Constantes.NUM_LIKES)
        set(_numLikes) {
            put(Constantes.NUM_LIKES, _numLikes!!)
        }

    var numDislikes: Int?
        get() = getInt(Constantes.NUM_DISLIKES)
        set(_numDislikes) {
            put(Constantes.NUM_DISLIKES, _numDislikes!!)
        }
    var descripcion: String?
        get() = getString(Constantes.DESCRIPCION)
        set(_descripcion) {
            put(Constantes.DESCRIPCION, _descripcion!!)
        }
}
