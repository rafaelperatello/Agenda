package br.edu.ifspsaocarlos.agenda.ui.activity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.edu.ifspsaocarlos.agenda.ui.theme.ContentProviderPhonebookTheme
import br.edu.ifspsaocarlos.agenda.contentprovider.ContactProvider.Contacts.CONTENT_URI
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_BIRTHDAY
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_EMAIL
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_ID
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_NAME
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_PHONE
import br.edu.ifspsaocarlos.agenda.data.Database.ContactsTable.KEY_PHONE2

class ContentProviderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ContentProviderPhonebookTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .safeDrawingPadding()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        text = "Content Provider Validator",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }

        //Insert
        val values = ContentValues().apply {
            put(KEY_NAME, "Reader")
            put(KEY_PHONE, "123456")
            put(KEY_PHONE2, "654321")
            put(KEY_BIRTHDAY, "01/03/05")
            put(KEY_EMAIL, "reader@reader.com.br")
        }

        val resultInsert = contentResolver.insert(CONTENT_URI, values) ?: throw Exception("Insert failed")
        Log.d("READER:", "Insert return: $resultInsert")

        queryAll()

        //Update
        val valuesUpdate = ContentValues()
        valuesUpdate.put(KEY_NAME, "João da Silva")

        val id = resultInsert.lastPathSegment
        id?.let {
            val resultUpdate = contentResolver.update(CONTENT_URI, valuesUpdate, "$KEY_ID=$id", null)
            Log.d("READER:", "Update result: $resultUpdate")
            queryAll()
        } ?: run {
            Log.d("READER:", "Update failed $resultInsert")
            queryAll()
        }

        //Delete
        val resultDelete = contentResolver.delete(resultInsert, null, null)
        Log.d("READER:", "Delete last insert: $resultDelete")
    }

    private fun queryAll() {
        //Query all
        val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
        cursor.use {
            while (cursor?.moveToNext() == true) {
                Log.d(
                    "READER:",
                    cursor.getString(0) + " " +
                            cursor.getString(1) + " " +
                            cursor.getString(2) + " " +
                            cursor.getString(3) + " " +
                            cursor.getString(4) + " " +
                            cursor.getString(5)
                )
            }
        }
    }
}