package com.santosgo.example.amedias.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GastoDao {
    @Insert
    fun insertarGasto(gasto: Gasto)

    @Query("SELECT * FROM gastos WHERE idGrupo = :grupoId")
    fun obtenerGastosDelGrupo(grupoId: Int): List<Gasto>

    @Query("DELETE FROM gastos WHERE idGrupo = :grupoId")
    fun eliminarGastosDeGrupo(grupoId: Int)
}
