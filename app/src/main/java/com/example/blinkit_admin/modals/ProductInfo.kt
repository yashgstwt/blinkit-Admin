
package com.example.blinkit_admin.modals

import kotlinx.serialization.Serializable

@Serializable
data class ProductInfo(
    val productName: String,
    val productPrice: String,
    val productQuantity: String,
    val productUnit: String,
    val productType: String,
    val productCategory: String,
    val productImages : List<String>
)
