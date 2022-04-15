package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private val reminder1 = ReminderDTO("Title1", "Description1", "location1", 1.0, 1.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "location2", 2.0, 2.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "location3", 3.0, 3.0)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO("Title", "Description", "location", 1.0, 1.0)

        //insert reminder
        database.reminderDao().saveReminder(reminder)

        //get reminder by id
        val loaded = database.reminderDao().getReminderById(reminder.id)

        //loaded data contains expected values
        if (loaded != null) {
            assertThat(loaded.id, `is`(reminder.id))
            assertThat(loaded.title, `is`(reminder.title))
            assertThat(loaded.description, `is`(reminder.description))
            assertThat(loaded.location, `is`(reminder.location))
            assertThat(loaded.latitude, `is`(reminder.latitude))
            assertThat(loaded.longitude, `is`(reminder.longitude))
        }
    }

    @Test
    fun getReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        val reminders = database.reminderDao().getReminders()

        assertThat(reminders.size, `is`(3))
    }

    @Test
    fun deleteReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)
        database.reminderDao().saveReminder(reminder3)

        database.reminderDao().deleteAllReminders()

        val reminders = database.reminderDao().getReminders()

        assertThat(reminders.size, `is`(0))
    }
}