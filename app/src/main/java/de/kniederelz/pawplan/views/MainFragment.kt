package de.kniederelz.pawplan.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.auth.AuthViewModel
import de.kniederelz.pawplan.databinding.FragmentMainBinding
import kotlin.getValue

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val viewModel: AuthViewModel by activityViewModels()

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val navHostFragment = binding.mainNavHost.getFragment() as NavHostFragment
            val navController = navHostFragment.navController

            when (item.itemId) {
                R.id.navigation_dogs -> {
                    navController.navigate(R.id.dogsView)
                    true
                }

                R.id.navigation_schedule -> {
                    navController.navigate(R.id.appointmentsOverviewView)
                    true
                }

                R.id.navigation_profiles -> {
                    navController.navigate(R.id.profileView)
                    true
                }

                R.id.navigation_tracker -> {
                    navController.navigate(R.id.trackerView)
                    true
                }

                R.id.navigation_auth -> {
                    navController.navigate(R.id.authView)
                    true
                }

                else -> false
            }
        }

        // check for user
        viewModel.authState.observe(viewLifecycleOwner) { authState ->
            val navHostFragment = binding.mainNavHost.getFragment() as NavHostFragment
            val navController = navHostFragment.navController

            if (!authState.isAuthenticated) {
                binding.bottomNavigation.selectedItemId = R.id.navigation_dogs

                binding.bottomNavigation.menu.findItem(R.id.navigation_tracker)
                    .isVisible = false
                binding.bottomNavigation.menu.findItem(R.id.navigation_schedule)
                    .isVisible = false
                binding.bottomNavigation.menu.findItem(R.id.navigation_profiles)
                    .isVisible = false
                binding.bottomNavigation.menu.findItem(R.id.navigation_auth)
                    .isVisible = true

            } else {
                binding.bottomNavigation.selectedItemId = R.id.navigation_dogs

                binding.bottomNavigation.menu.findItem(R.id.navigation_tracker)
                    .isVisible = true
                binding.bottomNavigation.menu.findItem(R.id.navigation_schedule)
                    .isVisible = true
                binding.bottomNavigation.menu.findItem(R.id.navigation_profiles)
                    .isVisible = true
                binding.bottomNavigation.menu.findItem(R.id.navigation_auth)
                    .isVisible = false
            }
        }
    }
}