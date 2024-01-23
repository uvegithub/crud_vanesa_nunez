package com.example.firebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pojo_casa(
    var id : String? = null,
    var nombre: String? = null,
    var fundador: String? = null,
    var anio_fundacion: Int? = null,
    var fecha_creacion: String? = null,
    var escudo: String? = null,
    var puntuacion: Int? = null,
    var estado_notificacion:Int? = null,
    var user_notificacion:String? = null,
):Parcelable