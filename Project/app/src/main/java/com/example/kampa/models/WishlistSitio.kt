package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("WishlistSitio")
class WishlistSitio : ParseObject() {

    var idWishlist: Wishlist?
        get() = getParseObject(Constantes.ID_WISHLIST) as Wishlist?
        set(_idWishlist) {
            put(Constantes.ID_WISHLIST, _idWishlist!!)
        }

    var idSitio: Sitio?
        get() = getParseObject(Constantes.ID_SITIO) as Sitio?
        set(_idSitio) {
            put(Constantes.ID_SITIO, _idSitio!!)
        }
}
