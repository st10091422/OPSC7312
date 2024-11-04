package com.opsc.opsc7312poe.api.local.db.databasehelper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.opsc.opsc7312poe.api.data.Transaction
import com.opsc.opsc7312poe.api.local.db.DbProvider
import com.opsc.opsc7312poe.api.local.db.tables.CategoryTable
import com.opsc.opsc7312poe.api.local.db.tables.TransactionTable

class TransactionDatabaseHelper(context: Context){
    private val dbHelper = DbProvider(context)

    fun addTransaction(transaction: Transaction): Long {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(TransactionTable.COLUMN_ID, transaction.id)
            put(TransactionTable.COLUMN_TITLE, transaction.title)
            put(TransactionTable.COLUMN_AMOUNT, transaction.amount)
            put(TransactionTable.COLUMN_DATE, transaction.date)
            put(TransactionTable.COLUMN_USER_ID, transaction.userid)
            put(TransactionTable.COLUMN_CATEGORY_ID, transaction.categoryid)
            put(TransactionTable.COLUMN_DESCRIPTION, transaction.description)
            put(TransactionTable.COLUMN_TRANSACTION_TYPE, transaction.transactionType)
            put(CategoryTable.COLUMN_SYNC_STATUS, 0)
        }
        val result = db.insert(TransactionTable.TABLE_TRANSACTIONS, null, contentValues)
        db.close()
        return result
    }

    fun addTransactionSync(transaction: Transaction): Long {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(TransactionTable.COLUMN_ID, transaction.id)
            put(TransactionTable.COLUMN_TITLE, transaction.title)
            put(TransactionTable.COLUMN_AMOUNT, transaction.amount)
            put(TransactionTable.COLUMN_DATE, transaction.date)
            put(TransactionTable.COLUMN_USER_ID, transaction.userid)
            put(TransactionTable.COLUMN_CATEGORY_ID, transaction.categoryid)
            put(TransactionTable.COLUMN_DESCRIPTION, transaction.description)
            put(TransactionTable.COLUMN_TRANSACTION_TYPE, transaction.transactionType)
            put(CategoryTable.COLUMN_SYNC_STATUS, 1)
        }
        val result = db.insert(TransactionTable.TABLE_TRANSACTIONS, null, contentValues)
        db.close()
        return result
    }

    fun getTransaction(id: String): Transaction? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.query(
            TransactionTable.TABLE_TRANSACTIONS,
            null,
            "${TransactionTable.COLUMN_ID} = ?",
            arrayOf(id),
            null,
            null,
            null
        )

        cursor?.let {
            if (it.moveToFirst()) {
                val transaction = Transaction(
                    id = it.getString(it.getColumnIndexOrThrow(TransactionTable.COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(TransactionTable.COLUMN_TITLE)),
                    amount = it.getDouble(it.getColumnIndexOrThrow(TransactionTable.COLUMN_AMOUNT)),
                    date = it.getLong(it.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE)),
                    userid = it.getString(it.getColumnIndexOrThrow(TransactionTable.COLUMN_USER_ID)),
                    categoryid = it.getString(it.getColumnIndexOrThrow(TransactionTable.COLUMN_CATEGORY_ID)),
                    description = it.getString(it.getColumnIndexOrThrow(TransactionTable.COLUMN_DESCRIPTION)),
                    transactionType = it.getString(it.getColumnIndexOrThrow(TransactionTable.COLUMN_TRANSACTION_TYPE))

                )
                it.close()
                db.close()
                return transaction
            }
        }
        cursor?.close()
        db.close()
        return null
    }

    fun getAllTransactions(userId: String): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM ${TransactionTable.TABLE_TRANSACTIONS}", null)

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_TITLE)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_AMOUNT)),
                    date = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_USER_ID)),
                    categoryid = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_CATEGORY_ID)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DESCRIPTION)),
                    transactionType = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_TRANSACTION_TYPE))

                )
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return transactions
    }

    fun getUnSyncedTransactions(userId: String): List<Transaction> {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val unSyncedList = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        // Update query to filter by both userId and sync status
        val selectQuery = "SELECT * FROM ${TransactionTable.TABLE_TRANSACTIONS} WHERE ${TransactionTable.COLUMN_SYNC_STATUS} = 0 AND ${TransactionTable.COLUMN_USER_ID} = ?"
        val cursor: Cursor = db.rawQuery(selectQuery, arrayOf(userId))

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_TITLE)),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_AMOUNT)),
                    date = cursor.getLong(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DATE)),
                    userid = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_USER_ID)),
                    transactionType = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_TRANSACTION_TYPE)),
                    categoryid = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_CATEGORY_ID)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(TransactionTable.COLUMN_DESCRIPTION))
                )
                unSyncedList.add(transaction)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return unSyncedList
    }


    fun updateCategoryId(localId: String, firebaseId: String): Boolean {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionTable.COLUMN_CATEGORY_ID, firebaseId) // Update local ID with Firebase ID
        }
        val result = db.update(
            TransactionTable.TABLE_TRANSACTIONS,
            values,
            "${TransactionTable.COLUMN_CATEGORY_ID} = ?",
            arrayOf(localId)
        )
        db.close()
        return result > 0
    }

    //method to update a transaction
    fun updateTransactionId(localId: String, firebaseId: String): Boolean {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionTable.COLUMN_ID, firebaseId) // Update local ID with Firebase ID
        }
        val result = db.update(
            TransactionTable.TABLE_TRANSACTIONS,
            values,
            "${TransactionTable.COLUMN_ID} = ?",
            arrayOf(localId)
        )
        db.close()
        return result > 0
    }

    // method to mark transaction as synced
    fun markAsSynced(goalId: String) {
        // This method was adapted from geeksforgeeks
        // https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/
        // scoder13
        // https://www.geeksforgeeks.org/user/scoder13/contributions/?itm_source=geeksforgeeks&itm_medium=article_author&itm_campaign=auth_user
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put(TransactionTable.COLUMN_SYNC_STATUS, 1)  // Mark as synced
        }
        db.update(TransactionTable.TABLE_TRANSACTIONS, contentValues, "${TransactionTable.COLUMN_ID} = ?", arrayOf(goalId))
        db.close()
    }
}