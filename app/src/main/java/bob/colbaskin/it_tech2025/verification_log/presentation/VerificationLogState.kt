package bob.colbaskin.it_tech2025.verification_log.presentation

import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.verification_log.domain.models.DocumentStatus
import bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog

data class VerificationLogState(
    val logs: UiState<List<VerificationLog>> = UiState.Loading,
    val selectedFilter: DocumentStatus? = null,
    val isRefreshing: Boolean = false,
    val showFilters: Boolean = false
)
