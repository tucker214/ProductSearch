package com.example.krogerdanieltalks.apiModels.token.productTerm

data class Data(
    val aisleLocations: List<AisleLocation>?,
    val brand: String?,
    val categories: List<String>?,
    val countryOrigin: String?,
    val description: String?,
    val images: List<Image>?,
    val itemInformation: ItemInformation?,
    val items: List<Item>?,
    val productId: String?,
    val productPageURI: String?,
    val taxonomies: List<Taxonomy>?,
    val temperature: Temperature?,
    val upc: String?
)