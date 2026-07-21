package de.kniederelz.pawplan.tracking.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import de.kniederelz.pawplan.databinding.FragmentTrackerReportBinding
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.roundToFiveMinutes
import de.kniederelz.pawplan.core.extensions.timeFormatter
import de.kniederelz.pawplan.core.extensions.toLocalDate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * A simple [Fragment] subclass.
 * Use the [TrackerReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackerReportFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "ReportBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance() = TrackerReportFragment()
    }

    private lateinit var binding: FragmentTrackerReportBinding
    private lateinit var dateButton: MaterialButton
    private lateinit var timeButton: MaterialButton

    private lateinit var selectedDate: LocalDate
    private lateinit var selectedTime: LocalTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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
        binding = FragmentTrackerReportBinding.inflate(inflater, container, false)

        dateButton = binding.selectDateButton
        dateButton.text = selectedDate.format(dateFormatter)
        timeButton = binding.selectTimeButton
        timeButton.text = selectedTime.format(timeFormatter)

        binding.selectDateButton.setOnClickListener {
            showDatePicker()
        }
        binding.selectTimeButton.setOnClickListener {
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

            dateButton.text = selectedDate.format(dateFormatter)
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

