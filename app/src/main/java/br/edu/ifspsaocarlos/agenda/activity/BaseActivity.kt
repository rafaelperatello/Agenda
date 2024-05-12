package br.edu.ifspsaocarlos.agenda.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import br.edu.ifspsaocarlos.agenda.R
import br.edu.ifspsaocarlos.agenda.adapter.ContactArrayAdapter
import br.edu.ifspsaocarlos.agenda.data.ContactDAO
import br.edu.ifspsaocarlos.agenda.model.Contact

open class BaseActivity : AppCompatActivity() {

    private val list by lazy { findViewById<ListView>(R.id.listView) }
    private var adapter: ContactArrayAdapter? = null

    private val contactDAO: ContactDAO by lazy { ContactDAO(this@BaseActivity.applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        list.onItemClickListener = OnItemClickListener { adapterView, _, arg2, _ ->
            val contact = adapterView.adapter.getItem(arg2) as Contact
            val intent = Intent(applicationContext, DetailActivity::class.java)
            intent.putExtra("contact", contact)
            startActivityForResult(intent, 0)
        }

        registerForContextMenu(list)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.searchContact).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true)

        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu, v: View,
        menuInfo: ContextMenuInfo
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_context, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo? ?: return false
        val adapter = list.adapter as ContactArrayAdapter
        val contact = adapter.getItem(info.position)

        if (item.itemId == R.id.delete_item) {
            contactDAO.deleteContact(contact!!)
            Toast.makeText(applicationContext, "Removido com sucesso", Toast.LENGTH_SHORT).show()
            buildListView()
            return true
        }
        return super.onContextItemSelected(item)
    }

    protected fun buildListView() {
        val values = contactDAO.searchAllContacts()
        adapter = ContactArrayAdapter(this, values)
        list.adapter = adapter
    }

    protected fun buildSearchListView(query: String) {
        val values = contactDAO.searchContact(query)
        adapter = ContactArrayAdapter(this, values)
        list.adapter = adapter
    }
}
