package org.unizd.rma.peric.pokedex

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvType: TextView
    private lateinit var tvAbility1: TextView
    private lateinit var tvAbility2: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvDescription: TextView

    private lateinit var ivIcon: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val selectedPokemon = intent.getStringExtra("selectedPokemon")
        if (selectedPokemon != null) {
            getDataFromApi(selectedPokemon)
        }

        tvName = findViewById(R.id.tvName)
        tvType = findViewById(R.id.tvType)
        tvAbility1 = findViewById(R.id.tvAbility1)
        tvAbility2 = findViewById(R.id.tvAbility2)
        tvHeight = findViewById(R.id.tvHeight)
        tvWeight = findViewById(R.id.tvWeight)
        tvDescription = findViewById(R.id.tvDescription)

        ivIcon = findViewById(R.id.ivIcon)

        if (selectedPokemon != null) {
            tvName.text = selectedPokemon.uppercase()
        }

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

    }

    private fun getDataFromApi(pokemon: String) {
        val apiUrl = "https://pokeapi.co/api/v2/pokemon/$pokemon"

        Log.d("MyApp", "API URL: $apiUrl")

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()

                    Log.d("MyApp", "Response: $response")

                    val jsonData = JSONObject(response)
                    val id = jsonData.getInt("id")
                    val typeArr = jsonData.getJSONArray("types")
                    val type = if (typeArr.length() > 0) {
                        typeArr.getJSONObject(0).getJSONObject("type").getString("name")
                    } else {
                        "N/A"
                    }

                    val abilityArr = jsonData.getJSONArray("abilities")
                    val ability1 = if (abilityArr.length() > 0) {
                        abilityArr.getJSONObject(0).getJSONObject("ability").getString("name")
                    } else {
                        "N/A"
                    }

                    val ability2 = if (abilityArr.length() > 1) {
                        abilityArr.getJSONObject(1).getJSONObject("ability").getString("name")
                    } else {
                        "N/A"
                    }

                    val height = jsonData.getInt("height")
                    val weight = jsonData.getInt("weight")

                    val speciesUrl = "https://pokeapi.co/api/v2/pokemon-species/$id"
                    getDescriptionDataFromApi(speciesUrl, id, type, ability1, ability2, height, weight)
                } else {
                    Log.e("MyApp", "HTTP response code: $responseCode")
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun getDescriptionDataFromApi(speciesUrl: String, id: Int, type: String, ability1: String, ability2: String, height: Int, weight: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(speciesUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()

                    val jsonData = JSONObject(response)
                    val flavorTextEntries = jsonData.getJSONArray("flavor_text_entries")
                    val description = flavorTextEntries.getJSONObject(0).getString("flavor_text")

                    Log.e("MyApp", "Desc:$description")

                    if (description.contains("\\s|\\n|\\t|\\f".toRegex())) {
                        description.replace("\n\\s*\n".toRegex(), "")
                        Log.e("MyApp", "Cleaned string:$description")
                    }



                    launch(Dispatchers.Main) {
                        tvType.text = type
                        tvAbility1.text = ability1
                        tvAbility2.text = ability2
                        tvHeight.text = height.toString()
                        tvWeight.text = weight.toString()
                        tvDescription.text = description

                        val iconUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
                        loadImage(iconUrl)
                    }
                } else {
                    throw IOException("HTTP response code: $responseCode")
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadImage(imageUrl: String){

        GlobalScope.launch ( Dispatchers.IO ) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                launch(Dispatchers.Main) {
                    ivIcon.setImageBitmap(bitmap)

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }


    }


}