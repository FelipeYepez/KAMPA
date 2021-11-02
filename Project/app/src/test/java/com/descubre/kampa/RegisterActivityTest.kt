package com.descubre.kampa

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RegisterActivityTest {
    @Test
    fun `usuario sin nombre`(){
        val result = RegisterUtils.validateInputs("", "!Hola12345", "!Hola12345")
        assertThat(result).isEqualTo("Escribe el nombre de usuario")
    }
    @Test
    fun `sin contrasena`(){
        val result = RegisterUtils.validateInputs("user37", "", "!Hola12345")
        assertThat(result).isEqualTo("Escribe la contrasena")
    }
    @Test
    fun `contrasena corta`(){
        val result = RegisterUtils.validateInputs("user37", "!Hola1", "!Hola1")
        assertThat(result).isEqualTo("Contra demasiado corta")
    }
    @Test
    fun `sin validacion`(){
        val result = RegisterUtils.validateInputs("user37", "!Hola12345", "")
        assertThat(result).isEqualTo("Valida la contrasena")
    }
    @Test
    fun `validacion incorrecta`(){
        val result = RegisterUtils.validateInputs("user37", "!Hola12345", "!Hola12")
        assertThat(result).isEqualTo("Validacion nula")
    }
    @Test
    fun `falta de caracteres`(){
        val result = RegisterUtils.validateInputs("user37", "Hola12345", "Hola12345")
        assertThat(result).isEqualTo("Contrasena invalida")
    }
    @Test
    fun `todo bien`(){
        val result = RegisterUtils.validateInputs("user37", "!Hola12345", "!Hola12345")
        assertThat(result).isEqualTo("todo correcto")
    }


}