package com.example.midnightdiner

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import androidx.appcompat.widget.SearchView

class SearchActivity : AppCompatActivity() {
    private lateinit var recipeDao: RecipeDao
    private lateinit var searchResultsAdapter: SearchResultsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recipeDao = RecipeDao(this)

        val rootView = findViewById<View>(R.id.searchLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchView = findViewById<SearchView>(R.id.searchView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchResultsAdapter = SearchResultsAdapter(this)
        recyclerView.adapter = searchResultsAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }
        })
    }

    private fun performSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            searchResultsAdapter.updateResults(emptyList())
        } else {
            val results = recipeDao.searchRecipes(query)
            searchResultsAdapter.updateResults(results)
        }
    }

    private inner class SearchResultsAdapter(
        val context: Context
    ) : RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder>() {

        private var results: List<Recipe> = emptyList()

        inner class SearchResultsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleTextView: TextView = view.findViewById(R.id.titleTextView)
            val tagsTextView: TextView = view.findViewById(R.id.tagsTextView)

            init {
                view.setOnClickListener {
                    val recipe = results[adapterPosition]
                    val intent = Intent(context, DetailActivity::class.java)
                    val recipeJson = Gson().toJson(recipe)
                    intent.putExtra("recipe", recipeJson)
                    context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_result, parent, false)
            return SearchResultsViewHolder(view)
        }

        override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
            val recipe = results[position]
            holder.titleTextView.text = recipe.title
            holder.tagsTextView.text = recipe.tags.joinToString(", ")
        }

        override fun getItemCount() = results.size

        fun updateResults(newResults: List<Recipe>) {
            results = newResults
            notifyDataSetChanged()
        }
    }
}
