package com.simdea.deeplinktester.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class Collection(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Int = 0,
    val name: String
)
