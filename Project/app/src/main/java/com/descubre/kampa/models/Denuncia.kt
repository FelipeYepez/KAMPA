package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

/** * @author RECON
 *  Clase para obtener y settear los valores de la tabla Denuncia en la base de datos.
 */
@ParseClassName("Denuncia")
class Denuncia : ParseObject() {

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

    var fotos: ParseFile?
        get() = getParseFile(com.descubre.kampa.Constantes.FOTOS)
        set(_fotos) {
            put(com.descubre.kampa.Constantes.FOTOS, _fotos!!)
        }

    var descripcion: String?
        get() = getString(com.descubre.kampa.Constantes.DESCRIPCION)
        set(_descripcion) {
            put(com.descubre.kampa.Constantes.DESCRIPCION, _descripcion!!)
        }

    var estado: String?
        get() = getString(com.descubre.kampa.Constantes.ESTADO)
        set(_estado) {
            put(com.descubre.kampa.Constantes.ESTADO, _estado!!)
        }
}
