package com.example.midnightdiner

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.midnightdiner.Recipe
import com.google.gson.Gson

class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        val avgStars = recipe.avgStars?.let { Math.round(it) } ?: 0
        val stars = "⭐".repeat(avgStars)

        holder.starsTextView.text = stars
        holder.titleTextView.text = recipe.title
        holder.descriptionTextView.text = recipe.description

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("RECIPE", Gson().toJson(recipe))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val starsTextView: TextView = view.findViewById(R.id.starsTextView)
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
    }
}
