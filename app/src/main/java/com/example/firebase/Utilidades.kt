package com.example.firebase

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class Utilidades {
    companion object{

        fun existeCasa(casas : List<Pojo_casa>, nombre:String):Boolean{
            return casas.any{ it.nombre!!.lowercase()==nombre.lowercase()}
        }


        fun obtenerListaCasas(database_ref: DatabaseReference):MutableList<Pojo_casa>{
            var lista = mutableListOf<Pojo_casa>()

            database_ref.child("howarts")
                .child("casas")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{hijo : DataSnapshot ->
                            val pojo_casa = hijo.getValue(Pojo_casa::class.java)
                            lista.add(pojo_casa!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })

            return lista
        }

        fun escribirCasa(db_ref: DatabaseReference, id: String, nombre:String, fundador:String, anio_fundacion:Int, fecha:String, url_firebase:String, puntos:Int, estado_notificacion: Int, user_notificacion: String)=
            db_ref.child("howarts").child("casas").child(id).setValue(Pojo_casa(
                id,
                nombre,
                fundador,
                anio_fundacion,
                fecha,
                url_firebase,
                puntos,
                estado_notificacion,
                user_notificacion
            ))

        suspend fun guardarEscudo(sto_ref: StorageReference, id:String, imagen: Uri):String{
            lateinit var url_escudo_firebase: Uri

            url_escudo_firebase=sto_ref.child("howarts").child("casas").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_escudo_firebase.toString()
        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }


        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.hogwartscrest)
                .error(R.drawable.kisspng_http_404_computer_icons_clip_art_5afe161a542093_5617268715266012423446)
            return options
        }
    }
}