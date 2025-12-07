package com.example.habittracker.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.habittracker.data.Habit

class HabitDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "HabitTracker.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_HABITS = "habits"
        private const val COLUMN_ID = "id"
        private const val COLUMN_HABIT_NAME = "habit_name"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_HABITS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HABIT_NAME TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HABITS")
        onCreate(db)
    }

    fun insertHabit(habit: Habit): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_HABIT_NAME, habit.habitName)
            put(COLUMN_DATE, habit.date)
            put(COLUMN_TIMESTAMP, habit.timestamp)
        }

        return db.insert(TABLE_HABITS, null, values)
    }

    fun getHabitsByDate(date: String): List<Habit> {
        val habits = mutableListOf<Habit>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_HABITS,
            null,
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null,
            null,
            "$COLUMN_TIMESTAMP DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val habit = Habit(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    habitName = it.getString(it.getColumnIndexOrThrow(COLUMN_HABIT_NAME)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
                habits.add(habit)
            }
        }

        return habits
    }

    fun getHabitHistory(habitName: String): List<Habit> {
        val habits = mutableListOf<Habit>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_HABITS,
            null,
            "$COLUMN_HABIT_NAME = ?",
            arrayOf(habitName),
            null,
            null,
            "$COLUMN_DATE DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val habit = Habit(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    habitName = it.getString(it.getColumnIndexOrThrow(COLUMN_HABIT_NAME)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
                habits.add(habit)
            }
        }

        return habits
    }

    fun getHabitStatistics(): Map<String, Int> {
        val stats = mutableMapOf<String, Int>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT $COLUMN_HABIT_NAME, COUNT(*) as count 
            FROM $TABLE_HABITS 
            GROUP BY $COLUMN_HABIT_NAME
            ORDER BY count DESC
            """.trimIndent(),
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val habitName = it.getString(0)
                val count = it.getInt(1)
                stats[habitName] = count
            }
        }

        return stats
    }

    fun getAllHabits(): List<Habit> {
        val habits = mutableListOf<Habit>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_HABITS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_TIMESTAMP DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val habit = Habit(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    habitName = it.getString(it.getColumnIndexOrThrow(COLUMN_HABIT_NAME)),
                    date = it.getString(it.getColumnIndexOrThrow(COLUMN_DATE)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
                habits.add(habit)
            }
        }

        return habits
    }

    fun deleteHabit(habitId: Int): Int {
        val db = writableDatabase
        val deleted = db.delete(
            TABLE_HABITS,
            "$COLUMN_ID = ?",
            arrayOf(habitId.toString())
        )
        db.close()
        return deleted
    }

    fun deleteHabitByName(habitName: String): Int{
        val db = writableDatabase
        val deleted = db.delete(
            TABLE_HABITS,
            "$COLUMN_HABIT_NAME = ?",
            arrayOf(habitName)
        )
        db.close()
        return deleted
    }
}