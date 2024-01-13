package br.edu.ifspsaocarlos.agenda.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import br.edu.ifspsaocarlos.agenda.R
import br.edu.ifspsaocarlos.agenda.data.ContactDAO
import br.edu.ifspsaocarlos.agenda.model.Contact

class DetailActivity : AppCompatActivity() {

    private var contact: Contact? = null

    private val contactDAO: ContactDAO by lazy {
        ContactDAO(this@DetailActivity.applicationContext)
    }

    private val nameText by lazy { findViewById<View>(R.id.editTextName) as EditText }
    private val phoneText by lazy { findViewById<View>(R.id.editTextPhone) as EditText }
    private val phone2Text by lazy { findViewById<View>(R.id.editTextPhone2) as EditText }
    private val emailText by lazy { findViewById<View>(R.id.editTextEmail) as EditText }
    private val birthdayText by lazy { findViewById<View>(R.id.editTextBirthday) as EditText }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if (intent.hasExtra("contact")) {
            contact = intent.getSerializableExtra("contact") as Contact

            contact?.let {
                nameText.setText(it.name)
                phoneText.setText(it.phone)
                phone2Text.setText(it.phone2)
                emailText.setText(it.email)
                birthdayText.setText(it.birthday)

                var pos = it.name?.indexOf(" ") ?: -1
                if (pos == -1) {
                    pos = it.name?.length ?: 0
                }

                title = it.name?.substring(0, pos)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_detalhe, menu)
        if (!intent.hasExtra("contact")) {
            val item = menu.findItem(R.id.delContato)
            item.setVisible(false)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.salvarContato) {
            salvar()
            return true
        } else if (itemId == R.id.delContato) {
            val contact = contact ?: return true
            contactDAO.deleteContact(contact)
            Toast.makeText(applicationContext, "Removido com sucesso", Toast.LENGTH_SHORT).show()
            val resultIntent = Intent()
            setResult(RESULT_OK, resultIntent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun salvar() {
        val name = nameText.text.toString()
        val phone = phoneText.text.toString()
        val phone2 = phone2Text.text.toString()
        val email = emailText.text.toString()
        val birthday = birthdayText.text.toString()

        var newContact = contact
        if (newContact == null) {
            newContact = Contact(
                name = name,
                phone = phone,
                phone2 = phone2,
                birthday = birthday,
                email = email
            )

            contactDAO.createContact(newContact)
            Toast.makeText(this, "Incluído com sucesso", Toast.LENGTH_SHORT).show()
        } else {
            newContact = newContact.copy(
                name = name,
                phone = phone,
                phone2 = phone2,
                birthday = birthday,
                email = email
            )

            contactDAO.updateContact(newContact)
            Toast.makeText(this, "Alterado com sucesso", Toast.LENGTH_SHORT).show()
        }

        val resultIntent = Intent()
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
