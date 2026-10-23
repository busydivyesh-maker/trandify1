package com.example.trandify1.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.trandify1.data.SampleData
import com.example.trandify1.data.model.CartItem
import com.example.trandify1.data.model.Product
import com.example.trandify1.data.model.UserProfile

class TrendifyyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "trendifyy.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val TABLE_PRODUCTS = "products"
        private const val TABLE_CART = "cart"

        private const val COL_USER_EMAIL = "email"
        private const val COL_USER_PASSWORD = "password"
        private const val COL_USER_NAME = "name"
        private const val COL_USER_ADDRESS = "address"
        private const val COL_USER_PHONE = "phone"
        private const val COL_USER_DOB = "dob"
        private const val COL_USER_IMAGE = "image_path"

        private const val COL_PROD_ID = "id"
        private const val COL_PROD_TITLE = "title"
        private const val COL_PROD_BRAND = "brand"
        private const val COL_PROD_DESC = "description"
        private const val COL_PROD_PRICE = "price"
        private const val COL_PROD_ORIGINAL_PRICE = "original_price"
        private const val COL_PROD_DISCOUNT = "discount"
        private const val COL_PROD_RATING = "rating"
        private const val COL_PROD_REVIEWS = "reviews"
        private const val COL_PROD_CATEGORY = "category"
        private const val COL_PROD_IMAGE = "image_url"

        private const val COL_CART_ID = "cart_id"
        private const val COL_CART_PROD_ID = "product_id"
        private const val COL_CART_SIZE = "size"
        private const val COL_CART_QTY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_EMAIL TEXT PRIMARY KEY,
                $COL_USER_PASSWORD TEXT,
                $COL_USER_NAME TEXT,
                $COL_USER_ADDRESS TEXT,
                $COL_USER_PHONE TEXT,
                $COL_USER_DOB TEXT,
                $COL_USER_IMAGE TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_PRODUCTS (
                $COL_PROD_ID TEXT PRIMARY KEY,
                $COL_PROD_TITLE TEXT,
                $COL_PROD_BRAND TEXT,
                $COL_PROD_DESC TEXT,
                $COL_PROD_PRICE REAL,
                $COL_PROD_ORIGINAL_PRICE REAL,
                $COL_PROD_DISCOUNT INTEGER,
                $COL_PROD_RATING REAL,
                $COL_PROD_REVIEWS INTEGER,
                $COL_PROD_CATEGORY TEXT,
                $COL_PROD_IMAGE TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_CART (
                $COL_CART_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CART_PROD_ID TEXT,
                $COL_CART_SIZE TEXT,
                $COL_CART_QTY INTEGER
            )
            """.trimIndent()
        )

        seedProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        onCreate(db)
    }

    private fun seedProducts(db: SQLiteDatabase) {
        for (product in SampleData.products) {
            val values = ContentValues().apply {
                put(COL_PROD_ID, product.id)
                put(COL_PROD_TITLE, product.title)
                put(COL_PROD_BRAND, product.brand)
                put(COL_PROD_DESC, product.description)
                put(COL_PROD_PRICE, product.price)
                put(COL_PROD_ORIGINAL_PRICE, product.originalPrice)
                put(COL_PROD_DISCOUNT, product.discountPercentage)
                put(COL_PROD_RATING, product.rating)
                put(COL_PROD_REVIEWS, product.reviewCount)
                put(COL_PROD_CATEGORY, product.category)
                put(COL_PROD_IMAGE, product.imageUrls.firstOrNull() ?: "")
            }
            db.insert(TABLE_PRODUCTS, null, values)
        }
    }

    fun registerUser(email: String, pass: String, name: String, phone: String, address: String, dob: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_EMAIL, email)
            put(COL_USER_PASSWORD, pass)
            put(COL_USER_NAME, name)
            put(COL_USER_PHONE, phone)
            put(COL_USER_ADDRESS, address)
            put(COL_USER_DOB, dob)
        }
        val result = db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun authenticateUser(email: String, pass: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_EMAIL),
            "$COL_USER_EMAIL = ? AND $COL_USER_PASSWORD = ?",
            arrayOf(email, pass),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserProfile(email: String): UserProfile? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COL_USER_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )
        var profile: UserProfile? = null
        if (cursor.moveToFirst()) {
            profile = UserProfile(
                name = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                address = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ADDRESS)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHONE)),
                dob = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_DOB)),
                imageUriPath = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_IMAGE))
            )
        }
        cursor.close()
        return profile
    }

    fun updateUserProfile(profile: UserProfile) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_NAME, profile.name)
            put(COL_USER_ADDRESS, profile.address)
            put(COL_USER_PHONE, profile.phone)
            put(COL_USER_DOB, profile.dob)
            put(COL_USER_IMAGE, profile.imageUriPath)
        }
        db.update(TABLE_USERS, values, "$COL_USER_EMAIL = ?", arrayOf(profile.email))
    }

    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_TITLE))
            val brand = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_BRAND))
            val desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_DESC))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_PRICE))
            val origPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PROD_ORIGINAL_PRICE))
            val discount = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_DISCOUNT))
            val rating = cursor.getFloat(cursor.getColumnIndexOrThrow(COL_PROD_RATING))
            val reviews = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_REVIEWS))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_CATEGORY))
            val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_IMAGE))

            list.add(
                Product(
                    id = id,
                    title = title,
                    brand = brand,
                    description = desc,
                    price = price,
                    originalPrice = origPrice,
                    discountPercentage = discount,
                    rating = rating,
                    reviewCount = reviews,
                    category = category,
                    imageUrls = listOf(imageUrl)
                )
            )
        }
        cursor.close()
        return list
    }

    fun addToCart(product: Product, size: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_CART_PROD_ID, product.id)
            put(COL_CART_SIZE, size)
            put(COL_CART_QTY, 1)
        }
        db.insert(TABLE_CART, null, values)
    }

    fun getCartItems(allProducts: List<Product>): List<CartItem> {
        val cartList = mutableListOf<CartItem>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CART, null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val prodId = cursor.getString(cursor.getColumnIndexOrThrow(COL_CART_PROD_ID))
            val size = cursor.getString(cursor.getColumnIndexOrThrow(COL_CART_SIZE))
            val qty = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CART_QTY))

            val product = allProducts.find { it.id == prodId }
            if (product != null) {
                cartList.add(CartItem(product = product, selectedSize = size, quantity = qty))
            }
        }
        cursor.close()
        return cartList
    }

    fun clearCart() {
        val db = writableDatabase
        db.delete(TABLE_CART, null, null)
    }
}