package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseObject

@ParseClassName("Sitio")
class Sitio : ParseObject() {

    var idTipoSitio: TipoSitio?
        get() = getParseObject(ID_TIPO_SITIO) as TipoSitio?
        set(_idTipoSitio) {
            put(ID_TIPO_SITIO, _idTipoSitio!!)
        }

    var nombre: String?
        get() = getString(NOMBRE)
        set(_nombre) {
            put(NOMBRE, _nombre!!)
        }

    var direccion: String?
        get() = getString(DIRECCION)
        set(_direccion) {
            put(DIRECCION, _direccion!!)
        }

    var ubicacion: ParseGeoPoint?
        get() = getParseGeoPoint(UBICACION)
        set(_ubicacion) {
            put(UBICACION, _ubicacion!!)
        }

    var fotoPerfil: ParseFile?
        get() = getParseFile(FOTO_PERFIL)
        set(_fotoPerfil) {
            put(FOTO_PERFIL, _fotoPerfil!!)
        }

    var descripcion: String?
        get() = getString(DESCRIPCION)
        set(_descripcion) {
            put(DESCRIPCION, _descripcion!!)
        }


    companion object {
        const val ID_TIPO_SITIO = "idTipoSitio"
        const val NOMBRE = "nombre"
        const val DIRECCION = "direccion"
        const val UBICACION = "ubicacion"
        const val FOTO_PERFIL = "fotoPerfil"
        const val DESCRIPCION = "descripcion"
    }
}