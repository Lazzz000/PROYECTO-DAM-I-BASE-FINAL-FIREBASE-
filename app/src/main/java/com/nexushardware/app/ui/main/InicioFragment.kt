package com.nexushardware.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.nexushardware.app.R
import com.nexushardware.app.databinding.FragmentInicioBinding
import com.nexushardware.app.domain.model.Slide
import com.nexushardware.app.utils.adapters.ProductoAdapter
import com.nexushardware.app.utils.session.SessionManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.nexushardware.app.domain.model.Producto
import com.nexushardware.app.ui.detail.DetalleActivity
import me.relex.circleindicator.CircleIndicator3
import kotlin.math.abs

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var handler: Handler

    private lateinit var productoAdapter: ProductoAdapter

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 👤 Saludo personalizado
        SessionManager.getCurrentUser()?.let { user ->
            val primerNombre = user.nombreCompleto
                ?.split(" ")
                ?.first()
                ?.lowercase()
                ?.replaceFirstChar { it.uppercase() }

            binding.tvWelcome.text = "Bienvenido a Nexus, $primerNombre"
        }

        // 🔥 BOTÓN VER TODO → Cambia al fragment Productos
        binding.tvVerTodo.setOnClickListener {
            val bottomNav = requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottomNavigation)

            bottomNav.selectedItemId = R.id.nav_productos
        }

        initViews()
        setupCarousel()
        setupRecycler()
        cargarNovedades()
    }

    // Inicializar vistas
    private fun initViews() {
        viewPager = binding.viewPagerSlider
        indicator = binding.indicator
        handler = Handler(Looper.getMainLooper())
    }

    // Configurar carrusel
    private fun setupCarousel() {

        val slideList = listOf(
            Slide(R.drawable.monitores, "Monitores Gamer", "Potencia y calidad visual"),
            Slide(R.drawable.perifericos, "Periféricos RGB", "Ilumina tu setup"),
            Slide(R.drawable.procesadores, "Procesadores", "Máximo rendimiento"),
            Slide(R.drawable.tarjetavideo, "Tarjetas de Video", "Gráficos de última generación")
        )

        val adapter = SliderAdapter(slideList)
        viewPager.adapter = adapter
        indicator.setViewPager(viewPager)


        viewPager.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3

            val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)

            setPageTransformer { page, position ->
                page.translationX = -offsetPx * position
                val scale = 0.85f + (1 - abs(position)) * 0.15f
                page.scaleY = scale
                page.alpha = 0.5f + (1 - abs(position)) * 0.5f
            }
        }

        //  AutoSlide cada 3 segundos
        handler.postDelayed(autoSlideRunnable, 3000)

        // ⏸ Pausar cuando el usuario interactúa
        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(autoSlideRunnable)
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    handler.removeCallbacks(autoSlideRunnable)
                    handler.postDelayed(autoSlideRunnable, 3000)
                }
            }
        })
    }

    //  Runnable del AutoSlide
    private val autoSlideRunnable = object : Runnable {
        override fun run() {

            val itemCount = viewPager.adapter?.itemCount ?: 0
            if (itemCount <= 1) return

            val nextItem = (viewPager.currentItem + 1) % itemCount
            viewPager.currentItem = nextItem

            handler.removeCallbacks(this)
            handler.postDelayed(this, 3000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Evitar memory leak
        handler.removeCallbacks(autoSlideRunnable)

        _binding = null
    }

    private fun setupRecycler() {

        productoAdapter = ProductoAdapter(requireContext()) { producto ->
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

        binding.rvNovedades.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = productoAdapter
        }
    }

    private fun cargarNovedades() {

        firestore.collection("productos")
            .limit(6)
            .get()
            .addOnSuccessListener { result ->

                val lista = result.map { doc ->
                    Producto(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        descripcion = doc.getString("descripcion") ?: "",
                        precio = doc.getDouble("precio") ?: 0.0,
                        stock = doc.getLong("stock")?.toInt() ?: 0,
                        categoria = doc.getString("categoria") ?: "",
                        urlImagen = doc.getString("urlImagen") ?: ""
                    )
                }

                productoAdapter.submitList(lista)
            }
            .addOnSuccessListener { result ->
                Log.d("FIRESTORE_TEST", "Docs encontrados: ${result.size()}")
    }
}
}