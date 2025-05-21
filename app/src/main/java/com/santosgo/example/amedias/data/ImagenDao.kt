package com.santosgo.example.amedias.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImagenDao {

    @Query("SELECT * FROM Imagen WHERE idGrupo = :grupoId")
    fun obtenerPorGrupo(grupoId: Int): List<Imagen>

    @Insert
    fun insertarImagen(imagen: Imagen)
}
