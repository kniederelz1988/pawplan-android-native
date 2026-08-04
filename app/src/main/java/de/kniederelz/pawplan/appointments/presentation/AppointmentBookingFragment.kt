package de.kniederelz.pawplan.appointments.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.roundToFiveMinutes
import de.kniederelz.pawplan.core.extensions.timeFormatter
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.databinding.FragmentAppointmentBookingBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.util.Calendar

@AndroidEntryPoint
class AppointmentBookingFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "AuthBottomSheetDialogFragment"
    }

    private lateinit var binding: FragmentAppointmentBookingBinding

    private lateinit var selectedDate: LocalDate
    private lateinit var selectedTime: LocalTime

    private val viewModel: AppointmentBookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val now = LocalDateTime.now().plusDays(1)
        selectedDate = now.toLocalDate()
        selectedTime = now.toLocalTime()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentAppointmentBookingBinding.inflate(inflater, container, false)

        binding.selectDateButton.text = selectedDate.format(dateFormatter)
        binding.selectDateButton.setOnClickListener {
            showDatePicker()
        }

        binding.selectTimeButton.text = selectedTime.format(timeFormatter)
        binding.selectTimeButton.setOnClickListener {
            showTimePicker()
        }

        binding.submitButton.setOnClickListener {
            val dogId = arguments?.getString("dogId") ?: return@setOnClickListener

            Log.d("BookAppointmentFragment", "Creating appointment for dog $dogId")
            viewModel.createAppointment(dogId, LocalDateTime.of(selectedDate, selectedTime))

            dismiss()
        }

        return binding.root
    }

    private fun showDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()

        val tomorrow = Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        val sevenDaysFromNow = Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, 7)
        }.timeInMillis

        val constraints = CalendarConstraints.Builder()
            .setValidator(
                CompositeDateValidator.allOf(
                    listOf(
                        DateValidatorPointForward.from(tomorrow),
                        DateValidatorPointBackward.before(sevenDaysFromNow)
                    )
                )
            )
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
            .setCalendarConstraints(constraints)
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            selectedDate = selection.toLocalDate()

            binding.selectDateButton.post {
                binding.selectDateButton.text = selectedDate.format(dateFormatter)
            }
        }

        picker.show(parentFragmentManager, "date_picker")
    }
    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(selectedTime.hour)
            .setMinute(selectedTime.minute)
            .setTitleText("Select time")
            .build()

        picker.addOnPositiveButtonClickListener {
            selectedTime = LocalTime.of(picker.hour, picker.minute).roundToFiveMinutes()

            binding.selectTimeButton.post {
                binding.selectTimeButton.text = selectedTime.format(timeFormatter)
            }
        }

        picker.show(parentFragmentManager, "time_picker")
    }
}