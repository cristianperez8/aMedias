package com.santosgo.example.amedias.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioGrupoDao {
    @Insert
    fun unirseAGrupo(relacion: UsuarioGrupo)

    @Query("""
        SELECT g.* FROM grupos g
        INNER JOIN usuario_grupo ug ON g.id = ug.idGrupo
        WHERE ug.nombreUsuario = :nickname
    """)
    fun obtenerGruposDelUsuario(nickname: String): List<Grupo>
}
