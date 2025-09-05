package com.simdea.deeplinktester.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

data class DeeplinkWithCollections(
    @Embedded val deeplink: Deeplink,
    @Relation(
        parentColumn = "id",
        entityColumn = "collectionId",
        associateBy = Junction(
            value = DeeplinkCollectionCrossRef::class,
            parentColumn = "deeplinkId",
            entityColumn = "collectionId"
        )
    )
    val collections: List<Collection>
)

@Entity(
    primaryKeys = ["deeplinkId", "collectionId"],
    foreignKeys = [
        ForeignKey(
            entity = Deeplink::class,
            parentColumns = ["id"],
            childColumns = ["deeplinkId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Collection::class,
            parentColumns = ["collectionId"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["collectionId"])]
)
data class DeeplinkCollectionCrossRef(
    val deeplinkId: Int,
    val collectionId: Int
)
