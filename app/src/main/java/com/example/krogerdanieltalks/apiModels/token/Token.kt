package com.example.krogerdanieltalks.apiModels.token

data class Token(
    val expires_in : Int,
    val access_token: String,
    val token_type: String
)


