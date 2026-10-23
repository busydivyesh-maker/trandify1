package com.example.trandify1.ui.orders

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trandify1.data.provider.OrderContentProvider
import com.example.trandify1.databinding.ActivityOrdersBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarOrders.setNavigationOnClickListener {
            finish()
        }

        binding.fabAddOrder.setOnClickListener {
            insertSampleOrder()
        }

        loadOrders()
    }

    private fun loadOrders() {
        val ordersList = mutableListOf<OrderItem>()
        val cursor = contentResolver.query(
            OrderContentProvider.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val idCol = it.getColumnIndex(OrderContentProvider.COLUMN_ID)
            val orderIdCol = it.getColumnIndex(OrderContentProvider.COLUMN_ORDER_ID)
            val itemsCol = it.getColumnIndex(OrderContentProvider.COLUMN_ITEMS)
            val totalCol = it.getColumnIndex(OrderContentProvider.COLUMN_TOTAL)
            val statusCol = it.getColumnIndex(OrderContentProvider.COLUMN_STATUS)
            val dateCol = it.getColumnIndex(OrderContentProvider.COLUMN_DATE)

            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val orderId = it.getString(orderIdCol) ?: ""
                val items = it.getString(itemsCol) ?: ""
                val total = it.getDouble(totalCol)
                val status = it.getString(statusCol) ?: "Pending"
                val date = it.getString(dateCol) ?: ""

                ordersList.add(OrderItem(id, orderId, items, total, status, date))
            }
        }

        if (ordersList.isEmpty()) {
            binding.rvOrders.visibility = View.GONE
            binding.tvNoOrders.visibility = View.VISIBLE
        } else {
            binding.rvOrders.visibility = View.VISIBLE
            binding.tvNoOrders.visibility = View.GONE
        }

        val adapter = OrderAdapter(
            ordersList,
            onUpdate = { order ->
                updateOrderStatus(order)
            },
            onDelete = { order ->
                deleteOrder(order)
            }
        )

        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
    }

    private fun insertSampleOrder() {
        val randomId = UUID.randomUUID().toString().take(6).uppercase()
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val values = ContentValues().apply {
            put(OrderContentProvider.COLUMN_ORDER_ID, "TRND-$randomId")
            put(OrderContentProvider.COLUMN_ITEMS, "Oversized Charcoal Hoodie (x1)")
            put(OrderContentProvider.COLUMN_TOTAL, 1299.00)
            put(OrderContentProvider.COLUMN_STATUS, "Processing")
            put(OrderContentProvider.COLUMN_DATE, dateStr)
        }

        contentResolver.insert(OrderContentProvider.CONTENT_URI, values)
        loadOrders()
        Snackbar.make(binding.root, "Order inserted via Content Provider!", Snackbar.LENGTH_SHORT).show()
    }

    private fun updateOrderStatus(order: OrderItem) {
        val newStatus = if (order.status == "Processing") "Delivered" else "Processing"
        val values = ContentValues().apply {
            put(OrderContentProvider.COLUMN_STATUS, newStatus)
        }
        val uri = android.content.ContentUris.withAppendedId(OrderContentProvider.CONTENT_URI, order.id)
        contentResolver.update(uri, values, null, null)
        loadOrders()
        Snackbar.make(binding.root, "Order status updated to $newStatus", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteOrder(order: OrderItem) {
        val uri = android.content.ContentUris.withAppendedId(OrderContentProvider.CONTENT_URI, order.id)
        contentResolver.delete(uri, null, null)
        loadOrders()
        Snackbar.make(binding.root, "Order deleted via Content Provider", Snackbar.LENGTH_SHORT).show()
    }
}