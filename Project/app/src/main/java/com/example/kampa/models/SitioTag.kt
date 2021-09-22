package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject

@ParseClassName("SitioTag")
class SitioTag : ParseObject() {

    var idSitio: Sitio?
        get() = getParseObject(ID_SITIO) as Sitio?
        set(_idSitio) {
            put(ID_SITIO, _idSitio!!)
        }

    var idTag: Tag?
        get() = getParseObject(ID_TAG) as Tag?
        set(_idTag) {
            put(ID_TAG, _idTag!!)
        }

    var numPublicaciones: Int?
        get() = getInt(NUM_PUBLICACIONES)
        set(_numPublicaciones) {
            put(NUM_PUBLICACIONES, _numPublicaciones!!)
        }

    companion object {
        const val ID_SITIO = "idSitio"
        const val ID_TAG = "idTag"
        const val NUM_PUBLICACIONES = "numPublicaciones"
    }
}
