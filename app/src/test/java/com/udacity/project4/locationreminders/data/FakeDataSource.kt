package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.runBlocking
import java.lang.Exception

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    var shouldReturnError = false
    var reminders = mutableListOf<ReminderDTO>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error("Error")
        } else {
            Result.Success(reminders)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (shouldReturnError) {
            Result.Error("Error retrieving reminder")
        } else {
            val reminder = reminders.find { it.id == id }
            return if(reminder == null){
                Result.Error("reminder not found")
            } else{
                Result.Success(reminder)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    fun addReminders(vararg remindersDTO: ReminderDTO) {
        for (reminder in remindersDTO) {
            reminders.add(reminder)
        }
    }
}