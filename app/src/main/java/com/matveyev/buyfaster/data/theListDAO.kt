package com.matveyev.buyfaster.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TheListDAO {
    @Query("SELECT * FROM the_list")
    fun getAll(): List<TheListDBO>

    @Insert
    fun insert(theListDBO: TheListDBO)

    @Query("DELETE FROM the_list")
    fun deleteAll()
}