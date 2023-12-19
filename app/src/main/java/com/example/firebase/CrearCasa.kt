package com.example.firebase

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Job

class CrearCasa : AppCompatActivity() {

    private lateinit var nombre : EditText
    private lateinit var fundador : EditText
    private lateinit var anio_fundacion : EditText
    private lateinit var escudo: ImageView
    private lateinit var bcrear: Button
    private lateinit var bvolver: Button


    private var url_escudo: Uri? = null
    private lateinit var database_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference
    private lateinit var lista_casas: MutableList<Pojo_casa>

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_casa)

        val this_actividad = this
        job = Job()

        nombre = findViewById(R.id.nombre)
        fundador = findViewById(R.id.fundador)
        anio_fundacion = findViewById(R.id.anio)
        escudo = findViewById(R.id.imageView)
        bcrear = findViewById(R.id.crear)
        bvolver = findViewById(R.id.volver)

        database_ref = FirebaseDatabase.getInstance().getReference()
        storage_ref = FirebaseStorage.getInstance().getReference()
        lista_casas = Utilidades.obtenerListaCasas(database_ref)
    }
}