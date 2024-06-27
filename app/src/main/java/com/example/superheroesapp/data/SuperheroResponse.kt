package com.example.superheroesapp.data

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

data class SuperheroResponse(
    @SerializedName("response") val response: String,
    @SerializedName("results-for") val resultsFor: String,
    @SerializedName("results") val results: List<Superhero>
) {
}

data class Superhero (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("powerstats") val stats:Stats,
    @SerializedName("biography") val biography:Biography,
    @SerializedName("image") val image: Image
) {
}

class Biography (
    @SerializedName("full-name") val realName:String,
    @SerializedName("place-of-birth") val placeOfBirth:String,
    @SerializedName("alignment") val alignment:String,
    @SerializedName("publisher") val publisher:String
) { }

data class Stats (
    @JsonAdapter(FloatAdapter::class) @SerializedName("intelligence") val intelligence: Float,
    @JsonAdapter(FloatAdapter::class) @SerializedName("strength") val strength: Float,
    @JsonAdapter(FloatAdapter::class) @SerializedName("speed") val speed: Float,
    @JsonAdapter(FloatAdapter::class) @SerializedName("durability") val durability: Float,
    @JsonAdapter(FloatAdapter::class) @SerializedName("power") val power: Float,
    @JsonAdapter(FloatAdapter::class) @SerializedName("combat") val combat: Float,
) { }

data class Image (
    @SerializedName("url") val url: String
)

class FloatAdapter : TypeAdapter<Float>() {
    override fun write(out: JsonWriter?, value: Float) {
        out?.value(value)
    }

    override fun read(`in`: JsonReader?): Float {
        if (`in` != null) {
            val value: String = `in`.nextString()
            if (value != "null") {
                return value.toFloat()
            }
        }
        return 0f
    }

}