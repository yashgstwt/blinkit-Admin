package com.example.blinkit_admin.modals.dataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class productTable(
    @SerialName("product_name")
    val productName: String,
    @SerialName("price")
    val price: Int,
    @SerialName("stock")
    val stock: Int,
    @SerialName("product_images")
    val img: MutableList<String>,
    @SerialName("details")
    val details: Array<FormInputData?>,
    @SerialName("category")
    val category: String,
    @SerialName("type")
    val type: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as productTable

        if (price != other.price) return false
        if (stock != other.stock) return false
        if (productName != other.productName) return false
        if (!img.contentEquals(other.img)) return false
        if (!details.contentEquals(other.details)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = price
        result = 31 * result + stock
        result = 31 * result + productName.hashCode()
        result = 31 * result + img.contentHashCode()
        result = 31 * result + details.contentHashCode()
        return result
    }
}
