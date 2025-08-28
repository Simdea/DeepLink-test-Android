package com.simdea.deeplinktester.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deeplink_history")
data class Deeplink(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val deeplink: String,
    val timestamp: Long = System.currentTimeMillis()
)
