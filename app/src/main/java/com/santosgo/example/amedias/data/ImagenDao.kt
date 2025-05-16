package com.santosgo.example.amedias.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImagenDao {
    @Insert
    fun insertarImagen(imagen: Imagen)

    @Query("SELECT * FROM Imagen")
    fun obtenerTodas(): List<Imagen>
}
