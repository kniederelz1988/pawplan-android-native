package de.kniederelz.pawplan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentAuthRegisterBinding
import de.kniederelz.pawplan.databinding.FragmentBookAppointmentBinding

private const val ARG_PARAM1 = "param1"

class BookAppointmentFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "AuthBottomSheetDialogFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment BookAppointmentFragment.
         */
        @JvmStatic
        fun newInstance(param1: String) = BookAppointmentFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
            }
        }
    }

    private var param1: String? = null

    private lateinit var binding: FragmentBookAppointmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookAppointmentBinding.inflate(inflater, container, false)
        binding.param1.text = param1

        return binding.root
    }
}