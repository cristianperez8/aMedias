package com.santosgo.example.amedias.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "grupos")
data class Grupo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val creador: String,
    val fechaCreacion: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
)


