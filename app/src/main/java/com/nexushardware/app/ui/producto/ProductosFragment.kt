package com.nexushardware.app.ui.producto

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nexushardware.app.data.local.NexusBDHelper
import com.nexushardware.app.data.remote.ProductRemoteDataSource
import com.nexushardware.app.data.repository.ProductRepositoryImpl
import com.nexushardware.app.domain.model.Producto
import com.nexushardware.app.databinding.FragmentProductosBinding
import com.nexushardware.app.domain.usecase.GetProductsUseCase
import com.nexushardware.app.ui.detail.DetalleActivity
import com.nexushardware.app.utils.AgregarListaProductos
import com.nexushardware.app.utils.adapters.ProductoAdapter

class ProductosFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProductosBinding? = null
    private val binding get() = _binding!!
    //private lateinit var dbHelper: NexusBDHelper

    private lateinit var adapter: ProductoAdapter
    private lateinit var viewModel: ProductoViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCatTodos.isSelected = true

        // Asignamos el listener a cada botón usando el binding
        binding.btnCatTodos.setOnClickListener(this)
        binding.btnCatCpu.setOnClickListener(this)
        binding.btnCatGpu.setOnClickListener(this)
        binding.btnCatMonitores.setOnClickListener(this)
        binding.btnCatTeclados.setOnClickListener(this)
        binding.btnCatAlmacenamiento.setOnClickListener(this)
        binding.btnCatAudifonos.setOnClickListener(this)


        setupRecyclerView()
        setupViewModel()
        setupSearch()

        viewModel.cargarProductos()


    }

    private fun setupRecyclerView() {
       // val lista = obtenerProductosDeBD()

        adapter = ProductoAdapter(requireContext()){ producto ->
            // Creamos el Intent para abrir el detalle
            val intent = Intent(context, DetalleActivity::class.java).apply {
                putExtra("id", producto.id)
                putExtra("nombre", producto.nombre)
                putExtra("descripcion", producto.descripcion)
                putExtra("precio", producto.precio)
                putExtra("stock", producto.stock)
                putExtra("categoria", producto.categoria)
                putExtra("url", producto.urlImagen)
            }
            startActivity(intent)
        }

        binding.rvProductos.layoutManager = LinearLayoutManager(context)
        binding.rvProductos.adapter = adapter
    }

    private fun setupViewModel() {

        // Si no usas Hilt, debes crear las dependencias manualmente
        val remote = ProductRemoteDataSource()
        val repository = ProductRepositoryImpl(remote)
        val useCase = GetProductsUseCase(repository)

        viewModel = ViewModelProvider(
            this,
            ProductoViewModelFactory(useCase)
        )[ProductoViewModel::class.java]

        //Hacemos visible la animacion
        binding.lottieLoadingProductos.visibility = View.VISIBLE

        viewModel.productos.observe(viewLifecycleOwner) {
            //Ocultamos la animacion de loading productos
            binding.lottieLoadingProductos.visibility = View.GONE

            adapter.submitList(it)

            // Usamos post para esperar a que el RecyclerView termine de dibujar y llevarla a la posicion 0 del scroll
            binding.rvProductos.post {
                binding.rvProductos.scrollToPosition(0)
            }
        }
    }
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filtrarProductos(newText ?: "")
                return true
            }
        })
    }

    override fun onClick(v: View?) {

        resetBotonesCategoria()

        when (v?.id) {
            binding.btnCatTodos.id -> {
                v.isSelected = true
                filtrarFirebase("todos")
            }
            binding.btnCatCpu.id-> {
                v.isSelected = true
                filtrarFirebase("CPU")
            }
            binding.btnCatGpu.id -> {
                v.isSelected = true
                filtrarFirebase("GPU")
            }
            binding.btnCatMonitores.id -> {
                v.isSelected = true
                filtrarFirebase("Monitores")
            }
            binding.btnCatTeclados.id -> {
                v.isSelected = true
                filtrarFirebase("Teclados")
            }
            binding.btnCatAlmacenamiento.id -> {
                v.isSelected = true
                filtrarFirebase("Almacenamiento")
            }
            binding.btnCatAudifonos.id -> {
                v.isSelected = true
                filtrarFirebase("Audífonos")
            }
        }
    }

    private fun filtrarFirebase(cat:String){
        // Mostramos la animacion de carga
        binding.lottieLoadingProductos.visibility = View.VISIBLE
        viewModel.filtrarCategoriaProductos(cat)

    }

    private fun resetBotonesCategoria(){
        binding.apply {
            btnCatTodos.isSelected = false
            btnCatCpu.isSelected = false
            btnCatGpu.isSelected = false
            btnCatMonitores.isSelected = false
            btnCatTeclados.isSelected = false
            btnCatAlmacenamiento.isSelected = false
            btnCatAudifonos.isSelected = false

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}