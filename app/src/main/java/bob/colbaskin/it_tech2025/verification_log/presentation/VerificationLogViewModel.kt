package bob.colbaskin.it_tech2025.verification_log.presentation

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bob.colbaskin.it_tech2025.common.ApiResult
import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.verification_log.domain.VerificationLogRepository
import bob.colbaskin.it_tech2025.verification_log.domain.models.DocumentStatus
import bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationLogViewModel @Inject constructor(
    private val repository: VerificationLogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VerificationLogState())
    val state: StateFlow<VerificationLogState> = _state.asStateFlow()

    init {
        loadLogs()
    }

    fun onAction(action: VerificationLogAction) {
        when (action) {
            VerificationLogAction.LoadLogs -> loadLogs()
            VerificationLogAction.Refresh -> refresh()
            VerificationLogAction.ToggleFilters -> toggleFilters()
            VerificationLogAction.ClearFilter -> clearFilter()
            is VerificationLogAction.FilterByStatus -> filterByStatus(action.status)
            else -> Unit
        }
    }

    private fun loadLogs() {
        viewModelScope.launch {
            _state.update { it.copy(logs = UiState.Loading) }

            try {
                val logs = repository.getAll()
                _state.update { it.copy(logs = UiState.Success(logs.first())) }
            } catch (e: Exception) {
                _state.update { it.copy(logs = UiState.Error(title = "успех!", text = "Не удалось загрузить логи")) }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            try {
                delay(1000)
                loadLogs()
            } catch (e: Exception) {
            } finally {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun toggleFilters() {
        _state.update { it.copy(showFilters = !it.showFilters) }
    }

    private fun clearFilter() {
        _state.update { it.copy(selectedFilter = null) }
        loadLogs()
    }

    private fun filterByStatus(status: DocumentStatus) {
        _state.update { it.copy(selectedFilter = status) }
        viewModelScope.launch {
            try {
                val filteredLogs = repository.getByStatus(status.name)
                _state.update { it.copy(logs = UiState.Success(filteredLogs.first())) }
            } catch (e: Exception) {
                _state.update { it.copy(logs = UiState.Error(title = "успех!", text = "Не удалось применить фильтр")) }
            }
        }
    }
}