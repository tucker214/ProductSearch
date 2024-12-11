package com.example.krogerdanieltalks.apiModels.token.productId

data class Item(
    val favorite: Boolean?,
    val fulfillment: Fulfillment?,
    val inventory: Inventory?,
    val itemId: String?,
    val nationalPrice: NationalPrice?,
    val price: Price?,
    val size: String?,
    val soldBy: String?
)