package com.sdnsoft.medmind

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat
import java.time.Period
import java.util.*
import kotlin.collections.ArrayList

class ScanModel {
    private var items = ArrayList<ScanItem>()

    private val MAX_HISTORY = 5
    private var consolidated = HashMap<String, ArrayList<String>>()
    private var keys = ArrayList<String>()

    private val timeFormat = SimpleDateFormat("hh:mm aa")

    private fun resolveData() {
        consolidated.clear()
        keys.clear()

        var dateFormatter = SimpleDateFormat("dd/MMM/yyyy")

        for(item in items) {
            val strDate = dateFormatter.format(item.date)
            if(!consolidated.keys.contains(strDate))
            {
                consolidated[strDate] = ArrayList()
                keys.add(strDate)
            }

            val t = timeFormat.format(item.date)
            consolidated[strDate]?.add("${item.tag} ($t)")
        }
    }

    fun add(item: ScanItem) : Unit {
        // items.add(item)
        items.add(0, item)

        resolveData()
    }

    fun clear() : Unit {
        items.clear()

        resolveData()
    }

    fun getCount() : Int {
        return consolidated.size
    }

    fun getDate(pos : Int) : String {
        return keys[pos]
    }

    fun getTag(pos: Int) : String {
        return consolidated[keys[pos]]?.joinToString(separator = ", ") { it -> it } ?: ""
    }

    fun save(file : File) : Boolean {
        var gson = Gson()
        var jsonStr = gson.toJson(items)
        Log.d("Test", "Saving items to " + file)

        file.writeText(jsonStr)

        return true
    }

    fun load(file : File) : Boolean {
        if(!file.exists()) {
            Log.d("Test", "File $file does not exist. Creating new")
            items = ArrayList<ScanItem>()
            return true
        }

        val bufferedReader: BufferedReader = file.bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        Log.d("Test", "Data read - $inputString")

        val gson = Gson()
        val itemType = object : TypeToken<ArrayList<ScanItem>>() {}.type

        items = gson.fromJson<ArrayList<ScanItem>>(inputString, itemType)
        items.sortByDescending { it.date }
        // items = gson.fromJson<ArrayList<ScanItem>>(inputString, itemType).sortedByDescending(compareBy({it.date})) as ArrayList<ScanItem>

        resolveData()

        return true
    }
}
