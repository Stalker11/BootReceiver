package com.example.boot_event_receiver

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.boot_event_receiver.models.BootEvent
//TODO Should be migrated to Room or other DB frameworks...
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "boot_events.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "boot_events"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TIMESTAMP INTEGER)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

    fun getAllBootEvents(): List<BootEvent> {
        val bootEvents = mutableListOf<BootEvent>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"

        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID) ?:0)
                val timestamp = cursor.getLong(cursor.getColumnIndex(COLUMN_TIMESTAMP) ?:0)
                val bootEvent = BootEvent(id, timestamp)
                bootEvents.add(bootEvent)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return bootEvents
    }

    fun insertBootEvent(timestamp: Long): Long {
        val db = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(COLUMN_TIMESTAMP, timestamp)

        val id = db.insert(TABLE_NAME, null, contentValues)

        db.close()

        return id
    }
}