package br.edu.ifspsaocarlos.agenda.di

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import br.edu.ifspsaocarlos.agenda.data.ContactDao
import br.edu.ifspsaocarlos.agenda.data.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SingletonModule {

    companion object {
        @Singleton
        @Provides
        fun provideSQLiteDatabase(
            @ApplicationContext appContext: Context
        ): SQLiteDatabase {
            return Database.getDatabase(appContext)
        }

        @Provides
        fun provideContactDao(
            database: SQLiteDatabase
        ): ContactDao {
            return ContactDao(database)
        }
    }
}