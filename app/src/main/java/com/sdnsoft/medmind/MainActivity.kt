package com.sdnsoft.medmind

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sdnsoft.medmind.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var model = ScanModel()
    var qrUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadModel()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.fabShare.setOnClickListener {
            if(qrUri == null)
                return@setOnClickListener

            shareImageUri(qrUri!!)
        }

        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data

        if(appLinkData != null) {
            val tag = appLinkData.getQueryParameter("id")
            if (tag != null) {
                Log.d("Test", tag)
                model.add(ScanItem(tag, Date()))
            }
        }
    }

    override fun onStart() {
        super.onStart()

        loadModel()
    }

    override fun onResume() {
        super.onResume()

        loadModel()
    }

    override fun onPause() {
        super.onPause()
        saveModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_create_qr -> {
                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_FirstFragment_to_createQRFragment)
                true
            }
            R.id.action_clear_all_data -> {
                model.clear()
                binding.root.invalidate()

                Snackbar.make(binding.fab.rootView, "All data cleared", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun enableDisableFabButtons(mode: String) : Unit {
        when (mode) {
            "fragment_main" -> {
                binding.fab.visibility = FloatingActionButton.VISIBLE
                binding.fabShare.visibility = FloatingActionButton.INVISIBLE
            }

            "fragment_create_qr" -> {
                binding.fab.visibility = FloatingActionButton.INVISIBLE
                binding.fabShare.visibility = FloatingActionButton.VISIBLE
            }

            else -> {
                binding.fab.visibility = FloatingActionButton.INVISIBLE
                binding.fabShare.visibility = FloatingActionButton.INVISIBLE
            }
        }
    }

    private fun saveModel() : Unit {
        val file = File(filesDir,"medmind.dat")
        model.save(file)
    }

    private fun loadModel() : Unit {
        Log.d("Test", "Loading model...")

        var file = File(filesDir,"medmind.dat")
        model.load(file)

        val sz = model.getCount()
        Log.d("Test", "Model size is ${sz}")
    }

    private fun shareImageUri(uri : Uri) : Unit {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }

}