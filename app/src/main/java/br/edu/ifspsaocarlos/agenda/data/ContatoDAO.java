package br.edu.ifspsaocarlos.agenda.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.agenda.model.Contato;

public class ContatoDAO {
    private Context        context;
    private SQLiteDatabase database;
    private SQLiteHelper   dbHelper;

    public ContatoDAO(Context context) {
        this.context = context;
        this.dbHelper = new SQLiteHelper(context);
    }

    public List<Contato> buscaTodosContatos() {
        database = dbHelper.getReadableDatabase();
        List<Contato> contacts = new ArrayList<Contato>();
        Cursor cursor = database.query(
                SQLiteHelper.DATABASE_TABLE,
                new String[]{
                        SQLiteHelper.KEY_ID,
                        SQLiteHelper.KEY_NAME,
                        SQLiteHelper.KEY_FONE,
                        SQLiteHelper.KEY_EMAIL,
                        SQLiteHelper.KEY_FONE2,
                        SQLiteHelper.KEY_BIRTHDAY },
                null,
                null,
                null,
                null,
                SQLiteHelper.KEY_NAME);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Contato contato = new Contato();

                contato.setId(cursor.getInt(0));
                contato.setNome(cursor.getString(1));
                contato.setFone(cursor.getString(2));
                contato.setEmail(cursor.getString(3));
                contato.setFone2(cursor.getString(4));
                contato.setBirthday(cursor.getString(5));

                contacts.add(contato);
                cursor.moveToNext();
            }
            cursor.close();
        }
        database.close();
        return contacts;
    }

    public List<Contato> buscaContato(String nome) {
        database = dbHelper.getReadableDatabase();
        List<Contato> contacts = new ArrayList<Contato>();

        Cursor cursor = database.query(
                SQLiteHelper.DATABASE_TABLE,
                new String[]{
                        SQLiteHelper.KEY_ID,
                        SQLiteHelper.KEY_NAME,
                        SQLiteHelper.KEY_FONE,
                        SQLiteHelper.KEY_EMAIL,
                        SQLiteHelper.KEY_FONE2,
                        SQLiteHelper.KEY_BIRTHDAY },
                SQLiteHelper.KEY_NAME + " like ? or " + SQLiteHelper.KEY_FONE + " = ? or " + SQLiteHelper.KEY_EMAIL + " like ?",
                new String[]{ "%" + nome + "%", nome, "%" + nome + "%" },
                null,
                null,
                SQLiteHelper.KEY_NAME);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Contato contato = new Contato();
                contato.setId(cursor.getInt(0));
                contato.setNome(cursor.getString(1));
                contato.setFone(cursor.getString(2));
                contato.setEmail(cursor.getString(3));
                contato.setFone2(cursor.getString(4));
                contato.setBirthday(cursor.getString(5));

                contacts.add(contato);
                cursor.moveToNext();
            }
            cursor.close();
        }
        database.close();
        return contacts;
    }

    public void updateContact(Contato c) {
        database = dbHelper.getWritableDatabase();

        ContentValues updateValues = new ContentValues();
        updateValues.put(SQLiteHelper.KEY_NAME, c.getNome());
        updateValues.put(SQLiteHelper.KEY_FONE, c.getFone());
        updateValues.put(SQLiteHelper.KEY_FONE2, c.getFone2());
        updateValues.put(SQLiteHelper.KEY_EMAIL, c.getEmail());
        updateValues.put(SQLiteHelper.KEY_BIRTHDAY, c.getBirthday());

        database.update(SQLiteHelper.DATABASE_TABLE, updateValues, SQLiteHelper.KEY_ID + "=" + c.getId(), null);
        database.close();
    }

    public void createContact(Contato c) {
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.KEY_NAME, c.getNome());
        values.put(SQLiteHelper.KEY_FONE, c.getFone());
        values.put(SQLiteHelper.KEY_FONE2, c.getFone2());
        values.put(SQLiteHelper.KEY_EMAIL, c.getEmail());
        values.put(SQLiteHelper.KEY_BIRTHDAY, c.getBirthday());

        database.insert(SQLiteHelper.DATABASE_TABLE, null, values);
        database.close();
    }

    public void deleteContact(Contato c) {
        database = dbHelper.getWritableDatabase();
        database.delete(SQLiteHelper.DATABASE_TABLE, SQLiteHelper.KEY_ID + "="
                + c.getId(), null);
        database.close();
    }
}