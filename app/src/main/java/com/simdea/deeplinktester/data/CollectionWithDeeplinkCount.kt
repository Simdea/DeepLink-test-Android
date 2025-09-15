package com.simdea.deeplinktester.data

import androidx.room.Embedded

data class CollectionWithDeeplinkCount(
    @Embedded val collection: Collection,
    val deeplinkCount: Int
)
