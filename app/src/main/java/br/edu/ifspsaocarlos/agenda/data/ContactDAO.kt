package br.edu.ifspsaocarlos.agenda.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable
import br.edu.ifspsaocarlos.agenda.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    @Deprecated("Use suspend functions instead")
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

    @Deprecated("Use suspend functions instead")
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

    @Deprecated("Use suspend functions instead")
    fun updateContact(contact: Contact) {
        val updateValues = contact.toContentValues()
        database.update(
            ContactsTable.TABLE_NAME,
            updateValues,
            "${ContactsTable.KEY_ID} = ${contact.id}",
            null
        )
    }

    @Deprecated("Use suspend functions instead")
    fun createContact(contact: Contact) {
        val values = contact.toContentValues()
        database.insert(
            ContactsTable.TABLE_NAME,
            null,
            values
        )
    }

    @Deprecated("Use suspend functions instead")
    fun deleteContact(contact: Contact) {
        database.delete(
            ContactsTable.TABLE_NAME,
            "${ContactsTable.KEY_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    // create suspend functions for the DAO
    suspend fun searchAllContactsSuspend(): List<Contact> {
        return withContext(Dispatchers.IO) {
            searchAllContacts()
        }
    }

    suspend fun searchContactSuspend(nome: String): List<Contact> {
        return withContext(Dispatchers.IO) {
            searchContact(nome)
        }
    }

    suspend fun updateContactSuspend(contact: Contact) {
        withContext(Dispatchers.IO) {
            updateContact(contact)
        }
    }

    suspend fun createContactSuspend(contact: Contact) {
        withContext(Dispatchers.IO) {
            createContact(contact)
        }
    }

    suspend fun deleteContactSuspend(contact: Contact) {
        withContext(Dispatchers.IO) {
            deleteContact(contact)
        }
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