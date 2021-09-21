package com.example.kampa.models

import com.parse.*

@ParseClassName("UsuarioTag")
class UsuarioTag : ParseObject() {

    var idTag: ParseObject?
        get() = getParseObject(ID_TAG) as Tag?
        set(_idTag) {
            put(ID_TAG, _idTag!!)
        }

    var idUsuario: ParseUser?
        get() = getParseUser(ID_USUARIO)
        set(_idUsuario) {
            put(ID_USUARIO, _idUsuario!!)
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


    companion object {
        const val ID_TAG = "idTag"
        const val ID_USUARIO = "idUsuario"
        const val NUM_LIKES = "numLikes"
        const val NUM_DISLIKES = "numDislikes"
    }
}