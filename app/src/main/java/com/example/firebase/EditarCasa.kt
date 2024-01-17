package com.example.firebase

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class EditarCasa : AppCompatActivity(),CoroutineScope {

    private lateinit var nombre: EditText
    private lateinit var fundador: EditText
    private lateinit var anio_fundacion: EditText
    private lateinit var escudo: ImageView
    private lateinit var bmodificar: Button
    private lateinit var bvolver: Button
    private lateinit var bpuntuar: Button


    private var url_escudo: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private  lateinit var  pojo_casa:Pojo_casa
    private lateinit var lista_casas: MutableList<Pojo_casa>

//    private lateinit var fecha_creacion: EditText

    private lateinit var puntuacion: RatingBar

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_casa)

        val this_activity = this
        job = Job()

        pojo_casa = intent.getParcelableExtra<Pojo_casa>("casas")!!


        nombre = findViewById(R.id.nombre)
        fundador = findViewById(R.id.fundador)
        anio_fundacion = findViewById(R.id.anio)
        escudo = findViewById(R.id.imageView)
        bmodificar = findViewById(R.id.modificar)
        bvolver = findViewById(R.id.volver)

        bpuntuar = findViewById(R.id.bpuntos)

        nombre.setText(pojo_casa.nombre)
        fundador.setText(pojo_casa.fundador)
        anio_fundacion.setText(pojo_casa.anio_fundacion.toString())


//        fecha_creacion = findViewById(R.id.fecha)

        puntuacion = findViewById(R.id.ratingBar)

//        val materialDatePicker =
//            MaterialDatePicker.Builder.datePicker().setTitleText("Seleccionar fecha").build()
//
//        materialDatePicker.addOnPositiveButtonClickListener {
//            fecha_creacion.setText(materialDatePicker.headerText)
//        }
//        fecha_creacion.setOnClickListener {
//            materialDatePicker.show(supportFragmentManager, materialDatePicker.toString())
//        }

        var getRatingValue = 0.0f

        bpuntuar.setOnClickListener {
            getRatingValue = puntuacion.rating
            Toast.makeText(this_activity,"puntos: "+getRatingValue, Toast.LENGTH_LONG).show()
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        sharedPreferences.edit {
            putString("puntos",getRatingValue.toString())
        }


        Glide.with(applicationContext)
            .load(pojo_casa.escudo)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(escudo)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        lista_casas = Utilidades.obtenerListaCasas(db_ref)

        bmodificar.setOnClickListener {

            val dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            if (nombre.text.toString().trim().isEmpty() ||
                fundador.text.toString().trim().isEmpty() ||
                anio_fundacion.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()

            } else if ( !nombre.text.toString().trim().equals(pojo_casa.nombre) && Utilidades.existeCasa(lista_casas, nombre.text.toString().trim())) {
                Toast.makeText(applicationContext, "Esa casa ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                //GlobalScope(Dispatchers.IO)
                var url_escudo_firebase = String()
                launch {
                    if(url_escudo == null){
                        url_escudo_firebase = pojo_casa.escudo!!
                    }else{
                        val url_escudo_firebase =
                            Utilidades.guardarEscudo(st_ref, pojo_casa.id!!, url_escudo!!)
                    }


                    Utilidades.escribirCasa(
                        db_ref, pojo_casa.id!!,
                        nombre.text.toString().trim(),
                        fundador.text.toString().trim(),
                        anio_fundacion.text.toString().trim().toInt(),
                        dateTime,
                        url_escudo_firebase,
                        getRatingValue.toInt()
                    )
                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Club modificado con exito"
                    )
                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)
                }
            }

        }

        bvolver.setOnClickListener {
            val activity = Intent(applicationContext, VerCasas::class.java)
            startActivity(activity)
        }

        escudo.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

    }


    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri ->
        if(uri!=null){
            url_escudo = uri
            escudo.setImageURI(uri)
        }


    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}