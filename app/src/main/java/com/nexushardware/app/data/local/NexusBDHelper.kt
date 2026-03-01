package com.nexushardware.app.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.nexushardware.app.data.model.CarritoItem
import java.util.Date
import com.nexushardware.app.domain.model.Producto
import com.nexushardware.app.data.model.User

//Mi db del proyecto
class NexusBDHelper(context: Context): SQLiteOpenHelper(context, "NexusHardware.db",null, 1) {

    companion object {
        const val ESTADO_PENDIENTE = 0
        const val ESTADO_SINCRONIZADO = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {

//

        val crearTablaCarrito = """
            CREATE TABLE carrito (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id TEXT,
                producto_id TEXT,
                cantidad INTEGER,
                fecha_agregado TEXT,
                estado_sync INTEGER DEFAULT $ESTADO_PENDIENTE,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                FOREIGN KEY(producto_id) REFERENCES productos(id)
            )
        """.trimIndent()
        db?.execSQL(crearTablaCarrito)


    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // En desarrollo, si cambias la BD, borramos todo y recreamos
        db?.execSQL("DROP TABLE IF EXISTS carrito")
        db?.execSQL("DROP TABLE IF EXISTS productos")
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }


    //funcion mejorada porque verifica si el usuario ya tiene en el carrito mas unidades del mismo item
    fun agregarAlCarrito(usuarioId: String, productoId: String, cantidad: Int): Long {
        val db = this.writableDatabase

        // Validamo si ya existe este producto en el carrito de un usuario
        val sqlConsulta = "SELECT id, cantidad FROM carrito WHERE usuario_id=? AND producto_id=? AND estado_sync=?"
        val cursor = db.rawQuery(sqlConsulta, arrayOf(usuarioId, productoId, ESTADO_PENDIENTE.toString()))

        val resultado: Long

        if (cursor.moveToFirst()) {
            // Si existe hacemos un update acumulando la cantidad
            val idCarritoExistente = cursor.getInt(0)
            val cantidadActual = cursor.getInt(1)

            val values = ContentValues().apply {
                put("cantidad", cantidadActual + cantidad)
            }
            // actualizamos esa fila especifica
            resultado = db.update("carrito", values, "id=?", arrayOf(idCarritoExistente.toString())).toLong()
        } else {
            //si no existe, hacemos el insert normal
            val values = ContentValues().apply {
                put("usuario_id", usuarioId)
                put("producto_id", productoId)
                put("cantidad", cantidad)
                put("fecha_agregado", Date().toString())
                put("estado_sync", ESTADO_PENDIENTE)
            }
            resultado = db.insert("carrito", null, values)
        }
        cursor.close()
        db.close()
        return resultado
    }

    fun obtenerCarrito(usuarioId: String): List<CarritoItem> {
        val lista = mutableListOf<CarritoItem>()
        val db = this.readableDatabase

        // query para obtener soloo la cantidad del carrito Y el nombre/precio del producto
        //solo traemos los que tengan estado_sync = 0 (Pendientes de compra)
        val sql = """
            SELECT id, producto_id, cantidad 
            FROM carrito
            WHERE usuario_id = ? AND estado_sync = $ESTADO_PENDIENTE
        """

        val cursor = db.rawQuery(sql, arrayOf(usuarioId))

        if (cursor.moveToFirst()) {
            do {
                val idCarrito = cursor.getInt(0)
                val idProducto = cursor.getString(1)
                val nombre = ""
                val precio = 0.0
                val cantidad = cursor.getInt(2)
                val imagen = ""

                lista.add(CarritoItem(idCarrito, idProducto, nombre, precio, cantidad, imagen))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    //Función para borrar un item del carrito
    fun eliminarItemCarrito(idCarrito: Int): Int {
        val db = this.writableDatabase
        return db.delete("carrito", "id=?", arrayOf(idCarrito.toString()))
    }

    //nueva funcion para procesar la compra
    fun procesarCompra(usuarioId: Int): Int {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put("estado_sync", ESTADO_SINCRONIZADO)
        }

        // aqui se actualiza todos los registros pendientes del usuario
        val filasActualizadas = db.update(
            "carrito",
            values,
            "usuario_id=? AND estado_sync=?",
            arrayOf(usuarioId.toString(), ESTADO_PENDIENTE.toString())
        )

        db.close()
        return filasActualizadas// devuelve cuantos productos se compraron
    }

}