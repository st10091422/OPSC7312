package com.opsc.opsc7312poe.api.local.db.tables

object TransactionTable {
    const val TABLE_TRANSACTIONS = "transactions"
    const val COLUMN_ID = "id"
    const val COLUMN_TITLE = "title"
    const val COLUMN_AMOUNT = "amount"
    const val COLUMN_DATE = "date"
    const val COLUMN_USER_ID = "userid"
    const val COLUMN_CATEGORY_ID = "categoryid"
    const val COLUMN_DESCRIPTION = "description"
    const val COLUMN_TRANSACTION_TYPE = "transactionType"
    const val COLUMN_SYNC_STATUS = "syncstatus"

    const val CREATE_TABLE = ("CREATE TABLE $TABLE_TRANSACTIONS ("
            + "$COLUMN_ID TEXT PRIMARY KEY,"
            + "$COLUMN_TITLE TEXT,"
            + "$COLUMN_AMOUNT REAL,"
            + "$COLUMN_DATE INTEGER,"
            + "$COLUMN_USER_ID TEXT,"
            + "$COLUMN_CATEGORY_ID TEXT,"
            + "$COLUMN_DESCRIPTION TEXT,"
            + "$COLUMN_TRANSACTION_TYPE TEXT,"
            + "${COLUMN_SYNC_STATUS} INTEGER)")
}