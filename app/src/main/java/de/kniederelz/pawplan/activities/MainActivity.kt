package de.kniederelz.pawplan.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.activities.permissions.MainPermissionLauncher
import de.kniederelz.pawplan.activities.permissions.MainPermissionManager
import de.kniederelz.pawplan.appointments.notifications.AppointmentNotificationSync
import de.kniederelz.pawplan.databinding.ActivityMainBinding
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var permissionManager: MainPermissionManager
    private val permissionLauncher = MainPermissionLauncher(
        this
    ) { requestNextPermission() }

    @Inject
    lateinit var appointmentNotificationSync : AppointmentNotificationSync

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Bottom padding handled by Nav
            insets
        }

        requestNextPermission()
    }
    override fun onResume() {
        super.onResume()

        if (!permissionManager.canScheduleExactAlarms()) {
            return
        }

        appointmentNotificationSync.start()
    }

    private fun requestNextPermission() {
        if (!permissionLauncher.requestNextPermission(permissionManager))
            return

        if (!permissionManager.canScheduleExactAlarms()){
            permissionManager.requestExactAlarmPermission()
            return
        }

        appointmentNotificationSync.start()
    }
}