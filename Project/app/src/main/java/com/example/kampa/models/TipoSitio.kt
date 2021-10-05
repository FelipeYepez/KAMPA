package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("TipoSitio")
class TipoSitio : ParseObject() {

    var descripcion: String?
        get() = getString(Constantes.DESCRIPCION)
        set(_descripcion) {
            put(Constantes.DESCRIPCION, _descripcion!!)
        }
}