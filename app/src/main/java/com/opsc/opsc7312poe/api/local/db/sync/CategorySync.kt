package com.opsc.opsc7312poe.api.local.db.sync

import android.content.Context
import android.util.Log
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.api.local.NotificationManager
import com.opsc.opsc7312poe.api.local.db.databasehelper.CategoryDatabaseHelper
import com.opsc.opsc7312poe.api.viewmodel.CategoryViewModel
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.opsc.opsc7312poe.api.data.Category
import com.opsc.opsc7312poe.api.data.Ids
import com.opsc.opsc7312poe.api.local.db.databasehelper.TransactionDatabaseHelper

class CategorySync(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    //private var notificationHandler: NotificationManager = NotificationManager(appContext)

    override suspend fun doWork(): Result {
        val categoryDbHelper = CategoryDatabaseHelper(applicationContext)

         val localUser = LocalUser.getInstance(applicationContext)

        val userId = localUser.getUser()?.id
        // Get the unsynced categories
        val unSyncedCategories = userId?.let { categoryDbHelper.getUnSyncedCategories(it) }

        if (unSyncedCategories!=null) {
            // Get the token
            val categoryController = CategoryViewModel()
            Log.d("unSyncedCategories", "ca ${unSyncedCategories}")

            // Trigger sync directly with the suspend function
            val (status, ids) = categoryController.syncCategoriesSuspend(unSyncedCategories)
            Log.e("ids", "ids: $ids")
            if (status) {
                markAsSynced(unSyncedCategories, categoryDbHelper)
                updateCategoryIds(ids, categoryDbHelper, userId)
                Log.d("unSyncedCategories", "ca ${userId.let { categoryDbHelper.getUnSyncedCategories(it) }}")

            } else {
                Log.e("SyncWorker", "Sync failed")
                return Result.retry()
            }
        }

        return syncRemoteToLocal(categoryDbHelper, userId)
    }

    private suspend fun syncRemoteToLocal(categoryDbHelper: CategoryDatabaseHelper, userId: String?): Result {

        return try {
            val viewModel = CategoryViewModel()

            // Get remote goals using a suspend function
            val remoteCategoriess = userId?.let { viewModel.getRemoteCategoriesSuspend(it) }
            Log.d("remoteCategoriess", "remoteCategoriess ${remoteCategoriess}")

            if (remoteCategoriess != null) {
                remoteCategoriess.forEach { remoteCategory ->
                    val existingCategory = categoryDbHelper.getCategoryById(remoteCategory.id)
                    Log.d("existingCategory", "existingCategory ${existingCategory}")
                    if (existingCategory == null || existingCategory != remoteCategory) {
                        Log.d("dbHelper.getAllCategories()", "ca ${categoryDbHelper.getAllCategories(userId).find { id.toString() == remoteCategory.id }}")

                        categoryDbHelper.addCategorySync(remoteCategory)
                    } else {
                        // Optionally update the local goal if needed
                    }
                }
                //notificationHandler.syncSuccessfulNotification()
                Result.success()  // Mark as successful if all transactions synced
            } else {
                // Show failure notification if remote sync fails
                //notificationHandler.syncFailNotification()
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("syncRemoteToLocal", "Error syncing categories", e)
            //notificationHandler.syncFailNotification()
            Result.failure()
        }
    }


    private fun updateCategoryIds(
        ids: List<Ids>?,
        dbHelper: CategoryDatabaseHelper,
        userId: String
    ) {
        val transactionDatabaseHelper = TransactionDatabaseHelper(applicationContext)
        if (ids != null) {
            for (id in ids){
                dbHelper.updateCategoryId(id.localId, id.firebaseId)
                transactionDatabaseHelper.updateCategoryId(id.localId, id.firebaseId)
            }
        }

        Log.d("dbHelper.getAllCategories()", "categories ${dbHelper.getAllCategories(userId)}")
        Log.d("getAllTransactions", "transactions ${transactionDatabaseHelper.getAllTransactions(userId)}")
    }

    private fun markAsSynced(unSyncedCategories: List<Category>, dbHelper: CategoryDatabaseHelper) {
        unSyncedCategories.forEach { category ->
            dbHelper.markAsSynced(category.id) // Assuming Goal has a method to get its ID
        }
    }
}