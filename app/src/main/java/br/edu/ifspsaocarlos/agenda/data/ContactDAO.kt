package br.edu.ifspsaocarlos.agenda.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import br.edu.ifspsaocarlos.agenda.model.Contact

val projection = arrayOf(
    SQLiteHelper.KEY_ID,
    SQLiteHelper.KEY_NAME,
    SQLiteHelper.KEY_PHONE,
    SQLiteHelper.KEY_PHONE2,
    SQLiteHelper.KEY_BIRTHDAY,
    SQLiteHelper.KEY_EMAIL
)

class ContactDAO(context: Context) {

    private val dbHelper by lazy {
        SQLiteHelper(context)
    }

    private val database: SQLiteDatabase by lazy {
        dbHelper.writableDatabase
    }

    fun searchAllContacts(): List<Contact> {
        val contacts: MutableList<Contact> = ArrayList()
        val cursor = database.query(
            SQLiteHelper.DATABASE_TABLE,
            projection,
            null,
            null,
            null,
            null,
            SQLiteHelper.KEY_NAME
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
            SQLiteHelper.DATABASE_TABLE,
            projection,
            "${SQLiteHelper.KEY_NAME} like ? or ${SQLiteHelper.KEY_PHONE} = ? or ${SQLiteHelper.KEY_EMAIL} like ?",
            arrayOf("%$nome%", nome, "%$nome%"),
            null,
            null,
            SQLiteHelper.KEY_NAME
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
        database.update(SQLiteHelper.DATABASE_TABLE, updateValues, SQLiteHelper.KEY_ID + "=" + contact.id, null)
    }

    fun createContact(contact: Contact) {
        val values = contact.toContentValues()
        database.insert(SQLiteHelper.DATABASE_TABLE, null, values)
    }

    fun deleteContact(contact: Contact) {
        database.delete(SQLiteHelper.DATABASE_TABLE, SQLiteHelper.KEY_ID + "= ?", arrayOf(contact.id.toString()))
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
        put(SQLiteHelper.KEY_NAME, name)
        put(SQLiteHelper.KEY_PHONE, phone)
        put(SQLiteHelper.KEY_PHONE2, phone2)
        put(SQLiteHelper.KEY_EMAIL, email)
        put(SQLiteHelper.KEY_BIRTHDAY, birthday)
    }
}