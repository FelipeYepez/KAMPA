package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("Tag")
class Tag : ParseObject() {

    var descripcion: String?
        get() = getString(DESCRIPCION)
        set(_descripcion) {
            put(DESCRIPCION, _descripcion!!)
        }

    companion object {
        const val DESCRIPCION = "descripcion"
    }
}