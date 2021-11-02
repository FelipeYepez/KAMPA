package com.descubre.kampa

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginActivityTest {
    @Test
    fun `username vacio`() {
        val result = LoginUtils.validateInputs(
            "",
            "password")
        assertThat(result).isEqualTo("Escriba su nombre de usuario")
    }

    @Test
    fun `password vacio`() {
        val result = LoginUtils.validateInputs(
            "recon",
            "")
        assertThat(result).isEqualTo("Escriba su contraseña")
    }
}