package br.edu.ifspsaocarlos.agenda.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable
import br.edu.ifspsaocarlos.agenda.model.Contact

private val projection = arrayOf(
    ContactsTable.KEY_ID,
    ContactsTable.KEY_NAME,
    ContactsTable.KEY_PHONE,
    ContactsTable.KEY_PHONE2,
    ContactsTable.KEY_BIRTHDAY,
    ContactsTable.KEY_EMAIL
)

class ContactDAO(context: Context) {

    private val database: SQLiteDatabase by lazy {
        Database.getDatabase(context)
    }

    fun searchAllContacts(): List<Contact> {
        val contacts: MutableList<Contact> = ArrayList()
        val cursor = database.query(
            ContactsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            ContactsTable.KEY_NAME
        )

        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                contacts.add(cursor.getContact())
                cursor.moveToNext()
            }
            cursor.close()
        }
        return contacts
    }

    fun searchContact(nome: String): List<Contact> {
        val contacts: MutableList<Contact> = ArrayList()

        val cursor = database.query(
            ContactsTable.TABLE_NAME,
            projection,
            "${ContactsTable.KEY_NAME} like ? or ${ContactsTable.KEY_PHONE} = ? or ${ContactsTable.KEY_EMAIL} like ?",
            arrayOf("%$nome%", nome, "%$nome%"),
            null,
            null,
            ContactsTable.KEY_NAME
        )

        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                contacts.add(cursor.getContact())
                cursor.moveToNext()
            }
            cursor.close()
        }
        return contacts
    }

    fun updateContact(contact: Contact) {
        val updateValues = contact.toContentValues()
        database.update(
            ContactsTable.TABLE_NAME,
            updateValues,
            "${ContactsTable.KEY_ID} = ${contact.id}",
            null
        )
    }

    fun createContact(contact: Contact) {
        val values = contact.toContentValues()
        database.insert(
            ContactsTable.TABLE_NAME,
            null,
            values
        )
    }

    fun deleteContact(contact: Contact) {
        database.delete(
            ContactsTable.TABLE_NAME,
            "${ContactsTable.KEY_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }
}

private fun Cursor.getContact(): Contact {
    return Contact(
        id = getInt(0).toLong(),
        name = getString(1),
        phone = getString(2),
        phone2 = getString(3),
        birthday = getString(4),
        email = getString(5)
    )
}

private fun Contact.toContentValues(): ContentValues {
    return ContentValues().apply {
        put(ContactsTable.KEY_NAME, name)
        put(ContactsTable.KEY_PHONE, phone)
        put(ContactsTable.KEY_PHONE2, phone2)
        put(ContactsTable.KEY_EMAIL, email)
        put(ContactsTable.KEY_BIRTHDAY, birthday)
    }
}