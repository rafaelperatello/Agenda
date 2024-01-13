package br.edu.ifspsaocarlos.agenda.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class Database {

    companion object {

        private var databaseSingleton: SQLiteDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): SQLiteDatabase {
            var database = databaseSingleton

            return if (database == null) {
                database = SQLiteHelper(context).writableDatabase
                databaseSingleton = database
                database
            } else {
                database
            }
        }

        const val DATABASE_NAME: String = "agenda.db"
        const val DATABASE_VERSION: Int = 3
    }

    object ContactsTable {

        const val TABLE_NAME: String = "contacts"

        const val KEY_ID: String = "id"
        const val KEY_NAME: String = "name"
        const val KEY_PHONE: String = "phone"
        const val KEY_PHONE2: String = "phone2"
        const val KEY_BIRTHDAY: String = "birthday"
        const val KEY_EMAIL: String = "email"

        const val CREATE_CONTACT_TABLE: String =
            """CREATE TABLE $TABLE_NAME 
                (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $KEY_NAME TEXT NOT NULL, 
                $KEY_PHONE TEXT, 
                $KEY_PHONE2 TEXT, 
                $KEY_BIRTHDAY TEXT, 
                $KEY_EMAIL TEXT
                );
            """
    }
}