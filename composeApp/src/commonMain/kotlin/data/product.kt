package data

import kotlinx.serialization.Serializable

@Serializable
data class Product (
    val id: Int,
    val title: String,
    val price: Double? = null,
    val description: String,
    val category: String? = null,
    val image: String? = null,
    val rating: Rating? = null,
)

@Serializable
data class Rating (
    val rate: Double? = null,
    val count: Int? = null,
)
