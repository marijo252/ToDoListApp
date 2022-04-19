package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel
    private val reminder1 = ReminderDTO("Title1", "Description1", "location1", 1.0, 1.0)
    private val reminder2 = ReminderDTO("Title2", "Description2", "location2", 2.0, 2.0)
    private val reminder3 = ReminderDTO("Title3", "Description3", "location3", 3.0, 3.0)

    @Before
    fun setUpViewModel() {
        remindersDataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersDataSource)
    }

    @After
    fun stop() {
        stopKoin()
    }

    @Test
    fun loadReminders_whenSuccess_reminderListNotEmpty() = mainCoroutineRule.runBlockingTest {
        remindersDataSource.addReminders(reminder1, reminder2, reminder3)

        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isNotEmpty(), `is`(true))
    }

    @Test
    fun loadReminders_whenFailure_reminderListEmpty() = mainCoroutineRule.runBlockingTest {
        remindersDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()

        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun loadReminders_whenLoading_showLoadingValue() = mainCoroutineRule.runBlockingTest {
        remindersDataSource.addReminders(reminder1, reminder2, reminder3)
        remindersDataSource.setReturnError(false)

        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_whenError_showSnackBar() = mainCoroutineRule.runBlockingTest {
        remindersDataSource.setReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), notNullValue())
    }
}

