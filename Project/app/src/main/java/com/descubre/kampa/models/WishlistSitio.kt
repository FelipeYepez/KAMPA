package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("WishlistSitio")
class WishlistSitio : ParseObject() {

    var idWishlist: Wishlist?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_WISHLIST) as Wishlist?
        set(_idWishlist) {
            put(com.descubre.kampa.Constantes.ID_WISHLIST, _idWishlist!!)
        }

    var idSitio: Sitio?
        get() = getParseObject(com.descubre.kampa.Constantes.ID_SITIO) as Sitio?
        set(_idSitio) {
            put(com.descubre.kampa.Constantes.ID_SITIO, _idSitio!!)
        }
}
