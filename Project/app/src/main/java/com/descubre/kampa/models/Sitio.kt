package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseGeoPoint
import com.parse.ParseObject

/** * @author RECON
 *  Clase para obtener y settear los valores de la tabla Sitio en la base de datos.
 */
@ParseClassName("Sitio")
class Sitio : ParseObject(){

    var nombre : String?
        get() = getString(com.descubre.kampa.Constantes.NOMBRE)
        set(_nombre) {
            put(com.descubre.kampa.Constantes.NOMBRE,_nombre!!)
        }

    var descripcion: String?
        get() = getString(com.descubre.kampa.Constantes.DESCRIPCION)
        set(_descripcion) {
            put(com.descubre.kampa.Constantes.DESCRIPCION, _descripcion!!)
        }

    var foto: ParseFile?
        get() {
            var fotoSitio : ParseFile? = getParseFile(com.descubre.kampa.Constantes.FOTO)
            if (fotoSitio == null) {
                return null
            } else {
                return fotoSitio
            }
        }
        set(_foto) {
            put(com.descubre.kampa.Constantes.FOTO, _foto!!)
        }

    var direccion: String?
        get() = getString(com.descubre.kampa.Constantes.DIRECCION)
        set(_direccion) {
            put(com.descubre.kampa.Constantes.DIRECCION, _direccion!!)
        }

    var ubicacion: ParseGeoPoint?
        get() = getParseGeoPoint(com.descubre.kampa.Constantes.UBICACION)
        set(_ubicacion) {
            put(com.descubre.kampa.Constantes.UBICACION, _ubicacion!!)
        }

    var historia: String?
        get() = getString(com.descubre.kampa.Constantes.HISTORIA)
        set(_historia) {
            put(com.descubre.kampa.Constantes.HISTORIA, _historia!!)
        }

    var paginaOficial: String?
        get() = getString(com.descubre.kampa.Constantes.PAGINA_OFICIAL)
        set(_paginaOficial) {
            put(com.descubre.kampa.Constantes.PAGINA_OFICIAL, _paginaOficial!!)
        }

    var idTipoSitio: TipoSitio?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_TIPO_SITIO) as TipoSitio?
        set(_idTipoSitio) {
            put(com.descubre.kampa.Constantes.ID_TIPO_SITIO, _idTipoSitio!!)
        }
}