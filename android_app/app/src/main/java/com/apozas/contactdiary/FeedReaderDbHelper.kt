package com.apozas.contactdiary

/*
    This file is part of Contact Diary.
    Contact Diary is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    Contact Diary is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with Contact Diary. If not, see <http://www.gnu.org/licenses/>.
    Copyright 2020 by Alex Pozas-Kerstjens (apozas)
*/

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ContactDatabase.SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) { db.execSQL(ContactDatabase.SQL_UPDATE_2) }
        if (oldVersion < 3) {
            db.execSQL(ContactDatabase.SQL_UPDATE_3)
            MigrationTools().migrateTo3(db)
        }
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "ContactDiary.db"
    }

    fun viewData(onlyRisky: Boolean): Cursor {
        val db = this.readableDatabase

        val query = if (onlyRisky) {
            "Select * from " + ContactDatabase.ContactDatabase.FeedEntry.TABLE_NAME +
                    " WHERE " + ContactDatabase.ContactDatabase.FeedEntry.CLOSECONTACT_COLUMN + ">1" +
                    " ORDER BY " + ContactDatabase.ContactDatabase.FeedEntry.TIMESTAMP_COLUMN + " DESC," +
                    " " + ContactDatabase.ContactDatabase.FeedEntry.NAME_COLUMN + " ASC"
        } else {
            "Select * from " + ContactDatabase.ContactDatabase.FeedEntry.TABLE_NAME +
                    " ORDER BY " + ContactDatabase.ContactDatabase.FeedEntry.TIMESTAMP_COLUMN + " DESC," +
                    " " + ContactDatabase.ContactDatabase.FeedEntry.NAME_COLUMN + " ASC"
        }

        return db.rawQuery(query, null)
    }

}
