package com.example.kampa.Models

import com.example.kampa.Constantes
import com.example.kampa.models.Publicacion
import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("PublicacionUsuario")
class PublicacionUsuario : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(Constantes.ID_USUARIO, _idUsuario!!)
        }
    var idPublicacion: Publicacion?
        get() = getParseObject(Constantes.ID_PUBLICACION) as Publicacion?
        set(_idPublicacion) {
            put(Constantes.ID_PUBLICACION, _idPublicacion!!)
        }
}