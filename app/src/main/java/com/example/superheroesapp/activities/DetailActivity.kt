package com.example.superheroesapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.superheroesapp.R
import com.example.superheroesapp.data.Superhero
import com.example.superheroesapp.data.SuperheroApiService
import com.example.superheroesapp.databinding.ActivityDetailBinding
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding

    lateinit var superhero: Superhero

    private var isShowingCardFront = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val id = intent.getIntExtra("SUPERHERO_ID", -1)

        getById(id)

        binding.cardView.setOnClickListener {
            rotateCard()
        }
    }

    private fun loadData() {
        binding.nameTextView.text = superhero.name
        Picasso.get().load(superhero.image.url).into(binding.photoImageView)

        binding.realNameTextView.text = superhero.biography.realName
        binding.placeOfBirthTextView.text = superhero.biography.placeOfBirth
        binding.publisherTextView.text = superhero.biography.publisher

        binding.alignmentTextView.text = superhero.biography.alignment.uppercase()
        val alignmentColor = if (superhero.biography.alignment == "good") {
            R.color.good_color
        } else {
            R.color.evil_color
        }
        binding.alignmentTextView.setTextColor(getColor(alignmentColor))

        setupRadarChart();
    }

    private fun rotateCard() {
        binding.cardView.animate()
            .rotationY(90f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (isShowingCardFront) {
                        binding.cardFrontLayout.visibility = View.GONE
                        binding.cardBackLayout.visibility = View.VISIBLE
                    } else {
                        binding.cardBackLayout.visibility = View.GONE
                        binding.cardFrontLayout.visibility = View.VISIBLE
                    }
                    isShowingCardFront = !isShowingCardFront
                    binding.cardView.rotationY = -90f
                    binding.cardView.animate()
                        .rotationY(0f)
                        .setDuration(300)
                        .setInterpolator(DecelerateInterpolator())
                        .setListener(null)
                }
            })
    }

    private fun setupRadarChart() {
        // Datos de ejemplo
        val entries = ArrayList<RadarEntry>()
        entries.add(RadarEntry(superhero.stats.intelligence.toFloat()))
        entries.add(RadarEntry(superhero.stats.strength.toFloat()))
        entries.add(RadarEntry(superhero.stats.speed.toFloat()))
        entries.add(RadarEntry(superhero.stats.durability.toFloat()))
        entries.add(RadarEntry(superhero.stats.power.toFloat()))
        entries.add(RadarEntry(superhero.stats.combat.toFloat()))

        val dataSet = RadarDataSet(entries, "Estadísticas")
        dataSet.setDrawFilled(true)
        dataSet.color = getColor(R.color.chart_line)
        dataSet.fillColor = getColor(R.color.chart_fill)
        dataSet.fillAlpha = 180
        dataSet.lineWidth = 2f
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.BLACK

        val data = RadarData(dataSet)
        binding.statsRadarChart.data = data

        // Etiquetas del eje X
        val labels = arrayOf("Intelligence", "Strength", "Speed", "Durability", "Power", "Combat")
        val xAxis = binding.statsRadarChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textSize = 14f

        // Configuración del eje Y
        val yAxis = binding.statsRadarChart.yAxis
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 100f
        yAxis.textSize = 14f

        binding.statsRadarChart.description.isEnabled = false
        binding.statsRadarChart.legend.isEnabled = false
        binding.statsRadarChart.setTouchEnabled(false)
        binding.statsRadarChart.invalidate() // refrescar la gráfica
    }

    private fun getById(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = getRetrofit().create(SuperheroApiService::class.java)
                val result = apiService.getSuperheroById(id)

                superhero = result

                runOnUiThread {
                    loadData()
                }
                //Log.i("HTTP", "${result.results}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://superheroapi.com/api/7252591128153666/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}