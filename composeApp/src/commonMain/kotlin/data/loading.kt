package data

import kotlinx.serialization.Serializable

@Serializable
data class LoadingState (
    val loading: Boolean = false,
)
