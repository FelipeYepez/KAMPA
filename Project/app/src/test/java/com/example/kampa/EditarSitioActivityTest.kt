package com.example.kampa

import com.google.common.truth.Truth.assertThat
import com.parse.ParseGeoPoint
import org.junit.Test

class EditarSitioActivityTest {
    @Test
    fun `sitio sin nombre regresa que no se ha seleccionado nombre`(){
        val result = EditarSitioUtils.validateInputs("",
            "Más de 200 años de existencia",
            ParseGeoPoint(1.0,1.0),
            1)
        assertThat(result).isEqualTo("Escribe el nombre del sitio")
    }

    @Test
    fun `sitio sin descripcion regresa que no se ha seleccionado descripcion`(){
        val result = EditarSitioUtils.validateInputs("Catedral Mateo",
            "",
            ParseGeoPoint(1.0,1.0),
            1)
        assertThat(result).isEqualTo("Escribe la descripción del sitio")
    }

    @Test
    fun `sitio sin localizacion regresa que no se ha seleccionado localizacion`(){
        val result = EditarSitioUtils.validateInputs("Catedral Mateo",
            "Más de 200 años de existencia",
            ParseGeoPoint(),
            1)
        assertThat(result).isEqualTo("Elige la ubicación del sitio")
    }

    @Test
    fun `sitio sin TipoSitio regresa que no se ha seleccionado categoría`(){
        val result = EditarSitioUtils.validateInputs("Catedral Mateo",
            "Más de 200 años de existencia",
            ParseGeoPoint(1.0,1.0),
            -1)
        assertThat(result).isEqualTo("Selecciona una categoría")
    }

    @Test
    fun `sitio con todos los campos regresa que la entrada está completa`(){
        val result = EditarSitioUtils.validateInputs("Catedral Mateo",
            "Más de 200 años de existencia",
            ParseGeoPoint(1.0,1.0),
            3)
        assertThat(result).isEqualTo("complete values")
    }
}