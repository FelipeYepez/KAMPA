package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

@ParseClassName("Denuncia")
class Denuncia : ParseObject() {

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

    var fotos: ParseFile?
        get() = getParseFile(Constantes.FOTOS)
        set(_fotos) {
            put(Constantes.FOTOS, _fotos!!)
        }

    var descripcion: String?
        get() = getString(Constantes.DESCRIPCION)
        set(_descripcion) {
            put(Constantes.DESCRIPCION, _descripcion!!)
        }

    var estado: String?
        get() = getString(Constantes.ESTADO)
        set(_estado) {
            put(Constantes.ESTADO, _estado!!)
        }
}
