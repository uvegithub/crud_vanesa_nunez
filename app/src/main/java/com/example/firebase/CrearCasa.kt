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
import com.example.firebase.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CrearCasa : AppCompatActivity(), CoroutineScope {

    private lateinit var nombre: EditText
    private lateinit var fundador: EditText
    private lateinit var anio_fundacion: EditText
    private lateinit var escudo: ImageView
    private lateinit var bcrear: Button
    private lateinit var bvolver: Button

    private lateinit var bpuntuar: Button

    private var url_escudo: Uri? = null
    private lateinit var database_ref: DatabaseReference
    private lateinit var storage_ref: StorageReference
    private lateinit var lista_casas: MutableList<Pojo_casa>

    private lateinit var job: Job

    private lateinit var fecha_creacion: EditText

    private lateinit var puntuacion: RatingBar

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_casa)

        val this_activity = this
        job = Job()

        nombre = findViewById(R.id.nombre)
        fundador = findViewById(R.id.fundador)
        anio_fundacion = findViewById(R.id.anio)
        escudo = findViewById(R.id.imageView)
        bcrear = findViewById(R.id.crear)
        bvolver = findViewById(R.id.volver)

        bpuntuar = findViewById(R.id.bpuntos)

        database_ref = FirebaseDatabase.getInstance().getReference()
        storage_ref = FirebaseStorage.getInstance().getReference()
        lista_casas = Utilidades.obtenerListaCasas(database_ref)

        fecha_creacion = findViewById(R.id.fecha)

        puntuacion = findViewById(R.id.ratingBar)

        val materialDatePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText("Seleccionar fecha").build()

        materialDatePicker.addOnPositiveButtonClickListener {
            fecha_creacion.setText(materialDatePicker.headerText)
        }
        fecha_creacion.setOnClickListener {
            materialDatePicker.show(supportFragmentManager, materialDatePicker.toString())
        }

//        var anio = fecha_creacion.text.toString()
//            .slice(fecha_creacion.text.toString().length - 4..fecha_creacion.text.toString().length - 1)
//        var mes = fecha_creacion.text.toString()
//            .slice(fecha_creacion.text.toString().length - 12..fecha_creacion.text.toString().length - 10)
//        var dia = fecha_creacion.text.toString()
//            .slice(fecha_creacion.text.toString().length - 8..fecha_creacion.text.toString().length - 7)
//        var mes_numero = ""
//        if (mes.lowercase() == "Ene".lowercase()) {
//            mes_numero = "01"
//        } else if (mes.lowercase() == "Feb".lowercase()) {
//            mes_numero = "02"
//        } else if (mes.lowercase() == "Mar".lowercase()) {
//            mes_numero = "03"
//        } else if (mes.lowercase() == "Abr".lowercase()) {
//            mes_numero = "04"
//        } else if (mes.lowercase() == "May".lowercase()) {
//            mes_numero = "05"
//        } else if (mes.lowercase() == "Jun".lowercase()) {
//            mes_numero = "06"
//        } else if (mes.lowercase() == "Jul".lowercase()) {
//            mes_numero = "07"
//        } else if (mes.lowercase() == "Ago".lowercase()) {
//            mes_numero = "08"
//        } else if (mes.lowercase() == "Sep".lowercase()) {
//            mes_numero = "09"
//        } else if (mes.lowercase() == "Oct".lowercase()) {
//            mes_numero = "10"
//        } else if (mes.lowercase() == "Nov".lowercase()) {
//            mes_numero = "11"
//        } else if (mes.lowercase() == "Dic".lowercase()) {
//            mes_numero = "12"
//        }
//
//        var modificar_formato_fecha = anio + "-" + mes_numero + "-" + dia + "-"
//        fecha_creacion.setText(modificar_formato_fecha)

        var getRatingValue = 0.0f

        bpuntuar.setOnClickListener {
            getRatingValue = puntuacion.rating
            Toast.makeText(this_activity,"puntos: "+getRatingValue,Toast.LENGTH_LONG).show()
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        sharedPreferences.edit {
            putString("puntos",getRatingValue.toString())
        }

        bcrear.setOnClickListener {

            if (nombre.text.toString().trim().isEmpty() ||
                fundador.text.toString().trim().isEmpty() ||
                anio_fundacion.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el " +
                            "formulario", Toast.LENGTH_SHORT
                ).show()

            } else if (url_escudo == null) {
                Toast.makeText(
                    applicationContext, "Falta seleccionar el " +
                            "escudo", Toast.LENGTH_SHORT
                ).show()
            } else if (Utilidades.existeCasa(lista_casas, nombre.text.toString().trim())) {
                Toast.makeText(applicationContext, "Esa casa ya existe", Toast.LENGTH_SHORT)
                    .show()
            } else {

                var id_generado: String? = database_ref.child("howarts").child("casas").push().key

                //GlobalScope(Dispatchers.IO)
                launch {
                    val url_escudo_firebase =
                        Utilidades.guardarEscudo(storage_ref, id_generado!!, url_escudo!!)

                    Utilidades.escribirClub(
                        database_ref, id_generado!!,
                        nombre.text.toString().trim(),
                        fundador.text.toString().trim(),
                        anio_fundacion.text.toString().trim().toInt(),
                        fecha_creacion.toString().trim(),
                        url_escudo_firebase)

                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Casa creada con exito"
                    )
                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)
                }

            }
        }

        bvolver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
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
    { uri: Uri ->
        if (uri != null) {
            url_escudo = uri
            escudo.setImageURI(uri)
        }
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
}