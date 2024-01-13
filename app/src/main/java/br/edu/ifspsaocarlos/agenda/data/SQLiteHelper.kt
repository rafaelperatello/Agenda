package br.edu.ifspsaocarlos.agenda.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import br.edu.ifspsaocarlos.agenda.data.Database.Companion.DATABASE_NAME
import br.edu.ifspsaocarlos.agenda.data.Database.Companion.DATABASE_VERSION
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.CREATE_CONTACT_TABLE
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_BIRTHDAY
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_PHONE2
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.TABLE_NAME

class SQLiteHelper(context: Context?) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(CREATE_CONTACT_TABLE)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        when (oldVersion) {
            1 -> {
                Log.d("Agenda", "Update da versao 1 para 2")

                val dbUpdate1 = "ALTER TABLE $TABLE_NAME ADD $KEY_PHONE2 TEXT"
                Log.d("Agenda", dbUpdate1)

                database.execSQL(dbUpdate1)

                Log.d("Agenda", "Update da versao 2 para 3")

                val dbUpdate2 = "ALTER TABLE $TABLE_NAME ADD $KEY_BIRTHDAY TEXT"
                Log.d("Agenda", dbUpdate2)

                database.execSQL(dbUpdate2)
            }

            2 -> {
                Log.d("Agenda", "Update da versao 2 para 3")

                val dbUpdate = "ALTER TABLE $TABLE_NAME ADD $KEY_BIRTHDAY TEXT"
                Log.d("Agenda", dbUpdate)

                database.execSQL(dbUpdate)
            }
        }
    }
}