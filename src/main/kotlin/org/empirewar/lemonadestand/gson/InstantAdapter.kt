package org.empirewar.lemonadestand.gson

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.time.Instant

class InstantAdapter : TypeAdapter<Instant>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Instant) {
        out.value(value.toString())
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): Instant {
        return Instant.parse(`in`.nextString())
    }
}