package com.santosgo.example.amedias.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Imagen(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String,
    val nombreUsuario: String
)
