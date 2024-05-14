package br.edu.ifspsaocarlos.agenda.activity

import android.content.Intent
import android.os.Bundle
import br.edu.ifspsaocarlos.agenda.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainFab = findViewById<FloatingActionButton>(R.id.fab)
        mainFab.setOnClickListener {
            val i = Intent(applicationContext, DetailActivity::class.java)
            startActivity(i)
        }

        val secondaryFab = findViewById<FloatingActionButton>(R.id.fabProvider)
        secondaryFab.setOnClickListener {
            val i = Intent(applicationContext, ContentProviderActivity::class.java)
            startActivity(i)
        }

        buildListView()
    }

    override fun onRestart() {
        super.onRestart()
        buildListView()
        invalidateOptionsMenu()
    }
}