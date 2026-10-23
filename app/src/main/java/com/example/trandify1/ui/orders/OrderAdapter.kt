package com.example.trandify1.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trandify1.databinding.ItemOrderRowBinding

data class OrderItem(
    val id: Long,
    val orderId: String,
    val itemsSummary: String,
    val totalPrice: Double,
    val status: String,
    val date: String
)

class OrderAdapter(
    private val orders: List<OrderItem>,
    private val onUpdate: (OrderItem) -> Unit,
    private val onDelete: (OrderItem) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ItemOrderRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        with(holder.binding) {
            tvOrderId.text = "Order #${order.orderId}"
            tvOrderDate.text = order.date
            tvOrderItems.text = order.itemsSummary
            tvOrderTotal.text = String.format("₹%.2f", order.totalPrice)
            tvOrderStatus.text = order.status

            btnUpdateOrder.setOnClickListener {
                onUpdate(order)
            }

            btnDeleteOrder.setOnClickListener {
                onDelete(order)
            }
        }
    }

    override fun getItemCount(): Int = orders.size
}