package com.example.krogerdanieltalks.apiModels.token.productId

data class Fulfillment(
    val curbside: Boolean?,
    val delivery: Boolean?,
    val instore: Boolean?,
    val shiptohome: Boolean?
)