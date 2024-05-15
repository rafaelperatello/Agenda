package br.edu.ifspsaocarlos.agenda.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Long = 0,
    val name: String? = null,
    val phone: String? = null,
    val phone2: String? = null,
    val birthday: String? = null,
    val email: String? = null
) : Parcelable