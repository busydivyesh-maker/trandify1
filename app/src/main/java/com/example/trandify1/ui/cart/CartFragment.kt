package com.example.trandify1.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trandify1.R
import com.example.trandify1.databinding.FragmentCartBinding
import com.example.trandify1.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupPromoCode()
        observeCartState()

        binding.btnCheckout.setOnClickListener {
            if (com.example.trandify1.utils.AirplaneModeUtils.isAirplaneModeOn(requireContext())) {
                Snackbar.make(binding.root, "Cannot proceed to checkout while Airplane Mode is active", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            binding.loadingOverlay.visibility = View.VISIBLE
            binding.btnCheckout.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                val currentItems = viewModel.cartItems.value
                if (currentItems.isNotEmpty()) {
                    val randomId = java.util.UUID.randomUUID().toString().take(6).uppercase()
                    val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    val itemsSummary = currentItems.joinToString(", ") { "${it.product.title} (x${it.quantity})" }
                    val total = currentItems.sumOf { it.totalPrice }

                    val values = android.content.ContentValues().apply {
                        put(com.example.trandify1.data.provider.OrderContentProvider.COLUMN_ORDER_ID, "TRND-$randomId")
                        put(com.example.trandify1.data.provider.OrderContentProvider.COLUMN_ITEMS, itemsSummary)
                        put(com.example.trandify1.data.provider.OrderContentProvider.COLUMN_TOTAL, total)
                        put(com.example.trandify1.data.provider.OrderContentProvider.COLUMN_STATUS, "Processing")
                        put(com.example.trandify1.data.provider.OrderContentProvider.COLUMN_DATE, dateStr)
                    }
                    requireContext().contentResolver.insert(com.example.trandify1.data.provider.OrderContentProvider.CONTENT_URI, values)
                }

                kotlinx.coroutines.delay(1500)
                viewModel.clearCart()
                binding.loadingOverlay.visibility = View.GONE
                binding.btnCheckout.isEnabled = true
                Snackbar.make(binding.root, getString(R.string.order_success), Snackbar.LENGTH_LONG).show()
            }
        }

        binding.btnCartExplore.setOnClickListener {
            val activity = activity as? com.example.trandify1.MainActivity
            activity?.navigateToTab(R.id.nav_home)
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChange = { item, newQty ->
                viewModel.updateCartQuantity(item, newQty)
            },
            onDeleteItem = { item, position ->
                viewModel.removeCartItem(item)
                Snackbar.make(binding.root, "${item.product.title} removed from cart", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.restoreCartItem(item, position)
                    }
                    .show()
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupPromoCode() {
        binding.btnApplyPromo.setOnClickListener {
            val code = binding.etPromoCode.text.toString()
            if (viewModel.applyPromoCode(code)) {
                Snackbar.make(binding.root, getString(R.string.promo_applied), Snackbar.LENGTH_SHORT).show()
            } else {
                binding.etPromoCode.error = getString(R.string.invalid_promo)
            }
        }
    }

    private fun observeCartState() {
        viewLifecycleOwner.lifecycleScope.launch {
            combine(viewModel.cartItems, viewModel.appliedPromo) { items, promo ->
                Pair(items, promo)
            }.collect { (items, promo) ->
                if (items.isEmpty()) {
                    binding.cartScrollView.visibility = View.GONE
                    binding.cardCartSummary.visibility = View.GONE
                    binding.emptyCartContainer.visibility = View.VISIBLE
                } else {
                    binding.cartScrollView.visibility = View.VISIBLE
                    binding.cardCartSummary.visibility = View.VISIBLE
                    binding.emptyCartContainer.visibility = View.GONE
                    cartAdapter.submitList(items)

                    // Calculate live summary
                    val subtotal = items.sumOf { it.totalPrice }
                    val discountPercentage = if (promo != null) 0.20 else 0.0
                    val discount = subtotal * discountPercentage
                    val deliveryFee = if (subtotal > 2000.0 || items.isEmpty()) 0.0 else 99.0
                    val total = subtotal - discount + deliveryFee

                    binding.tvSummarySubtotal.text = String.format("₹%.2f", subtotal)
                    binding.tvSummaryDiscount.text = String.format("-₹%.2f", discount)
                    binding.tvSummaryDelivery.text = if (deliveryFee == 0.0) "FREE" else String.format("₹%.2f", deliveryFee)
                    binding.tvSummaryTotal.text = String.format("₹%.2f", total)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}