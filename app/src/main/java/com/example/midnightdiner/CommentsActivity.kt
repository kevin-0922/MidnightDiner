package com.example.midnightdiner

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class CommentsActivity : AppCompatActivity() {

    private lateinit var commenterEditText: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var commentEditText: EditText
    private lateinit var saveCommentButton: Button
    private lateinit var recipeDao: RecipeDao
    private var recipeId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        // Initialize the views
        commenterEditText = findViewById(R.id.commenterEditText)
        ratingBar = findViewById(R.id.ratingBar)
        commentEditText = findViewById(R.id.commentEditText)
        saveCommentButton = findViewById(R.id.saveCommentButton)

        // Initialize RecipeDao
        recipeDao = RecipeDao(this)

        // Get the recipe ID passed from the previous activity
        recipeId = intent.getLongExtra("recipeId", 0)

        // Save the comment when the button is clicked
        saveCommentButton.setOnClickListener {
            saveComment()
        }
    }

    private fun saveComment() {
        val commenter = commenterEditText.text.toString()
        val stars = ratingBar.rating.toInt()
        val commentText = commentEditText.text.toString()

        if (commenter.isBlank() || commentText.isBlank()) {
            Snackbar.make(saveCommentButton, "請填寫所有字段", Snackbar.LENGTH_LONG).show()
            return
        }

        val comment = Comment(
            id = 0,
            recipeId = recipeId,
            commenter = commenter,
            comment = commentText,
            timestamp = System.currentTimeMillis().toString(), // Convert this to a proper timestamp
            stars = stars
        )

        recipeDao.addComment(comment)
        Snackbar.make(saveCommentButton, "評論已保存", Snackbar.LENGTH_LONG).show()
        finish()
    }
}
