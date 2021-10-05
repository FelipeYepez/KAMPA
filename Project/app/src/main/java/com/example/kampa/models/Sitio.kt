package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseObject

@ParseClassName("Sitio")
class Sitio : ParseObject() {

    var idTipoSitio: TipoSitio?
        get() = getParseObject(Constantes.ID_TIPO_SITIO) as TipoSitio?
        set(_idTipoSitio) {
            put(Constantes.ID_TIPO_SITIO, _idTipoSitio!!)
        }

    var nombre: String?
        get() = getString(Constantes.NOMBRE)
        set(_nombre) {
            put(Constantes.NOMBRE, _nombre!!)
        }

    var direccion: String?
        get() = getString(Constantes.DIRECCION)
        set(_direccion) {
            put(Constantes.DIRECCION, _direccion!!)
        }

    var ubicacion: ParseGeoPoint?
        get() = getParseGeoPoint(Constantes.UBICACION)
        set(_ubicacion) {
            put(Constantes.UBICACION, _ubicacion!!)
        }

    var fotoPerfil: ParseFile?
        get() = getParseFile(Constantes.FOTO_PERFIL)
        set(_fotoPerfil) {
            put(Constantes.FOTO_PERFIL, _fotoPerfil!!)
        }

    var descripcion: String?
        get() = getString(Constantes.DESCRIPCION)
        set(_descripcion) {
            put(Constantes.DESCRIPCION, _descripcion!!)
        }
}