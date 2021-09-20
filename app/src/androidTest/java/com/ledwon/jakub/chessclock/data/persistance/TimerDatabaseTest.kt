package com.ledwon.jakub.chessclock.data.persistance

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDb = "timers_db-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TimerDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun shouldCorrectlyMigrateFromVersion1to2() {
        helper.createDatabase(testDb, 1).apply {

            // db has schema version 1. DAO classes can't be used because they expect the latest schema.
            execSQL("INSERT INTO timer (id, description, white_hours, white_minutes, white_seconds, white_increment, black_hours, black_minutes, black_seconds, black_increment, isFavourite) values (1, 'value', 1, 0, 0, 0, 1, 0, 0, 0, 0)")
            close()
        }

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process.
        helper.runMigrationsAndValidate(testDb, 2, true, Migrations.Migration_1_2).apply {
            val cursor = query("SELECT * FROM timer")
            cursor.moveToFirst()

            val timerId = cursor.getInt(cursor.getColumnIndex("id"))
            val isFavourite = cursor.getInt(cursor.getColumnIndex("isFavourite"))
            val whiteHours = cursor.getInt(cursor.getColumnIndex("white_hours"))
            val whiteMinutes = cursor.getInt(cursor.getColumnIndex("white_minutes"))
            val whiteSeconds = cursor.getInt(cursor.getColumnIndex("white_seconds"))
            val whiteIncrement = cursor.getInt(cursor.getColumnIndex("white_increment"))
            val blackHours = cursor.getInt(cursor.getColumnIndex("black_hours"))
            val blackMinutes = cursor.getInt(cursor.getColumnIndex("black_minutes"))
            val blackSeconds = cursor.getInt(cursor.getColumnIndex("black_seconds"))
            val blackIncrement = cursor.getInt(cursor.getColumnIndex("black_increment"))

            cursor.close()
            
            assertEquals(1, timerId)
            assertEquals(0, isFavourite)
            assertEquals(1, whiteHours)
            assertEquals(0, whiteMinutes)
            assertEquals(0, whiteSeconds)
            assertEquals(0, whiteIncrement)
            assertEquals(1, blackHours)
            assertEquals(0, blackMinutes)
            assertEquals(0, blackSeconds)
            assertEquals(0, blackIncrement)
        }
    }
}