package br.edu.ifspsaocarlos.agenda.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import br.edu.ifspsaocarlos.agenda.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myFab = findViewById<FloatingActionButton>(R.id.fab)
        myFab.hide()

        val intent = intent
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
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