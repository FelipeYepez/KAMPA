package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("Tag")
class Tag : ParseObject() {

    var descripcion: String?
        get() = getString(com.descubre.kampa.Constantes.DESCRIPCION)
        set(_descripcion) {
            put(com.descubre.kampa.Constantes.DESCRIPCION, _descripcion!!)
        }
}