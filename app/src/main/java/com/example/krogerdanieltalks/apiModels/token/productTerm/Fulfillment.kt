package com.example.krogerdanieltalks.apiModels.token.productTerm

data class Fulfillment(
    val curbside: Boolean?,
    val delivery: Boolean?,
    val instore: Boolean?,
    val shiptohome: Boolean?
)