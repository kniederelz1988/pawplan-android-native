package de.kniederelz.pawplan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.kniederelz.pawplan.auth.AuthDialogFragment
import de.kniederelz.pawplan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

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

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_schedule -> {
                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_dogs -> {
                    Toast.makeText(this, "Dashboard selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_profiles -> {
                    Toast.makeText(this, "Notifications selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        binding.button.setOnClickListener {
            AuthDialogFragment().show(supportFragmentManager, AuthDialogFragment.TAG)
        }
    }
}