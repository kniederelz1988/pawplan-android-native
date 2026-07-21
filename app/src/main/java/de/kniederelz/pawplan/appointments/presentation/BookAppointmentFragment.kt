package de.kniederelz.pawplan.appointments.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import de.kniederelz.pawplan.databinding.FragmentBookAppointmentBinding
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.roundToFiveMinutes
import de.kniederelz.pawplan.core.extensions.timeFormatter
import de.kniederelz.pawplan.core.extensions.toLocalDate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

private const val ARG_PARAM1 = "param1"

class BookAppointmentFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "AuthBottomSheetDialogFragment"
    }

    private var dogId: String? = null

    private lateinit var binding: FragmentBookAppointmentBinding
    private lateinit var dateButton: MaterialButton
    private lateinit var timeButton: MaterialButton


    private lateinit var selectedDate: LocalDate
    private lateinit var selectedTime: LocalTime


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dogId = it.getString(ARG_PARAM1)
        }

        val now = LocalDateTime.now()
        selectedDate = now.toLocalDate()
        selectedTime = now.toLocalTime()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
        binding.selectDogButton.text = dogId

        dateButton = binding.selectDateButton
        dateButton.text = selectedDate.format(dateFormatter)
        dateButton.setOnClickListener {
            showDatePicker()
        }

        timeButton = binding.selectTimeButton
        timeButton.text = selectedTime.format(timeFormatter)
        timeButton.setOnClickListener {
            showTimePicker()
        }

        return binding.root
    }
    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            selectedDate = selection.toLocalDate()

            dateButton.post {
                dateButton.text = selectedDate.format(dateFormatter)
            }
        }

        picker.show(parentFragmentManager, "date_picker")
    }
    private fun showTimePicker() {
        val now = Instant.now()

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(now.atZone(ZoneId.systemDefault()).hour)
            .setMinute(now.atZone(ZoneId.systemDefault()).minute)
            .setTitleText("Select time")
            .build()

        picker.addOnPositiveButtonClickListener {
            selectedTime = LocalTime.of(picker.hour, picker.minute).roundToFiveMinutes()

            timeButton.post {
                timeButton.text = selectedTime.format(timeFormatter)
            }
        }

        picker.show(parentFragmentManager, "time_picker")
    }

}