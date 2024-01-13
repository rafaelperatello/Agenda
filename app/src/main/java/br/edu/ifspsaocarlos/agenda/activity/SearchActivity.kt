package br.edu.ifspsaocarlos.agenda.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import br.edu.ifspsaocarlos.agenda.R

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myFab = findViewById<FloatingActionButton>(R.id.fab)
        myFab.hide()

        val intent = intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) ?: ""
            buildSearchListView(query)
        }
    }
}