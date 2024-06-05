package com.example.midnightdiner

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson

class RecommendedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended)

        val rootView = findViewById<View>(R.id.recommendedLayout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recipeDao = RecipeDao(this)
        val recipes = recipeDao.getTopRatedRecipes(5)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecipeAdapter(this, recipes)
    }

    private inner class RecipeAdapter(
        val context: Context,
        val recipes: List<Recipe>
    ) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

        inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val titleTextView: TextView = view.findViewById(R.id.titleTextView)
            val starsTextView: TextView = view.findViewById(R.id.starsTextView)

            init {
                view.setOnClickListener {
                    val recipe = recipes[adapterPosition]
                    val intent = Intent(context, DetailActivity::class.java)
                    val recipeJson = Gson().toJson(recipe)
                    intent.putExtra("recipe", recipeJson)
                    context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe, parent, false)
            return RecipeViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            val recipe = recipes[position]
            holder.titleTextView.text = recipe.title
            holder.starsTextView.text = "⭐ ${String.format("%.1f", recipe.avgStars)}"
        }

        override fun getItemCount() = recipes.size
    }
}