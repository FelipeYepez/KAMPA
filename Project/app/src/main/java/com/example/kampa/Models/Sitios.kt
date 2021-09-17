package com.example.kampa.Models

import com.parse.ParseClassName
import com.parse.ParseObject

@ParseClassName("Sitio")
class Sitios : ParseObject(){
    val nombre : String?
        get() = getString("nombre")
    val descripcion: String?
        get() = getString("descripcion")

}