package com.kangrio.carservertest

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.lang.reflect.Modifier

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        try {

            val evChargingConnectorTypeClass = android.car.EvConnectorType::class.java
            val fields = evChargingConnectorTypeClass.declaredFields
            for (field in fields) {
                field.isAccessible = true
                if (field.modifiers and Modifier.STATIC != 0) {
                    Log.i("MainActivity", "${field.name}")
                    Log.i("MainActivity", "${field.get(null)}")
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}