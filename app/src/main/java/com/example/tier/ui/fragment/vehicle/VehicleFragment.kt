package com.example.tier.ui.fragment.vehicle

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.tier.R
import com.example.tier.databinding.FragmentVehicleBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VehicleFragment : BottomSheetDialogFragment() {

    private val args by navArgs<VehicleFragmentArgs>()

    private var _binding: FragmentVehicleBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehicleBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also {
            setStyle(STYLE_NO_FRAME, 0)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).apply {
            backgroundTintMode = PorterDuff.Mode.CLEAR
            backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            setBackgroundColor(Color.TRANSPARENT)
        }
        with(binding) {
            type.text = getString(R.string.vehicle_details_type, args.vehicle.type)
            distance.text = getString(R.string.vehicle_details_distance, args.distance)
            battery.text = getString(R.string.vehicle_details_battery, args.vehicle.batteryLevel)
            maxSpeed.text = getString(R.string.vehicle_details_max_speed, args.vehicle.maxSpeed)
            val hasHelmetBox =
                if (args.vehicle.hasHelmetBox) getString(R.string.yes) else getString(R.string.no)
            helmet.text = getString(R.string.vehicle_details_helmet, hasHelmetBox)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
