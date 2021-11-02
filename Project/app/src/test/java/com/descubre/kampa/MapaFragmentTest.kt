package com.descubre.kampa

import com.descubre.kampa.fragments.MapaFragmentUtils
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MapaFragmentTest {
    @Test
    fun `lugar visitado y no en lista de favoritos regresa morado`(){
        val result = MapaFragmentUtils.setColor(true, false)
        assertThat(result).isEqualTo(260F)
    }

    @Test
    fun `lugar visitado y en lista de favoritos regresa morado`(){
        val result = MapaFragmentUtils.setColor(true, true)
        assertThat(result).isEqualTo(260F)
    }

    @Test
    fun `lugar no visitado y en lista de favoritos regresa dorado`(){
        val result = MapaFragmentUtils.setColor(false, true)
        assertThat(result).isEqualTo(47F)
    }

    @Test
    fun `lugar no visitado y no en lista de favoritos regresa rojo`(){
        val result = MapaFragmentUtils.setColor(false, false)
        assertThat(result).isEqualTo(0F)
    }

}