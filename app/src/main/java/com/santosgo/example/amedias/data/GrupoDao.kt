package com.santosgo.example.amedias.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GrupoDao {
    @Insert
    fun insertar(grupo: Grupo)

    @Query("SELECT * FROM grupos")
    fun obtenerTodos(): List<Grupo>
}
