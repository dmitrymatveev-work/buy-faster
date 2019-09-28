package com.matveyev.buyfaster.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TheListDBO::class, DepDBO::class], version = 2, exportSchema = false)
abstract class BuyFasterDB : RoomDatabase() {
    abstract fun theListDAO() : TheListDAO
    abstract fun depDAO() : DepDAO
}