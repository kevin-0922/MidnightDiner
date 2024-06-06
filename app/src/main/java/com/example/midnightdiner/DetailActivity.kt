package com.example.midnightdiner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson

class DetailActivity : AppCompatActivity() {
    private lateinit var recipeDao: RecipeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recipeDao = RecipeDao(this)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        //val starsTextView = findViewById<TextView>(R.id.starsTextView)
        val tagsTextView = findViewById<TextView>(R.id.tagsTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val ingredientsContainer = findViewById<LinearLayout>(R.id.ingredientsContainer)
        val stepsContainer = findViewById<LinearLayout>(R.id.stepsContainer)
        val commentsContainer = findViewById<LinearLayout>(R.id.commentsContainer)
        val addCommentButton = findViewById<Button>(R.id.addCommentButton)

        val recipeJson = intent.getStringExtra("recipe")
        val recipe = Gson().fromJson(recipeJson, Recipe::class.java)

        if (recipe == null) {
            finish()
            return
        }

        titleTextView.text = recipe.title
        //starsTextView.text = "⭐ ${String.format("%.1f", recipe.avgStars)}"
        tagsTextView.text = recipe.tags.joinToString("  ")
        tagsTextView.setTextColor(ContextCompat.getColor(this, R.color.tagsTextColor))
        descriptionTextView.text = recipe.description

        recipe.imagePath?.let {
            val imageResId = resources.getIdentifier(it.replace(".png", ""), "drawable", packageName)
            if (imageResId != 0) {
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.default_image)
            }
        } ?: run {

            imageView.setImageResource(R.drawable.default_image)
        }

        recipe.ingredients.forEachIndexed { index, ingredient ->
            val ingredientParts = ingredient.split(" ")
            val ingredientName = ingredientParts.dropLast(1).joinToString(" ")
            val ingredientAmount = ingredientParts.last()

            val textView = TextView(this).apply {
                text = ingredientName
                textSize = 16f
            }

            val amountTextView = TextView(this).apply {
                text = ingredientAmount
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.ingredient_amount_color))
            }

            val container = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(textView, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                addView(amountTextView, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            }

            ingredientsContainer.addView(container)

            if (index < recipe.ingredients.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        topMargin = 8
                        bottomMargin = 8
                    }
                    setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.dividerColor))
                }
                ingredientsContainer.addView(divider)
            }
        }

        recipe.steps.forEachIndexed { index, step ->
            val textView = TextView(this).apply {
                text = step
                textSize = 16f
            }
            stepsContainer.addView(textView)

            if (index < recipe.steps.size - 1) {
                val divider = View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        topMargin = 8
                        bottomMargin = 8
                    }
                    setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.dividerColor))
                }
                stepsContainer.addView(divider)
            }
        }

        val comments = getCommentsForRecipe(recipe.id)
        comments.forEach { comment ->
            val commenterTextView = TextView(this).apply {
                text = "${comment.commenter} ⭐ ${comment.stars}"
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.commenter_color))
            }
            val commentTextView = TextView(this).apply {
                text = comment.comment
                textSize = 16f
                setTextColor(ContextCompat.getColor(this@DetailActivity, R.color.comment_stars_color))
            }

            commentsContainer.addView(commenterTextView)
            commentsContainer.addView(commentTextView)

            val divider = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                ).apply {
                    topMargin = 8
                    bottomMargin = 8
                }
                setBackgroundColor(ContextCompat.getColor(this@DetailActivity, R.color.dividerColor))
            }
            commentsContainer.addView(divider)
        }

        addCommentButton.setOnClickListener {
            val intent = Intent(this, CommentsActivity::class.java)
            intent.putExtra("recipeId", recipe.id)
            startActivity(intent)
        }
    }

    private fun getCommentsForRecipe(recipeId: Long): List<Comment> {
        return recipeDao.getCommentsByRecipeId(recipeId)
    }
}
