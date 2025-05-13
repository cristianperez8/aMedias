package com.santosgo.example.amedias.data

import androidx.room.Entity

@Entity(
    tableName = "usuario_grupo",
    primaryKeys = ["nombreUsuario", "idGrupo"]
)
data class UsuarioGrupo(
    val nombreUsuario: String,
    val idGrupo: Int
)