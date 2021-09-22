package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.*

@ParseClassName("UsuarioTag")
class UsuarioTag : ParseObject() {

    var idTag: ParseObject?
        get() = getParseObject(Constantes.ID_TAG) as Tag?
        set(_idTag) {
            put(Constantes.ID_TAG, _idTag!!)
        }

    var idUsuario: ParseUser?
        get() = getParseUser(Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(Constantes.ID_USUARIO, _idUsuario!!)
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
}