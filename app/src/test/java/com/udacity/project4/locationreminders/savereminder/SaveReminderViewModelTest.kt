package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private var reminder1 = ReminderDataItem("Title1", "Description1", "location1", 1.0, 1.0)
    //TODO: provide testing to the SaveReminderView and its live data objects
    @Before
    fun setUpViewModel() {
        remindersDataSource = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersDataSource)

    }

    @After
    fun stop() {
        stopKoin()
    }

    @Test
    fun validateAndSaveReminder_whenSaving_showLoadingAndToastValue() = mainCoroutineRule.runBlockingTest{
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminder1)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue().isNotEmpty(),`is`(true))
    }

    @Test
    fun validateAndSaveReminder_whenValidatingData_showSnackBar() {
        //when reminder title is null
        reminder1.title = null
        saveReminderViewModel.validateAndSaveReminder(reminder1)
        assert(saveReminderViewModel.showSnackBarInt.getOrAwaitValue() == R.string.err_enter_title)

        //when reminder location is null
        reminder1.title = "Title1"
        reminder1.location = null
        saveReminderViewModel.validateAndSaveReminder(reminder1)
        assert(saveReminderViewModel.showSnackBarInt.getOrAwaitValue() == R.string.err_select_location)
    }

    @Test
    fun onClear_setValuesToNull(){
        saveReminderViewModel.reminderTitle.value = "Title"
        saveReminderViewModel.reminderDescription.value = "Description"
        saveReminderViewModel.reminderSelectedLocationStr.value = "location"
        saveReminderViewModel.selectedPOI.value = PointOfInterest(LatLng(1.0,1.0),"","")
        saveReminderViewModel.latitude.value = 1.0
        saveReminderViewModel.longitude.value = 1.0

        saveReminderViewModel.onClear()
        assert(saveReminderViewModel.reminderTitle.getOrAwaitValue() == null)
        assert(saveReminderViewModel.reminderDescription.getOrAwaitValue() == null)
        assert(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue() == null)
        assert(saveReminderViewModel.selectedPOI.getOrAwaitValue() == null)
        assert(saveReminderViewModel.latitude.getOrAwaitValue() == null)
        assert(saveReminderViewModel.longitude.getOrAwaitValue() == null)

    }
}