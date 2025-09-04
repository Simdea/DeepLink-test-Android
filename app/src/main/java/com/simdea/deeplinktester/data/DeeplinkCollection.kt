package com.simdea.deeplinktester.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

data class DeeplinkWithCollections(
    @Embedded val deeplink: Deeplink,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId",
        associateBy = Junction(DeeplinkCollectionCrossRef::class)
    )
    val collections: List<Collection>
)

@Entity(primaryKeys = ["deeplinkId", "collectionId"])
data class DeeplinkCollectionCrossRef(
    val deeplinkId: Int,
    val collectionId: Int
)
