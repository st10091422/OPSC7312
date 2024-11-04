package com.opsc.opsc7312poe.api.local.db.databasehelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.opsc.opsc7312poe.api.data.Category
import com.opsc.opsc7312poe.api.local.db.DbProvider
import com.opsc.opsc7312poe.api.local.db.tables.CategoryTable

class CategoryDatabaseHelper(context: Context) {
    private val dbHelper = DbProvider(context)

    fun addCategory(category: Category): Long {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategoryTable.COLUMN_ID, category.id)
            put(CategoryTable.COLUMN_NAME, category.name)
            put(CategoryTable.COLUMN_ICON, category.icon)
            put(CategoryTable.COLUMN_USER_ID, category.userid)
            put(CategoryTable.COLUMN_SYNC_STATUS, 0)

        }
        val result = db.insert(CategoryTable.TABLE_CATEGORIES, null, contentValues)
        db.close()
        return result
    }

    fun addCategorySync(category: Category): Long {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategoryTable.COLUMN_ID, category.id)
            put(CategoryTable.COLUMN_NAME, category.name)
            put(CategoryTable.COLUMN_ICON, category.icon)
            put(CategoryTable.COLUMN_USER_ID, category.userid)
            put(CategoryTable.COLUMN_SYNC_STATUS, 1)

        }
        val result = db.insert(CategoryTable.TABLE_CATEGORIES, null, contentValues)
        db.close()
        return result
    }

    fun getCategory(id: String): Category? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.query(
            CategoryTable.TABLE_CATEGORIES,
            null,
            "${CategoryTable.COLUMN_ID} = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        cursor?.let {
            if (it.moveToFirst()) {
                val category = Category(
                    id = it.getString(it.getColumnIndexOrThrow(CategoryTable.COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME)),
                    icon = it.getString(it.getColumnIndexOrThrow(CategoryTable.COLUMN_ICON)),
                    userid = it.getString(it.getColumnIndexOrThrow(CategoryTable.COLUMN_USER_ID))
                )
                it.close()
                db.close()
                return category
            }
        }
        cursor?.close()
        db.close()
        return null
    }

    fun getAllCategories(userId: String): List<Category> {
        val categories = mutableListOf<Category>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${CategoryTable.TABLE_CATEGORIES} WHERE ${CategoryTable.COLUMN_USER_ID} = ?",
            arrayOf(userId))

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ICON)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_USER_ID))
                )
                categories.add(category)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return categories
    }

    // method to get category by id
    fun getCategoryById(categoryId: String): Category? {
        val db = dbHelper.readableDatabase
        var category: Category? = null
        val cursor = db.rawQuery("SELECT * FROM ${CategoryTable.TABLE_CATEGORIES} WHERE ${CategoryTable.COLUMN_ID} = ?", arrayOf(categoryId))

        cursor.use {
            if (it.moveToFirst()) {
                category = Category(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ICON)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_USER_ID))
                )
            }
        }
        db.close()
        return category
    }

    fun updateCategoryId(localId: String, firebaseId: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CategoryTable.COLUMN_ID, firebaseId) // Update local ID with Firebase ID
        }
        val result = db.update(
            CategoryTable.TABLE_CATEGORIES,
            values,
            "${CategoryTable.COLUMN_ID} = ?",
            arrayOf(localId)
        )
        db.close()
        return result > 0
    }

    // Method to get unsynced categories for a specific user
    fun getUnSyncedCategories(userid: String): List<Category> {
        val unSyncedList = mutableListOf<Category>()
        val db = dbHelper.readableDatabase

        // Update the query to include both SYNC_STATUS and userId in the WHERE clause
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM ${CategoryTable.TABLE_CATEGORIES} WHERE ${CategoryTable.COLUMN_SYNC_STATUS} = 0 AND ${CategoryTable.COLUMN_USER_ID} = ?",
            arrayOf(userid)
        )

        if (cursor.moveToFirst()) {
            do {
                val category = Category(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME)),
                    icon = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_ICON)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.COLUMN_USER_ID))
                )
                unSyncedList.add(category)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return unSyncedList
    }


    // Method to mark a category as synced
    fun markAsSynced(categoryId: String) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(CategoryTable.COLUMN_SYNC_STATUS, 1) // Mark as synced
        }
        db.update(CategoryTable.TABLE_CATEGORIES, contentValues, "${CategoryTable.COLUMN_ID} = ?", arrayOf(categoryId))
        db.close()
    }
}