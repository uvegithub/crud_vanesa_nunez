package com.example.firebase

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerCasas : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var volver: Button
    private lateinit var recycler: RecyclerView
    private  lateinit var lista:MutableList<Pojo_casa>
    private lateinit var adaptador: CasaAdaptador
    private lateinit var db_ref: DatabaseReference

    private lateinit var spinner: Spinner
    //private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_casas)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        spinner= findViewById(R.id.filtro)

        val items = resources.getStringArray(R.array.spinner_items)
//
//        if (spinner!=null) {
//            val adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.spinner_items,
//                android.R.layout.simple_spinner_item
//            )
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
//
//            spinner.adapter = adapter
//        }

        var estrellas = 0

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        estrellas = sharedPreferences.getString("puntos", "5")!!.toInt()

        volver = findViewById(R.id.volver_inicio)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()

        db_ref.child("howarts")
            .child("casas")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{hijo: DataSnapshot?
                        ->
                        val pojo_casa = hijo?.getValue(Pojo_casa::class.java)
                        lista.add(pojo_casa!!)
                    }
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }

            })

        adaptador = CasaAdaptador(lista)
        recycler = findViewById(R.id.lista_casas)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)

        volver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, id: Long) {
                val item_seleccionado = items[posicion]
                if(item_seleccionado == "Por nombre"){

                }else if(item_seleccionado == "Por puntuacion"){
                    lista.sortBy{ it.puntuacion }
                }

                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }




    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val item = menu?.findItem(R.id.search)
        val searhView = item?.actionView as SearchView


        searhView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter((newText))
                return true
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return  true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                adaptador.filter.filter("")
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }
}