package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var crear_casa: Button
    private lateinit var ver_casa: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val ref=FirebaseDatabase.getInstance().getReference()
//
//        ref.child("Hola").setValue("loooooococococococ")

        crear_casa = findViewById(R.id.bcrear)
        ver_casa = findViewById(R.id.bver)

        crear_casa.setOnClickListener {
            val activity = Intent(applicationContext, CrearCasa::class.java)
            startActivity(activity)
        }

        ver_casa.setOnClickListener {
            val activity = Intent(applicationContext, VerCasas::class.java)
            startActivity(activity)
        }

    }

    override fun onBackPressed() {
        finish()
        val intent: Intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}