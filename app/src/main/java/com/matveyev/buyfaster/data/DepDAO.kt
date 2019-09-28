package com.matveyev.buyfaster.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DepDAO {
    @Query("SELECT * FROM department")
    fun getAll(): List<DepDBO>

    @Insert
    fun insert(depDBO: DepDBO)

    @Query("DELETE FROM department")
    fun deleteAll()
}