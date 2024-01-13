package br.edu.ifspsaocarlos.agenda.model

import java.io.Serializable

data class Contact(
    val id: Long = 0,
    val name: String? = null,
    val phone: String? = null,
    val phone2: String? = null,
    val birthday: String? = null,
    val email: String? = null
) : Serializable