package com.example.blinkit_admin.modals.dataClasses

import kotlinx.serialization.Serializable

@Serializable
data class FormInputData(
    var label: String,
    var value: String?
)
