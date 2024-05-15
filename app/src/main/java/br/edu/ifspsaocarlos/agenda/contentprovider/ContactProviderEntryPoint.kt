package br.edu.ifspsaocarlos.agenda.contentprovider

import android.database.sqlite.SQLiteDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContactProviderEntryPoint {
    var database: SQLiteDatabase
}