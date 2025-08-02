package com.example.blinkit_admin.modals.dataClasses

import kotlinx.serialization.Serializable

@Serializable
 data class CategoryList(
     val categoryList : List<CategoryItem>
 )