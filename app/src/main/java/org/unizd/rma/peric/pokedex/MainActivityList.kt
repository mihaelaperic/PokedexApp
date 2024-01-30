package org.unizd.rma.peric.pokedex

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivityList : AppCompatActivity(), PokemonAdapter.OnItemClickListener {

    private val pokeList: MutableList<String> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)

        GlobalScope.launch(Dispatchers.IO) {
            for (i in 1..50) {
                try {
                    val pokemonData = getPokemonData(i)
                    pokeList.add(pokemonData)
                } catch (e: Exception) {
                    throw IOException("Error adding data", e)
                }
            }

            launch(Dispatchers.Main) {
                val rvList: RecyclerView = findViewById(R.id.pokemonRecyclerView)
                rvList.layoutManager = LinearLayoutManager(this@MainActivityList)
                val pokeAdapter = PokemonAdapter(pokeList, this@MainActivityList)
                rvList.adapter = pokeAdapter
            }
        }
    }

    override fun onItemClick(pokemon: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedPokemon", pokemon)
        startActivity(intent)
    }

    private suspend fun getPokemonData(pokemonId: Int): String {
        val apiUrl = "https://pokeapi.co/api/v2/pokemon/$pokemonId"

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()

                    val jsonData = JSONObject(response)
                    return@withContext jsonData.getString("name")
                } else {
                    throw IOException("HTTP response code: $responseCode")
                }
            } catch (e: Exception) {
                throw IOException("Error fetching data", e)

            }

        }
    }

}
