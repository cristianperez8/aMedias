package com.santosgo.example.amedias.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Insert
    fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :usuario LIMIT 1")
    fun buscarPorNombre(usuario: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombreUsuario = :usuario AND contrasena = :clave LIMIT 1")
    fun login(usuario: String, clave: String): Usuario?
}
