package com.example.kampa.models

import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser


class CustomUser : ParseObject() {

    private var mUser = ParseUser()

    var idRol: Rol?
        get() = mUser.getParseObject(ID_ROL) as Rol?
        set(_idRol) {
            mUser.put(ID_ROL, _idRol!!)
        }

    var fotoPerfil: ParseFile?
        get() = mUser.getParseFile(FOTO_PERFIL)
        set(_fotoPerfil) {
            mUser.put(FOTO_PERFIL, _fotoPerfil!!)
        }

    companion object {
        const val ID_ROL = "idRol"
        const val FOTO_PERFIL = "fotoPerfil"
    }
}