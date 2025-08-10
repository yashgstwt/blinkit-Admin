package com.example.blinkit_admin.modals.dataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductTypeFormResponse(
    @SerialName("product type form details")
    val productTypeFormDetails: List<ProductFormInfoListItem>
)