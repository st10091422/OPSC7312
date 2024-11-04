package com.opsc.opsc7312poe.api.local.db.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.api.local.NotificationManager
import com.opsc.opsc7312poe.api.local.db.databasehelper.TransactionDatabaseHelper
import com.opsc.opsc7312poe.api.viewmodel.TransactionViewModel

class TransactionSync (appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    //private var notificationHandler: NotificationManager = NotificationManager(appContext)

    override suspend fun doWork(): Result {
        // Helper to access transaction-related database operations
        val transactionDbHelper = TransactionDatabaseHelper(applicationContext)
        // Manager for handling user authentication tokens
        val localUser = LocalUser.getInstance(applicationContext)

        val userId = localUser.getUser()?.id
        // Retrieve any unsynced transactions for the user
        val unSyncedTransactions = userId?.let { transactionDbHelper.getUnSyncedTransactions(it) }

        if (unSyncedTransactions!=null) {

            val viewmodel = TransactionViewModel()

            // Perform sync operation for unsynced transactions (suspend function for async operation)
            val (status, ids) = viewmodel.syncTransactionsSuspend(unSyncedTransactions)
            Log.e("ids", "ids: $ids")

            // If sync was successful, mark transactions as synced and update category IDs
            if (status) {
                unSyncedTransactions.forEach { transaction ->
                    transactionDbHelper.markAsSynced(transaction.id)  // Assuming Transaction has a method to get its ID
                }
                if (ids != null) {
                    for (id in ids) {
                        transactionDbHelper.updateTransactionId(id.localId, id.firebaseId)
                    }
                }
            } else {
                Log.e("SyncWorker", "Sync failed")
                return Result.retry()  // Retry the work if sync fails
            }
        }

        // Synchronize remote transactions with local database
        return syncRemoteToLocal(transactionDbHelper, userId)
    }

    private suspend fun syncRemoteToLocal(transactionDbHelper: TransactionDatabaseHelper, userId: String?): Result{


        return try {
            val viewmodel = TransactionViewModel()

            // Retrieve remote transactions for the user
            val remoteTransactions = userId?.let { viewmodel.getRemoteTransactionsSuspend(it) }

            // If transactions were retrieved successfully
            if (remoteTransactions != null) {
                Log.d("remoteTransactions", "remoteTransactions $remoteTransactions")

                remoteTransactions.forEach { remoteTransaction ->
                    // Check if the transaction already exists locally
                    val transaction = transactionDbHelper.getTransaction(remoteTransaction.id)
                    Log.d("transaction", "transaction $transaction")

                    if (transaction == null) {
                        // Add new transactions from remote data
                        transactionDbHelper.addTransactionSync(remoteTransaction)
                    } else {
                        // Optional: Update existing transaction if needed
                    }
                }
                // Show success notification to the user
                //notificationHandler.syncSuccessfulNotification()
                Result.success()  // Mark as successful if all transactions synced
            } else {
                // Show failure notification if remote sync fails
               // notificationHandler.syncFailNotification()
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("syncRemoteToLocal", "Error syncing categories", e)
            //notificationHandler.syncFailNotification()
            Result.failure()
        }
    }
}