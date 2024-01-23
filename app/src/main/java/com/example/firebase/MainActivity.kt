package com.example.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private lateinit var crear_casa: Button
    private lateinit var ver_casa: Button

    private lateinit var androidId: String
    private lateinit var db_ref: DatabaseReference
    private lateinit var generador: AtomicInteger
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

        crearCanalNotificaciones()
        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        db_ref = FirebaseDatabase.getInstance().reference
        generador = AtomicInteger(0)

        //CONTROLADOR NOTIFICACIONES
        db_ref.child("howarts").child("casas")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_casa = snapshot.getValue(Pojo_casa::class.java)
                    if (!pojo_casa!!.user_notificacion.equals(androidId) && pojo_casa!!.estado_notificacion == Estado.CREADO) {
                        db_ref.child("howarts").child("casas").child(pojo_casa.id!!)
                            .child("estado_notificacion").setValue(Estado.NOTIFICADO)
                        generarNotificacion(generador.incrementAndGet(), pojo_casa,
                            "Se ha creado una nueva casa " + pojo_casa.nombre,
                            "Nuevos datos en la app",
                            VerCasas::class.java
                        )
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_casa = snapshot.getValue(Pojo_casa::class.java)
                    if (!pojo_casa!!.user_notificacion.equals(androidId) && pojo_casa!!.estado_notificacion == Estado.MODIFICADO) {
                        db_ref.child("howarts").child("casas").child(pojo_casa.id!!)
                            .child("estado_notificado").setValue(Estado.NOTIFICADO)
                        generarNotificacion(generador.incrementAndGet(), pojo_casa,
                            "Se ha editado la casa " + pojo_casa.nombre,
                            "Datos modificados en la app",
                            EditarCasa::class.java
                        )
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val pojo_casa = snapshot.getValue(Pojo_casa::class.java)
                    if (!pojo_casa!!.user_notificacion.equals(androidId)){
                        generarNotificacion(generador.incrementAndGet(), pojo_casa,
                            "Se ha eliminado la casa " + pojo_casa.nombre,
                            "Datos eliminados en la app",
                            VerCasas::class.java
                        )
                    }

                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun generarNotificacion(
        id_notificacion: Int,
        pojo: Parcelable,
        contenido: String,
        titulo: String,
        destino: Class<*>
    ) {
        val id = "Canal de prueba"
        val actividad = Intent(applicationContext, destino)
        actividad.putExtra("casas", pojo)

        val pendingIntent =
            PendingIntent.getActivity(this, 0, actividad, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificacion = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.notification_icon_124899)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("sistema de informacion")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        with(NotificationManagerCompat.from(this)) {
            notify(id_notificacion, notificacion)
        }

    }


    private fun crearCanalNotificaciones() {
        val nombre = "canal_basico"
        val id = "Canal de prueba"
        val descripcion = "Notificacion basica"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(id, nombre, importancia).apply {
            description = descripcion
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }

    override fun onBackPressed() {
        finish()
        val intent: Intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}