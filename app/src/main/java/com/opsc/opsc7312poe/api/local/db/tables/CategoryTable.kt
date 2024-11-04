package com.opsc.opsc7312poe.api.local.db.tables

object CategoryTable {
    const val TABLE_CATEGORIES = "categories"
    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_ICON = "icon"
    const val COLUMN_USER_ID = "userid"
    const val COLUMN_SYNC_STATUS = "syncstatus"

    const val CREATE_TABLE = ("CREATE TABLE $TABLE_CATEGORIES ("
            + "$COLUMN_ID TEXT PRIMARY KEY,"
            + "$COLUMN_NAME TEXT,"
            + "$COLUMN_ICON TEXT,"
            + "$COLUMN_USER_ID TEXT,"
            + "${COLUMN_SYNC_STATUS} INTEGER)")
}