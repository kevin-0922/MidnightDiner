package com.example.midnightdiner
data class Recipe(
    val id: Long,
    val title: String,
    val description: String,
    val imagePath: String?,
    val ingredients: List<String>,
    val tags: List<String>,
    val steps: List<String>,
    val avgStars: Float? = null
)


data class Comment(
    val id: Long,
    val recipeId: Long,
    val commenter: String,
    val comment: String,
    val timestamp: String,
    val stars: Int
)