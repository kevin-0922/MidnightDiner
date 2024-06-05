package com.example.midnightdiner

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.midnightdiner.RecipeDao
import com.example.midnightdiner.Recipe
import com.example.midnightdiner.Comment
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService


class MainActivity : AppCompatActivity() {
    private lateinit var recipeDao: RecipeDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        SQLiteStudioService. instance().start(this);//##SQLiteStudio


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the search button
        findViewById<Button>(R.id.searchButton).setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Set up the popular recipes button
        findViewById<Button>(R.id.popularButton).setOnClickListener {
            val intent = Intent(this, RecommendedActivity::class.java)
            startActivity(intent)
        }
    }
}

