package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.*

@ParseClassName("Sitio")
class Sitio : ParseObject(){

    var nombre : String?
        get() = getString(Constantes.NOMBRE)
        set(_nombre) {
            put(Constantes.NOMBRE,_nombre!!)
        }

    var descripcion: String?
        get() = getString(Constantes.DESCRIPCION)
        set(_descripcion) {
            put(Constantes.DESCRIPCION, _descripcion!!)
        }

    var foto: ParseFile?
        get() = getParseFile(Constantes.FOTO)!!
        set(_foto) {
            put(Constantes.FOTO, _foto!!)
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

    var historia: String?
        get() = getString(Constantes.HISTORIA)
        set(_historia) {
            put(Constantes.HISTORIA, _historia!!)
        }

    var paginaOficial: String?
        get() = getString(Constantes.PAGINA_OFICIAL)
        set(_paginaOficial) {
            put(Constantes.PAGINA_OFICIAL, _paginaOficial!!)
        }

    var idTipoSitio: TipoSitio?
        get() = getParseObject(Constantes.ID_TIPO_SITIO) as TipoSitio?
        set(_idTipoSitio) {
            put(Constantes.ID_TIPO_SITIO, _idTipoSitio!!)
        }
}