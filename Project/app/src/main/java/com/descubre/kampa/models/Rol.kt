package com.descubre.kampa.models

import com.parse.ParseClassName
import com.parse.ParseObject

/** * @author RECON
 *  Clase para obtener y settear los valores de la tabla Rol en la base de datos.
 */
@ParseClassName("Rol")
class Rol : ParseObject() {

    var descripcion: String?
        get() = getString(com.descubre.kampa.Constantes.DESCRIPCION)
        set(_descripcion) {
            put(com.descubre.kampa.Constantes.DESCRIPCION, _descripcion!!)
        }
}