package com.example.kampa.models

import com.example.kampa.Constantes
import com.parse.ParseClassName
import com.parse.ParseObject

/** * @author RECON
 *  Clase para obtener y settear los valores de la tabla Rol en la base de datos.
 */
@ParseClassName("Rol")
class Rol : ParseObject() {

    var descripcion: String?
        get() = getString(Constantes.DESCRIPCION)
        set(_descripcion) {
            put(Constantes.DESCRIPCION, _descripcion!!)
        }
}