package com.example.midnightdiner

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class RecipeDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val gson = Gson()

    fun addRecipe(recipe: Recipe): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", recipe.title)
            put("description", recipe.description)
            put("imagePath", recipe.imagePath)
            put("ingredients", gson.toJson(recipe.ingredients))
            put("tags", gson.toJson(recipe.tags))
            put("steps", gson.toJson(recipe.steps))
        }
        return db.insert("Recipe", null, values)
    }

    fun getAllRecipes(): List<Recipe> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("Recipe", null, null, null, null, null, null)
        val recipes = mutableListOf<Recipe>()
        val listType = object : TypeToken<List<String>>() {}.type

        with(cursor) {
            while (moveToNext()) {
                try {
                    val id = getLong(getColumnIndexOrThrow("id"))
                    val title = getString(getColumnIndexOrThrow("title"))
                    val description = getString(getColumnIndexOrThrow("description"))
                    val imagePath = getString(getColumnIndexOrThrow("imagePath"))

                    val ingredientsJson = getString(getColumnIndexOrThrow("ingredients"))
                    val ingredients: List<String> = if (ingredientsJson.isNullOrEmpty()) {
                        emptyList()
                    } else {
                        try {
                            gson.fromJson(ingredientsJson, listType)
                        } catch (e: JsonSyntaxException) {
                            Log.e("RecipeDao", "Failed to parse ingredients JSON", e)
                            emptyList()
                        }
                    }

                    val tagsJson = getString(getColumnIndexOrThrow("tags"))
                    val tags: List<String> = if (tagsJson.isNullOrEmpty()) {
                        emptyList()
                    } else {
                        try {
                            gson.fromJson(tagsJson, listType)
                        } catch (e: JsonSyntaxException) {
                            Log.e("RecipeDao", "Failed to parse tags JSON", e)
                            emptyList()
                        }
                    }

                    val stepsJson = getString(getColumnIndexOrThrow("steps"))
                    val steps: List<String> = if (stepsJson.isNullOrEmpty()) {
                        emptyList()
                    } else {
                        try {
                            gson.fromJson(stepsJson, listType)
                        } catch (e: JsonSyntaxException) {
                            Log.e("RecipeDao", "Failed to parse steps JSON", e)
                            emptyList()
                        }
                    }

                    recipes.add(Recipe(id, title, description, imagePath, ingredients, tags, steps))
                } catch (e: Exception) {
                    Log.e("RecipeDao", "Error reading recipe from database", e)
                }
            }
        }
        cursor.close()
        return recipes
    }

    fun addComment(comment: Comment): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("recipe_id", comment.recipeId)
            put("commenter", comment.commenter)
            put("comment", comment.comment)
            put("timestamp", comment.timestamp)
            put("stars", comment.stars)
        }
        return db.insert("Comment", null, values)
    }
    fun getTopRatedRecipes(limit: Int): List<Recipe> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT Recipe.*, AVG(Comment.stars) as avgStars 
            FROM Recipe
            LEFT JOIN Comment ON Recipe.id = Comment.recipe_id
            GROUP BY Recipe.id
            ORDER BY avgStars DESC
            LIMIT ?
        """
        val cursor = db.rawQuery(query, arrayOf(limit.toString()))
        val recipes = mutableListOf<Recipe>()
        val listType = object : TypeToken<List<String>>() {}.type

        with(cursor) {
            while (moveToNext()) {
                try {
                    val id = getLong(getColumnIndexOrThrow("id"))
                    val title = getString(getColumnIndexOrThrow("title"))
                    val description = getString(getColumnIndexOrThrow("description"))
                    val imagePath = getString(getColumnIndexOrThrow("imagePath"))
                    val avgStars = getFloat(getColumnIndexOrThrow("avgStars"))

                    val ingredientsJson = getString(getColumnIndexOrThrow("ingredients"))
                    val ingredients: List<String> = if (ingredientsJson.isNullOrEmpty()) {
                        emptyList()
                    } else {
                        try {
                            gson.fromJson(ingredientsJson, listType)
                        } catch (e: JsonSyntaxException) {
                            Log.e("RecipeDao", "Failed to parse ingredients JSON", e)
                            emptyList()
                        }
                    }

                    val tagsJson = getString(getColumnIndexOrThrow("tags"))
                    val tags: List<String> = if (tagsJson.isNullOrEmpty()) {
                        emptyList()
                    } else {
                        try {
                            gson.fromJson(tagsJson, listType)
                        } catch (e: JsonSyntaxException) {
                            Log.e("RecipeDao", "Failed to parse tags JSON", e)
                            emptyList()
                        }
                    }

                    val stepsJson = getString(getColumnIndexOrThrow("steps"))
                    val steps: List<String> = if (stepsJson.isNullOrEmpty()) {
                        emptyList()
                    } else {
                        try {
                            gson.fromJson(stepsJson, listType)
                        } catch (e: JsonSyntaxException) {
                            Log.e("RecipeDao", "Failed to parse steps JSON", e)
                            emptyList()
                        }
                    }

                    recipes.add(Recipe(id, title, description, imagePath, ingredients, tags, steps, avgStars))
                } catch (e: Exception) {
                    Log.e("RecipeDao", "Error reading recipe from database", e)
                }
            }
        }
        cursor.close()
        return recipes
    }
    fun getCommentsByRecipeId(recipeId: Long): List<Comment> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Comment",
            null,
            "recipe_id = ?",
            arrayOf(recipeId.toString()),
            null,
            null,
            null
        )
        val comments = mutableListOf<Comment>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow("id"))
                val commenter = getString(getColumnIndexOrThrow("commenter"))
                val comment = getString(getColumnIndexOrThrow("comment"))
                val timestamp = getString(getColumnIndexOrThrow("timestamp"))
                val stars = getInt(getColumnIndexOrThrow("stars"))
                comments.add(Comment(id, recipeId, commenter, comment, timestamp, stars))
            }
        }
        cursor.close()
        return comments
    }
    fun searchRecipes(query: String): List<Recipe> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Recipe WHERE title LIKE ? OR description LIKE ? OR tags LIKE ?", arrayOf("%$query%", "%$query%","%$query%"))
        val recipes = mutableListOf<Recipe>()
        val gson = Gson()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val imagePath = cursor.getString(cursor.getColumnIndexOrThrow("imagePath"))
            val ingredientsJson = cursor.getString(cursor.getColumnIndexOrThrow("ingredients"))
            val tagsJson = cursor.getString(cursor.getColumnIndexOrThrow("tags"))
            val stepsJson = cursor.getString(cursor.getColumnIndexOrThrow("steps"))

            val ingredients = gson.fromJson(ingredientsJson, Array<String>::class.java).toList()
            val tags = gson.fromJson(tagsJson, Array<String>::class.java).toList()
            val steps = gson.fromJson(stepsJson, Array<String>::class.java).toList()

            recipes.add(Recipe(id, title, description, imagePath, ingredients, tags, steps))
        }

        cursor.close()
        return recipes
    }

}
