package com.example.trandify1.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class OrderContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.trandify1.provider.orders"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/orders")

        private const val ORDERS = 1
        private const val ORDER_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "orders", ORDERS)
            addURI(AUTHORITY, "orders/#", ORDER_ID)
        }

        const val TABLE_NAME = "orders"
        const val COLUMN_ID = "_id"
        const val COLUMN_ORDER_ID = "order_id"
        const val COLUMN_ITEMS = "items_summary"
        const val COLUMN_TOTAL = "total_price"
        const val COLUMN_STATUS = "status"
        const val COLUMN_DATE = "date"
    }

    private lateinit var dbHelper: OrderDatabaseHelper

    class OrderDatabaseHelper(context: android.content.Context) : SQLiteOpenHelper(context, "trendifyy_orders.db", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_ORDER_ID TEXT,
                    $COLUMN_ITEMS TEXT,
                    $COLUMN_TOTAL REAL,
                    $COLUMN_STATUS TEXT,
                    $COLUMN_DATE TEXT
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = OrderDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDataSource
        val cursor = when (uriMatcher.match(uri)) {
            ORDERS -> db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder ?: "$COLUMN_ID DESC")
            ORDER_ID -> {
                val id = ContentUris.parseId(uri)
                db.query(TABLE_NAME, projection, "$COLUMN_ID = ?", arrayOf(id.toString()), null, null, sortOrder)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            ORDERS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.orders"
            ORDER_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.orders"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = when (uriMatcher.match(uri)) {
            ORDERS -> db.insert(TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Unknown URI for insert: $uri")
        }
        if (id > 0) {
            context?.contentResolver?.notifyChange(uri, null)
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            ORDERS -> db.delete(TABLE_NAME, selection, selectionArgs)
            ORDER_ID -> {
                val id = ContentUris.parseId(uri)
                db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
            }
            else -> throw IllegalArgumentException("Unknown URI for delete: $uri")
        }
        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val count = when (uriMatcher.match(uri)) {
            ORDERS -> db.update(TABLE_NAME, values, selection, selectionArgs)
            ORDER_ID -> {
                val id = ContentUris.parseId(uri)
                db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
            }
            else -> throw IllegalArgumentException("Unknown URI for update: $uri")
        }
        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    private val SQLiteOpenHelper.readableDataSource: SQLiteDatabase
        get() = readableDatabase
}