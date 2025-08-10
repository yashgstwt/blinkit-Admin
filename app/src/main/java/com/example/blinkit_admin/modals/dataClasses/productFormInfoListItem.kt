package com.example.blinkit_admin.modals.dataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductFormInfoListItem(
    @SerialName("Label")
    val label: String?,

    @SerialName("options")
    val options: Array<String>?,

    @SerialName("Input Type")
    val inputType: String?,

    @SerialName("Input Method")
    val inputMethod: String?

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductFormInfoListItem

        if (label != other.label) return false
        if (!options.contentEquals(other.options)) return false
        if (inputType != other.inputType) return false
        if (inputMethod != other.inputMethod) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label?.hashCode() ?: 0
        result = 31 * result + (options?.contentHashCode() ?: 0)
        result = 31 * result + (inputType?.hashCode() ?: 0)
        result = 31 * result + (inputMethod?.hashCode() ?: 0)
        return result
    }
}