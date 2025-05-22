package com.santosgo.example.amedias.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface
GrupoDao {
    @Insert
    fun insertarGrupo(grupo: Grupo)

    @Query("SELECT * FROM grupos")
    fun obtenerTodos(): List<Grupo>

    @Query("SELECT * FROM grupos WHERE creador = :nickname")
    fun obtenerGruposPorUsuario(nickname: String): List<Grupo>

    @Delete
    fun eliminarGrupo(grupo: Grupo)

}
