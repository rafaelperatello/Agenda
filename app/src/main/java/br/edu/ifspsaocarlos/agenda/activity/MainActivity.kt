package br.edu.ifspsaocarlos.agenda.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import br.edu.ifspsaocarlos.agenda.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val myFab = findViewById<View>(R.id.fab) as FloatingActionButton
        myFab.setOnClickListener {
            val i = Intent(applicationContext, DetailActivity::class.java)
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