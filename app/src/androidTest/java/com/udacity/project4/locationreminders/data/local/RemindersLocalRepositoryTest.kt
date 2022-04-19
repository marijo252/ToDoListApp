package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    private val reminder1 = ReminderDTO("Title1", "Description1", "location1", 1.0, 1.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "location2", 2.0, 2.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "location3", 3.0, 3.0)

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        remindersLocalRepository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrieveReminder() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)

        val result = remindersLocalRepository.getReminder(reminder1.id)
        result as Result.Success

        assertThat(result.data.id, `is`(reminder1.id))
        assertThat(result.data.title, `is`(reminder1.title))
        assertThat(result.data.description, `is`(reminder1.description))
        assertThat(result.data.location, `is`(reminder1.location))
        assertThat(result.data.latitude, `is`(reminder1.latitude))
        assertThat(result.data.longitude, `is`(reminder1.longitude))
    }

    @Test
    fun getReminders() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        val reminders = remindersLocalRepository.getReminders() as Result.Success

        assertThat(reminders.data.size, `is`(3))
    }

    @Test
    fun deleteReminders() = runBlocking {
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)
        remindersLocalRepository.saveReminder(reminder3)

        remindersLocalRepository.deleteAllReminders()

        val reminders = remindersLocalRepository.getReminders() as Result.Success

        assertThat(reminders.data.size, `is`(0))
    }

    @Test
    fun getReminder_reminderNotFound() = runBlocking {
        val reminder = remindersLocalRepository.getReminder("123")
        reminder as Result.Error
        assertThat(reminder.message, `is` ("Reminder not found!"))
    }
}