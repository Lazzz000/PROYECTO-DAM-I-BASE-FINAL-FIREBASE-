package com.nexushardware.app.utils

import com.google.firebase.firestore.FirebaseFirestore

object AgregarListaProductos {
    private val firestore = FirebaseFirestore.getInstance()

    fun insertarProductosDemoSiVacio() {
        firestore.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    insertarProductosDemo()
                }
            }
    }
    fun insertarProductosDemo() {

        val productos = listOf(
            hashMapOf(
                "nombre" to "AMD Radeon RX 6600 8GB GDDR6",
                "descripcion" to "Tarjeta gráfica eficiente y potente para gaming en 1080p con arquitectura RDNA 2.",
                "precio" to 1299.00,
                "stock" to 5,
                "categoria" to "GPU",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/amd-rx-6600.jpg"
            ),
            hashMapOf(
                "nombre" to "NVIDIA GeForce RTX 3060 12GB GDDR6",
                "descripcion" to "Tarjeta gráfica ideal para gaming en 1080p y 1440p con soporte Ray Tracing y DLSS.",
                "precio" to 1499.0,
                "stock" to 6,
                "categoria" to "GPU",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/NVIDIA%20GeForce%20RTX%203060%2012GB%20GDDR6.jpg"
            ),
            hashMapOf(
                "nombre" to "Intel Core i9-13900K 13va Generación",
                "descripcion" to "Procesador tope de gama con máximo rendimiento para gaming extremo, streaming y creación de contenido profesional.",
                "precio" to 2599.0,
                "stock" to 10,
                "categoria" to "CPU",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Intel%20Core%20i9-13900K%20.jpg"
            ),
            hashMapOf(
                "nombre" to "NVIDIA RTX 4090",
                "descripcion" to "Tarjeta gráfica de última generación 24GB. Máximo rendimiento para gaming en 4K y renderizado 3D",
                "precio" to 8499.0,
                "stock" to 8,
                "categoria" to "GPU",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/tarjetadevideo3.jpg"
            ),
            hashMapOf(
                "nombre" to "Intel Core i5-12400F 12va Gen",
                "descripcion" to "Procesador de alto rendimiento ideal para gaming y productividad avanzada.",
                "precio" to 799.0,
                "stock" to 10,
                "categoria" to "CPU",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/i5-1240.jpg"
            ),
            hashMapOf(
                "nombre" to "Kingston A400 480GB SSD SATA III 2.5",
                "descripcion" to "Unidad de estado sólido que mejora significativamente la velocidad de arranque y carga de aplicaciones.",
                "precio" to 169.0,
                "stock" to 4,
                "categoria" to "Almacenamiento",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/kingston%20480gb.jpg"
            ),
            hashMapOf(
                "nombre" to "Crucial BX500 1TB SSD SATA III 2.5",
                "descripcion" to "SSD confiable y eficiente para mejorar el rendimiento general del sistema.",
                "precio" to 329.0,
                "stock" to 10,
                "categoria" to "Almacenamiento",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/crucial%20bx500.jpg"
            ),
            hashMapOf(
                "nombre" to "Microsoft Bluetooth Mouse",
                "descripcion" to "Mouse inalámbrico compacto y moderno ideal para oficina y uso diario.",
                "precio" to 89.0,
                "stock" to 20,
                "categoria" to "Mouse",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Microsoft%20Bluetooth%20Mouse.jpg"
            ),
            hashMapOf(
                "nombre" to "HyperX Pulsefire Core RGB",
                "descripcion" to "Mouse gaming con sensor preciso y diseño cómodo para juegos competitivos.",
                "precio" to 149.0,
                "stock" to 25,
                "categoria" to "Mouse",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/HyperX%20Pulsefire%20Core%20RGB.jpg"
            ),
            hashMapOf(
                "nombre" to "Redragon K552 Kumara RGB Mecánico",
                "descripcion" to "Teclado mecánico compacto ideal para gaming competitivo, con iluminación RGB y switches duraderos.",
                "precio" to 199.0,
                "stock" to 25,
                "categoria" to "Teclados",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Redragon%20K552.jpg"
            ),
            hashMapOf(
                "nombre" to "Logitech G413 SE Mecánico",
                "descripcion" to "Teclado mecánico premium con diseño minimalista y alto rendimiento para gaming.",
                "precio" to 329.0,
                "stock" to 22,
                "categoria" to "Teclados",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Logitech%20G413.jpg"
            ),
            hashMapOf(
                "nombre" to "HyperX Alloy Origins Core RGB Mecánico",
                "descripcion" to "Teclado gaming compacto con switches HyperX y estructura sólida de aluminio.",
                "precio" to 399.0,
                "stock" to 18,
                "categoria" to "Teclados",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/HyperX%20Alloy%20Origins.jpg"
            ),
            hashMapOf(
                "nombre" to "Microsoft Wired Keyboard 600",
                "descripcion" to "Teclado básico y confiable ideal para oficina, estudio y uso doméstico.",
                "precio" to 69.0,
                "stock" to 30,
                "categoria" to "Teclados",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Microsoft%20Wired%20Keyboard%20600.jpg"
            ),
            hashMapOf(
                "nombre" to "Samsung Odyssey G5 27\" QHD 144Hz Curvo",
                "descripcion" to "Monitor gaming curvo de alta resolución QHD con 144Hz ideal para juegos competitivos y experiencia inmersiva.",
                "precio" to 1199.0,
                "stock" to 10,
                "categoria" to "Monitores",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Samsung%20Odyssey.jpg"
            ),
            hashMapOf(
                "nombre" to "MSI Optix G241 24 144Hz IPS",
                "descripcion" to "Monitor gaming con panel IPS y 144Hz diseñado para ofrecer colores vivos y experiencia fluida en juegos.",
                "precio" to 999.0,
                "stock" to 12,
                "categoria" to "Monitores",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/MSI%20Optix%20G241%2024%20144Hz%20IPS.jpg"
            ),
            hashMapOf(
                "nombre" to "ASUS TUF Gaming VG27AQ 27 165Hz",
                "descripcion" to "Monitor gaming de alto rendimiento con 165Hz y panel IPS para una experiencia fluida y colores precisos.",
                "precio" to 1499.0,
                "stock" to 8,
                "categoria" to "Monitores",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/ASUS%20TUF%20Gaming%20VG27AQ%2027%20165Hz.jpg"
            ),
            hashMapOf(
                "nombre" to "LG UltraGear 24 FHD 144Hz Gaming",
                "descripcion" to "Monitor gaming Full HD con panel IPS y 144Hz, ideal para eSports y uso profesional.",
                "precio" to 899.0,
                "stock" to 15,
                "categoria" to "Monitores",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/LG%20UltraGear%2024%20FHD%20144Hz%20Gaming.jpg"
            ),



            hashMapOf(
                "nombre" to "HyperX Cloud Stinger 2 Gaming",
                "descripcion" to "Audífonos gaming ligeros y cómodos con sonido envolvente ideal para largas sesiones de juego.",
                "precio" to 199.0,
                "stock" to 22,
                "categoria" to "Audífonos",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/HyperX%20Cloud%20Stinger%202%20Gaming.jpg"
            ),
            hashMapOf(
                "nombre" to "Logitech G435 Lightspeed Wireless",
                "descripcion" to "Audífonos inalámbricos ligeros con conectividad Bluetooth y Lightspeed, ideales para gaming y uso diario.",
                "precio" to 329.0,
                "stock" to 14,
                "categoria" to "Audífonos",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Logitech%20G435%20Lightspeed%20Wireless.jpg"
            ),
            hashMapOf(
                "nombre" to "Redragon H510 Zeus RGB",
                "descripcion" to "Audífonos gaming con iluminación RGB, sonido potente y micrófono desmontable.",
                "precio" to 219.0,
                "stock" to 13,
                "categoria" to "Audífonos",
                "urlImagen" to "https://sbpybzzizfjpjyjoewik.supabase.co/storage/v1/object/public/img/Redragon%20H510%20Zeus%20RGB.jpg"
            )

        )

        productos.forEach { producto ->
            firestore.collection("productos")
                .add(producto)
        }

    }
}