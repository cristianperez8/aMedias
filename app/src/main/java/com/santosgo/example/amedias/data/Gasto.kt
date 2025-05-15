package com.santosgo.example.amedias.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val idGrupo: Int,
    val nombreUsuario: String,
    val pagadoPor: String,
    val cantidad: Double
)

