package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Denuncia")
class Denuncia : ParseObject() {

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

    var fotos: ParseFile?
        get() = getParseFile(FOTOS)
        set(_fotos) {
            put(FOTOS, _fotos!!)
        }

    var descripcion: String?
        get() = getString(DESCRIPCION)
        set(_descripcion) {
            put(DESCRIPCION, _descripcion!!)
        }

    var estado: String?
        get() = getString(ESTADO)
        set(_estado) {
            put(ESTADO, _estado!!)
        }

    companion object {
        const val ID_USUARIO = "idUsuario"
        const val ID_SITIO = "idSitio"
        const val FOTOS = "fotos"
        const val DESCRIPCION = "descripcion"
        const val ESTADO = "estado"
    }
}
