package com.example.kampa.Models

import android.media.Image
import com.parse.*




@ParseClassName("Sitio")
class Sitios : ParseObject(){
    var nombre : String?
        get() = getString(NOMBRE)
        set(nombre) = put(NOMBRE,nombre!!)
    var descripcion: String?
        get() = getString(DESCRIPCION)
        set(descripcion) = put(DESCRIPCION,descripcion!!)
    var fotos: List<ParseFile>?
        get() = getList<ParseFile>(FOTOS)!!
        set(fotos) = put(FOTOS,fotos!!)
    var direccion: String?
        get() = getString(DIRECCION)
        set(direccion) = put(DIRECCION,direccion!!)
    var ubicacion: ParseGeoPoint?
        get() = getParseGeoPoint(UBICACION)
        set(ubicacion) = put(UBICACION,ubicacion!!)
    var historia: String?
        get() = getString(HISTORIA)
        set(historia) = put(HISTORIA,historia!!)
    var paginaOficial: String?
        get() = getString(PAGINA_OFICIAL)
        set(paginaOficial) = put(PAGINA_OFICIAL,paginaOficial!!)
    var idTipoSitio: TipoSitio?
        get() = getParseObject(ID_TIPO_SITIO) as TipoSitio?
        set(_idTipoSitio) {
            put(ID_TIPO_SITIO, _idTipoSitio!!)
        }

    companion object {
        const val NOMBRE ="nombre"
        const val DESCRIPCION = "descripcion"
        const val DIRECCION ="direccion"
        const val UBICACION = "ubicacion"
        const val HISTORIA = "historia"
        const val PAGINA_OFICIAL = "paginaOficial"
        const val FOTOS = "fotos"
        const val ID_TIPO_SITIO = "idTipoSitio"
    }



}