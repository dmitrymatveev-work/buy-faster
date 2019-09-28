package com.matveyev.buyfaster.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "the_list")
data class TheListDBO(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "content") var content: String?
)
