package com.example.midnightdiner

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "recipeApp.db"
        private const val DATABASE_VERSION = 2  // 更新版本号
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建食谱 Table
        val createRecipeTable = """
            CREATE TABLE Recipe (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                imagePath TEXT,
                ingredients TEXT,
                tags TEXT,
                steps TEXT
            )
        """.trimIndent()
        db.execSQL(createRecipeTable)

        // 创建评论 Table
        val createCommentTable = """
            CREATE TABLE Comment (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                recipe_id INTEGER NOT NULL,
                commenter TEXT,
                comment TEXT,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                stars INTEGER,
                FOREIGN KEY (recipe_id) REFERENCES Recipe(id)
            )
        """.trimIndent()
        db.execSQL(createCommentTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS Comment")
            val createCommentTable = """
                CREATE TABLE Comment (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    recipe_id INTEGER NOT NULL,
                    commenter TEXT,
                    comment TEXT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    stars INTEGER,
                    FOREIGN KEY (recipe_id) REFERENCES Recipe(id)
                )
            """.trimIndent()
            db.execSQL(createCommentTable)
        }
    }
}
