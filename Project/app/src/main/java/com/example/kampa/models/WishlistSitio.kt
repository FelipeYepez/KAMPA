package com.example.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("WishlistSitio")
class WishlistSitio : ParseObject() {

    var idWishlist: Wishlist?
        get() = getParseObject(ID_WISHLIST) as Wishlist?
        set(_idWishlist) {
            put(ID_WISHLIST, _idWishlist!!)
        }

    var idSitio: Sitio?
        get() = getParseObject(ID_SITIO) as Sitio?
        set(_idSitio) {
            put(ID_SITIO, _idSitio!!)
        }

    companion object {
        const val ID_WISHLIST = "idWishlist"
        const val ID_SITIO = "idSitio"
    }
}
