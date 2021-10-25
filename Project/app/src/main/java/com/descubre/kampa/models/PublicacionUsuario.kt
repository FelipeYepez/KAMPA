package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

/** * @author RECON
 *  Clase para obtener y settear los valores de la tabla PublicacionUsuario en la base de datos.
 */
@ParseClassName("PublicacionUsuario")
class PublicacionUsuario : ParseObject() {

    var idUsuario: ParseUser?
        get() = getParseUser(com.descubre.kampa.Constantes.ID_USUARIO)
        set(_idUsuario) {
            put(com.descubre.kampa.Constantes.ID_USUARIO, _idUsuario!!)
        }
    var idPublicacion: Publicacion?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_PUBLICACION) as Publicacion?
        set(_idPublicacion) {
            put(com.descubre.kampa.Constantes.ID_PUBLICACION, _idPublicacion!!)
        }
}