package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("UsuarioTag")
class UsuarioTag : ParseObject() {

    var idTag: Tag?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_TAG) as Tag?
        set(_idTag) {
            put(com.descubre.kampa.Constantes.ID_TAG, _idTag!!)
        }

    var idUsuario: ParseUser?
        get() = getParseUser(com.descubre.kampa.Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(com.descubre.kampa.Constantes.ID_USUARIO, _idUsuario!!)
        }

    var numLikes: Int?
        get() = getInt(com.descubre.kampa.Constantes.NUM_LIKES)
        set(_numLikes) {
            put(com.descubre.kampa.Constantes.NUM_LIKES, _numLikes!!)
        }

    var numDislikes: Int?
        get() = getInt(com.descubre.kampa.Constantes.NUM_DISLIKES)
        set(_numDislikes) {
            put(com.descubre.kampa.Constantes.NUM_DISLIKES, _numDislikes!!)
        }
}