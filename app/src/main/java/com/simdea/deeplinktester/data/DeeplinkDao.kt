package com.simdea.deeplinktester.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeeplinkDao {
    @Transaction
    @Query("SELECT * FROM deeplink_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<DeeplinkWithCollections>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deeplink: Deeplink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: Collection): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeeplinkCollectionCrossRef(crossRef: DeeplinkCollectionCrossRef)

    @Delete
    suspend fun deleteDeeplinkCollectionCrossRef(crossRef: DeeplinkCollectionCrossRef)

    @Query("SELECT * FROM collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<Collection>>

    @Transaction
    @Query("""
        SELECT c.*, COUNT(dcr.deeplinkId) as deeplinkCount
        FROM collections as c
        LEFT JOIN deeplink_collection_cross_ref as dcr ON c.collectionId = dcr.collectionId
        GROUP BY c.collectionId
        ORDER BY c.name ASC
    """)
    fun getCollectionsWithDeeplinkCount(): Flow<List<CollectionWithDeeplinkCount>>

    @Update
    suspend fun update(deeplink: Deeplink)

    @Delete
    suspend fun delete(deeplink: Deeplink)

    @Update
    suspend fun updateCollection(collection: Collection)

    @Delete
    suspend fun deleteCollection(collection: Collection)
}
