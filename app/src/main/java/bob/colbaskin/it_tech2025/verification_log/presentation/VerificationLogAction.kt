package bob.colbaskin.it_tech2025.verification_log.presentation

import bob.colbaskin.it_tech2025.verification_log.domain.models.DocumentStatus

sealed interface VerificationLogAction {
    data object LoadLogs: VerificationLogAction
    data object Refresh: VerificationLogAction
    data object Authenticate: VerificationLogAction
    data object ClearFilter: VerificationLogAction
    data object ToggleFilters: VerificationLogAction
    data class FilterByStatus(val status: DocumentStatus): VerificationLogAction
    data object OpenDocumentDetails: VerificationLogAction
    data object NavigateToScan: VerificationLogAction
}
