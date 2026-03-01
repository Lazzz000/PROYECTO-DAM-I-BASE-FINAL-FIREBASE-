package com.nexushardware.app.utils.adapters

import android.content.Context
import android.graphics.Color
import com.nexushardware.app.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.nexushardware.app.domain.model.Producto
import com.nexushardware.app.databinding.ItemProductoBinding
import java.text.NumberFormat
import java.util.Locale
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.nexushardware.app.data.local.NexusBDHelper
import com.nexushardware.app.utils.session.SessionManager

class ProductoAdapter(
    private val context: Context,
    private val onProductoClick: (Producto) -> Unit// Para manejar clics en el futuro
    ) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(DiffCallback()) {

    inner class ProductoViewHolder(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(producto: Producto) {

            binding.tvNombre.text = producto.nombre
            binding.tvCategoria.text = producto.categoria

            val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
            binding.tvPrecio.text = format.format(producto.precio)

            Glide.with(binding.root.context)
                .load(producto.urlImagen.takeIf { it.isNotEmpty() })
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .fallback(R.drawable.ic_image_placeholder)
                .into(binding.imgProducto)

            binding.root.setOnClickListener {
                onProductoClick(producto)
            }

            binding.btnAgregar.setOnClickListener {
                // Próximamente: agregar al carrito
                //logica del boton agregar
                if (!producto.id.isEmpty()) {
                    val db = NexusBDHelper(context)
                    // Por ahora usamos el usuario_id del admin
                    val exito = db.agregarAlCarrito(usuarioId = SessionManager.getCurrentUser()?.id?:"", productoId = producto.id, cantidad = 1)

                    if (exito > -1) {
                        //snackbar de exito
                        Snackbar.make(binding.root, "✅ Agregado al Carrito", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#03DAC5"))
                            .setTextColor(Color.BLACK)
                            .setActionTextColor(Color.BLACK) // El botón "OK" también en negro
                            .setAction("OK") {
                                // Se cierra automáticamente al hacer clic
                            }
                            .show()
                    }
                    else {
                        // snackbar de errores
                        Snackbar.make(binding.root, "❌ Error al agregar", Snackbar.LENGTH_LONG)
                            .setBackgroundTint(Color.parseColor("#CF6679"))
                            .setTextColor(Color.BLACK)
                            .show()
                    }
                } else {
                    Snackbar.make(binding.root, "Error: Producto no identificado", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(Color.parseColor("#CF6679"))
                        .setTextColor(Color.BLACK)
                        .show()
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}