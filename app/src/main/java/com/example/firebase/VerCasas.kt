package com.example.firebase

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager

class VerCasas : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_casas)

        var estrellas = 0

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        estrellas = sharedPreferences.getString("puntos", "5")!!.toInt()
    }
}