package com.example.kampa.models

import android.nfc.Tag
import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("SitioTag")
class SitioTag : ParseObject() {

    var idSitio: Sitio?
        get() = getParseObject(Constantes.ID_SITIO) as Sitio?
        set(_idSitio) {
            put(Constantes.ID_SITIO, _idSitio!!)
        }

    var idTag: Tag?
        get() = getParseObject(Constantes.ID_TAG) as Tag?
        set(_idTag) {
            put(Constantes.ID_TAG, _idTag!!)
        }

    var numPublicaciones: Int?
        get() = getInt(Constantes.NUM_PUBLICACIONES)
        set(_numPublicaciones) {
            put(Constantes.NUM_PUBLICACIONES, _numPublicaciones!!)
        }
}
