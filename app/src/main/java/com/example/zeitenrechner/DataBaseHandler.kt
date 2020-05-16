package com.example.zeitenrechner

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

private val DATABASE_NAME = "DataTimes"
private val TABLE_NAME = "Times"
private val COL_ID = "id"
private val COL_NAME = "name"
private val COL_TIME = "time"
private val COL_DISTANCE = "distance"
private val COL_PACE_M = "paceM"
private val COL_PACE_H = "paceH"

class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,
                                                    null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE '${TABLE_NAME}' ( '${COL_ID}' " +
                          "INTEGER PRIMARY KEY AUTOINCREMENT, '${COL_NAME}' VARCHAR(50), '${COL_TIME}' VARCHAR(8), " +
                          "'${COL_DISTANCE}' VARCHAR(10), '${COL_PACE_M}' VARCHAR(15), " +
                          "'${COL_PACE_H}' VARCHAR(10));"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS '${TABLE_NAME}'")
        }
        onCreate(db)
    }

    fun insertData(data: Data) {
        val dbase = this.readableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $COL_NAME = '${data.name}'"
        val cursor: Cursor = dbase.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            Toast.makeText(context, "exists", Toast.LENGTH_SHORT).show()
        } else {
            val db = this.writableDatabase
            var cv = ContentValues()
            cv.put(COL_NAME, data.name)
            cv.put(COL_TIME, data.time)
            cv.put(COL_DISTANCE, data.distance)
            cv.put(COL_PACE_M, data.paceM)
            cv.put(COL_PACE_H, data.paceH)
            var result = db.insert(TABLE_NAME, null, cv)
            if (result == -1.toLong())
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, "Sucess", Toast.LENGTH_SHORT).show()
        }
    }

    fun readData() : MutableList<Data> {
        var list : MutableList<Data> = LinkedList()

        val db = this.readableDatabase
        val query = "SELECT * from '${TABLE_NAME}'"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()) {
            do {
                var data = Data()
                data.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                data.name = result.getString(result.getColumnIndex(COL_NAME))
                data.time = result.getString(result.getColumnIndex(COL_TIME))
                data.distance = result.getString(result.getColumnIndex(COL_DISTANCE))
                data.paceM = result.getString(result.getColumnIndex(COL_PACE_M))
                data.paceH = result.getString(result.getColumnIndex(COL_PACE_H))
                list.add(data)
            }
            while(result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun updateData(data: Data) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_NAME, data.name)
        cv.put(COL_TIME, data.time)
        cv.put(COL_DISTANCE, data.distance)
        cv.put(COL_PACE_M, data.paceM)
        cv.put(COL_PACE_H, data.paceH)
        db.update(TABLE_NAME, cv, "name = ?", arrayOf(data.name))
    }

    fun deleteData(data: Data) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COL_NAME + " = ?", arrayOf(data.name))
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '${data.name}'");
        db.close()
    }


}