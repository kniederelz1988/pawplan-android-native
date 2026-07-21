package de.kniederelz.pawplan.auth.presentation.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.auth.domain.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

}