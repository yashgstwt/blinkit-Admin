package com.example.blinkit.Modals

import kotlinx.serialization.Serializable


@Serializable
data class Admin(val uid: String, var phoneNumber: String)
