package com.nexushardware.app.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.nexushardware.app.domain.model.Producto
import kotlinx.coroutines.tasks.await

class ProductRemoteDataSource {
    private val firestore = FirebaseFirestore.getInstance()
    suspend fun getProducts(): List<Producto> {
        val snapshot = firestore.collection("productos").get().await()

        return snapshot.documents.map { doc ->
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
    }


}
