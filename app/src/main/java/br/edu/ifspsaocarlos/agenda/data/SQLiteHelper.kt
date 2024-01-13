package br.edu.ifspsaocarlos.agenda.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLiteHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                Log.d("Agenda", "Update da versao 1 para 2")

                val dbUpdate1 = "ALTER TABLE $DATABASE_TABLE ADD $KEY_PHONE2 TEXT"
                Log.d("Agenda", dbUpdate1)

                database.execSQL(dbUpdate1)

                Log.d("Agenda", "Update da versao 2 para 3")

                val dbUpdate2 = "ALTER TABLE $DATABASE_TABLE ADD $KEY_BIRTHDAY TEXT"
                Log.d("Agenda", dbUpdate2)

                database.execSQL(dbUpdate2)
            }

            2 -> {
                Log.d("Agenda", "Update da versao 2 para 3")

                val dbUpdate = "ALTER TABLE $DATABASE_TABLE ADD $KEY_BIRTHDAY TEXT"
                Log.d("Agenda", dbUpdate)

                database.execSQL(dbUpdate)
            }
        }
    }

    companion object {

        const val DATABASE_NAME: String = "agenda.db"
        const val DATABASE_VERSION: Int = 3

        const val DATABASE_TABLE: String = "contatos"

        const val KEY_ID: String = "id"
        const val KEY_NAME: String = "nome"
        const val KEY_PHONE: String = "phone"
        const val KEY_PHONE2: String = "phone2"
        const val KEY_BIRTHDAY: String = "birthday"
        const val KEY_EMAIL: String = "email"

        const val DATABASE_CREATE: String =
            """CREATE TABLE $DATABASE_TABLE 
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