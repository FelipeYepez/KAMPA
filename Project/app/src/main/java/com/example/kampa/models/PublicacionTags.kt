package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject

@ParseClassName("PublicacionTags")
class PublicacionTags : ParseObject() {

    var idPublicacion: Publicacion?
        get() = getParseObject(ID_PUBLICACION) as Publicacion?
        set(_idPublicacion) {
            put(ID_PUBLICACION, _idPublicacion!!)
        }

    var idTag: Tag?
        get() = getParseObject(ID_TAG) as Tag?
        set(_idTag) {
            put(ID_TAG, _idTag!!)
        }

    companion object {
        const val ID_PUBLICACION = "idPublicacion"
        const val ID_TAG = "idTag"
    }
}
