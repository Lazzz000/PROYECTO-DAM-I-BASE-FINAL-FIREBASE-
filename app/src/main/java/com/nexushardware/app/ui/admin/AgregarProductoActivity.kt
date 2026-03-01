package com.nexushardware.app.ui.admin

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nexushardware.app.databinding.ActivityAgregarProductoBinding
import com.nexushardware.app.data.local.NexusBDHelper

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarProductoBinding
    private lateinit var dbHelper: NexusBDHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = NexusBDHelper(this)

        binding.btnGuardarProducto.setOnClickListener {
        }
    }

}