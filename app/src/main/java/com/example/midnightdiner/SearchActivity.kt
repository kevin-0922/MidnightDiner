package com.example.midnightdiner

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var recipeDao: RecipeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecipeDao
        recipeDao = RecipeDao(this)

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list
        searchAdapter = SearchAdapter(emptyList()) { recipe ->
            val intent = Intent(this, DetailActivity::class.java)
            val recipeJson = Gson().toJson(recipe)
            intent.putExtra("recipe", recipeJson)
            startActivity(intent)
        }
        recyclerView.adapter = searchAdapter

        // Set up SearchView
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    performSearch(newText)
                }
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        val results = recipeDao.searchRecipes(query)
        searchAdapter = SearchAdapter(results) { recipe ->
            val intent = Intent(this, DetailActivity::class.java)
            val recipeJson = Gson().toJson(recipe)
            intent.putExtra("recipe", recipeJson)
            startActivity(intent)
        }
        findViewById<RecyclerView>(R.id.recyclerView).adapter = searchAdapter
    }
}
