package com.example.kampa.models

import android.nfc.Tag
import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject

@ParseClassName("PublicacionTags")
class PublicacionTag : ParseObject() {

    var idPublicacion: Publicacion?
        get() = getParseObject(Constantes.ID_PUBLICACION) as Publicacion?
        set(_idPublicacion) {
            put(Constantes.ID_PUBLICACION, _idPublicacion!!)
        }

    var idTag: Tag?
        get() = getParseObject(Constantes.ID_TAG) as Tag?
        set(_idTag) {
            put(Constantes.ID_TAG, _idTag!!)
        }
}
