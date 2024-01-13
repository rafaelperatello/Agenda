package br.edu.ifspsaocarlos.agenda.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import br.edu.ifspsaocarlos.agenda.data.SQLiteHelper;

/**
 * Created by rafae on 14/12/2015.
 */
public class ContactProvider extends ContentProvider {
    public static final  int        CONTATOS    = 1;
    public static final  int        CONTATOS_ID = 2;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(Contatos.AUTHORITY, "contatos", CONTATOS);
        sURIMatcher.addURI(Contatos.AUTHORITY, "contatos/#", CONTATOS_ID);
    }

    private SQLiteDatabase database;
    private SQLiteHelper   dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new SQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        database = dbHelper.getWritableDatabase();

        Cursor cursor;

        switch (sURIMatcher.match(uri)) {
            case CONTATOS:
                cursor = database.query(SQLiteHelper.DATABASE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CONTATOS_ID:
                cursor = database.query(SQLiteHelper.DATABASE_TABLE, projection, Contatos.KEY_ID + "=" + uri.getLastPathSegment(), null,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (sURIMatcher.match(uri)) {
            case CONTATOS:
                return Contatos.CONTENT_TYPE;

            case CONTATOS_ID:
                return Contatos.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("URI desconhecida");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        database = dbHelper.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        long id;

        switch (uriType) {
            case CONTATOS:
                id = database.insert(SQLiteHelper.DATABASE_TABLE, null, values);
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");

        }

        uri = ContentUris.withAppendedId(uri, id);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        database = dbHelper.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        int count;

        switch (uriType) {
            case CONTATOS:
                count = database.delete(SQLiteHelper.DATABASE_TABLE, selection, selectionArgs);
                break;

            case CONTATOS_ID:
                count = database.delete(SQLiteHelper.DATABASE_TABLE, Contatos.KEY_ID + "=" + uri.getPathSegments().get(1), null);
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");

        }
        database.close();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        database = dbHelper.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        int count;

        switch (uriType) {
            case CONTATOS:
                count = database.update(SQLiteHelper.DATABASE_TABLE, values, selection, selectionArgs);
                break;

            case CONTATOS_ID:
                count = database.update(SQLiteHelper.DATABASE_TABLE, values, Contatos.KEY_ID + "=" + uri.getPathSegments().get(1), null);
                break;

            default:
                throw new IllegalArgumentException("URI desconhecida");
        }
        database.close();
        return count;
    }

    public static final class Contatos {

        public static final String AUTHORITY = "br.edu.ifspsaocarlos.agenda.provider";


        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/contatos");

        public static final String CONTENT_TYPE      = "vnd.android.cursor.dir/vnd.br.edu.ifspsaocarlos.agenda.contatos";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.br.edu.ifspsaocarlos.agenda.contatos";

        public static final String KEY_ID       = "id";
        public static final String KEY_NOME     = "nome";
        public static final String KEY_TELEFONE = "fone";
    }
}
