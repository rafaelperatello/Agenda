package br.edu.ifspsaocarlos.agenda.contentprovider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_ID
import dagger.hilt.android.EntryPointAccessors

class ContactProvider : ContentProvider() {

    private lateinit var database: SQLiteDatabase

    override fun onCreate(): Boolean {
        context?.applicationContext?.let {
            val entryPoint = EntryPointAccessors.fromApplication(it, ContactProviderEntryPoint::class.java)
            database = entryPoint.database
        }

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = when (sURIMatcher.match(uri)) {
            CONTACTS -> database.query(
                ContactsTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
            )

            CONTACTS_ID -> database.query(
                ContactsTable.TABLE_NAME,
                projection,
                "$KEY_ID = ${uri.lastPathSegment}",
                null,
                null,
                null,
                sortOrder
            )

            else -> throw IllegalArgumentException("Unknown URI")
        }
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (sURIMatcher.match(uri)) {
            CONTACTS -> Contacts.CONTENT_TYPE

            CONTACTS_ID -> Contacts.CONTENT_ITEM_TYPE

            else -> throw IllegalArgumentException("Unknown URI")
        }
    }

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri {
        val uriType = sURIMatcher.match(uri)
        val id: Long

        when (uriType) {
            CONTACTS -> id = database.insert(
                ContactsTable.TABLE_NAME,
                null,
                values
            )

            else -> throw IllegalArgumentException("Unknown URI")

        }
        return ContentUris.withAppendedId(uri, id)
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val uriType = sURIMatcher.match(uri)

        val count = when (uriType) {
            CONTACTS -> database.delete(
                ContactsTable.TABLE_NAME,
                selection,
                selectionArgs
            )

            CONTACTS_ID -> database.delete(
                ContactsTable.TABLE_NAME,
                "$KEY_ID = ${uri.pathSegments[1]}",
                null
            )

            else -> throw IllegalArgumentException("Unknown URI")

        }
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val uriType = sURIMatcher.match(uri)

        val count = when (uriType) {
            CONTACTS -> database.update(
                ContactsTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )

            CONTACTS_ID -> database.update(
                ContactsTable.TABLE_NAME,
                values,
                "$KEY_ID = ${uri.pathSegments[1]}",
                null
            )

            else -> throw IllegalArgumentException("Unknown URI")
        }
        return count
    }

    object Contacts {

        const val AUTHORITY: String = "br.edu.ifspsaocarlos.agenda.provider"

        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/contacts")

        const val CONTENT_TYPE: String = "vnd.android.cursor.dir/vnd.br.edu.ifspsaocarlos.agenda.contacts"
        const val CONTENT_ITEM_TYPE: String = "vnd.android.cursor.item/vnd.br.edu.ifspsaocarlos.agenda.contacts"
    }

    companion object {

        const val CONTACTS: Int = 1
        const val CONTACTS_ID: Int = 2

        private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            with(sURIMatcher) {
                addURI(Contacts.AUTHORITY, "contacts", CONTACTS)
                addURI(Contacts.AUTHORITY, "contacts/#", CONTACTS_ID)
            }
        }
    }
}