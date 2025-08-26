package com.simdea.deeplinktester.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DeeplinkDao {
    @Query("SELECT * FROM deeplink_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Deeplink>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deeplink: Deeplink)

    @Delete
    suspend fun delete(deeplink: Deeplink)
}
