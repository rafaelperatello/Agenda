package br.edu.ifspsaocarlos.agenda.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import br.edu.ifspsaocarlos.agenda.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainFab = findViewById<FloatingActionButton>(R.id.fab)
        mainFab.setOnClickListener {
            val i = Intent(applicationContext, DetailActivity::class.java)
            startActivityForResult(i, 0)
        }

        val secondaryFab = findViewById<FloatingActionButton>(R.id.fabProvider)
        secondaryFab.setOnClickListener {
            val i = Intent(applicationContext, ContentProviderActivity::class.java)
            startActivityForResult(i, 0)
        }

        buildListView()
    }

    override fun onRestart() {
        super.onRestart()
        buildListView()
        invalidateOptionsMenu()
    }
}