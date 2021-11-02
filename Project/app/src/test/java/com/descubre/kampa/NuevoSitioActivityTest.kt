package com.descubre.kampa

import com.google.common.truth.Truth.assertThat
import com.parse.ParseGeoPoint
import org.junit.Test

class NuevoSitioActivityTest {
    @Test
    fun `sitio sin nombre regresa que no se ha seleccionado nombre`(){
        val result = NuevoSitioUtils.validateInputs("",
            "Más de 200 años de existencia",
            ParseGeoPoint(1.0,1.0),
            1)
        assertThat(result).isEqualTo("Escribe el nombre del sitio")
    }

    @Test
    fun `sitio sin descripcion regresa que no se ha seleccionado descripcion`(){
        val result = NuevoSitioUtils.validateInputs("Catedral Mateo",
            "",
            ParseGeoPoint(1.0,1.0),
            1)
        assertThat(result).isEqualTo("Escribe la descripción del sitio")
    }

    @Test
    fun `sitio sin localizacion regresa que no se ha seleccionado localizacion`(){
        val result = NuevoSitioUtils.validateInputs("Catedral Mateo",
            "Más de 200 años de existencia",
            ParseGeoPoint(),
            1)
        assertThat(result).isEqualTo("Elige la ubicación del sitio")
    }

    @Test
    fun `sitio sin TipoSitio regresa que no se ha seleccionado categoría`(){
        val result = NuevoSitioUtils.validateInputs("Catedral Mateo",
            "Más de 200 años de existencia",
            ParseGeoPoint(1.0,1.0),
            -1)
        assertThat(result).isEqualTo("Selecciona una categoría")
    }

    @Test
    fun `sitio con todos los campos regresa que la entrada está completa`(){
        val result = NuevoSitioUtils.validateInputs("Catedral Mateo",
            "Más de 200 años de existencia",
            ParseGeoPoint(1.0,1.0),
            3)
        assertThat(result).isEqualTo("complete values")
    }
}