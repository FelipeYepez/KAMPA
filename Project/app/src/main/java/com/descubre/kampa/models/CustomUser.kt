package com.descubre.kampa.models

import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

/** * @author RECON
 *  Clase para obtener y settear los valores de un usuario de Parse en la base de datos.
 */
class CustomUser : ParseObject() {

    private var mUser = ParseUser()

    var idRol: Rol?
        get() = mUser.getParseObject(com.descubre.kampa.Constantes.ID_ROL) as Rol?
        set(_idRol) {
            mUser.put(com.descubre.kampa.Constantes.ID_ROL, _idRol!!)
        }

    var fotoPerfil: ParseFile?
        get() = mUser.getParseFile(com.descubre.kampa.Constantes.FOTO_PERFIL)
        set(_fotoPerfil) {
            mUser.put(com.descubre.kampa.Constantes.FOTO_PERFIL, _fotoPerfil!!)
        }
}