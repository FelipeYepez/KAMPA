package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

/** * @author RECON
 *  Clase para obtener y settear los valores de la tabla Publicacion en la base de datos.
 */
@ParseClassName("Publicacion")
class Publicacion : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(com.descubre.kampa.Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(com.descubre.kampa.Constantes.ID_USUARIO, _idUsuario!!)
        }

    var idSitio: Sitio?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_SITIO) as Sitio?
        set(_idSitio) {
            put(com.descubre.kampa.Constantes.ID_SITIO, _idSitio!!)
        }

    var foto: ParseFile?
        get() = getParseFile(com.descubre.kampa.Constantes.FOTO)
        set(_foto) {
            put(com.descubre.kampa.Constantes.FOTO, _foto!!)
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
    var descripcion: String?
        get() = getString(com.descubre.kampa.Constantes.DESCRIPCION)
        set(_descripcion) {
            put(com.descubre.kampa.Constantes.DESCRIPCION, _descripcion!!)
        }

    var eliminada: Boolean?
        get() = getBoolean(com.descubre.kampa.Constantes.ELIMINADA)
        set(_eliminada) {
            put(com.descubre.kampa.Constantes.ELIMINADA, _eliminada!!)
        }

    var aprobada: Boolean?
        get() = getBoolean(com.descubre.kampa.Constantes.APROBADA)
        set(_aprobada) {
            put(com.descubre.kampa.Constantes.APROBADA, _aprobada!!)
        }
}
