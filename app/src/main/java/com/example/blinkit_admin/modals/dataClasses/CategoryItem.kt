package com.example.blinkit_admin.modals.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class CategoryItem(
    val id: Int,
    val name: String,
    val types: List<Type>
)