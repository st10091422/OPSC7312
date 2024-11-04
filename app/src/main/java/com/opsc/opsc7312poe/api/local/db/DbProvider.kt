package com.opsc.opsc7312poe.api.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.opsc.opsc7312poe.api.local.db.tables.CategoryTable
import com.opsc.opsc7312poe.api.local.db.tables.TransactionTable

class DbProvider(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "savr.db"
        private const val DATABASE_VERSION = 1// Increment this number for updates
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables when the database is created
        db.execSQL(CategoryTable.CREATE_TABLE)
        db.execSQL(TransactionTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades
        db.execSQL("DROP TABLE IF EXISTS ${TransactionTable.TABLE_TRANSACTIONS}")
        db.execSQL("DROP TABLE IF EXISTS ${CategoryTable.TABLE_CATEGORIES}")
        onCreate(db) // Recreate the database
    }
}