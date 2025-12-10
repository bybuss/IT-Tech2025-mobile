package bob.colbaskin.it_tech2025.scanner.presentation.image_selection

import bob.colbaskin.it_tech2025.scanner.domain.models.SelectionArea

sealed class ImageAreaSelectionAction {
    data class SetImageUri(val uri: String) : ImageAreaSelectionAction()
    data class UpdateSelectionArea(val area: SelectionArea) : ImageAreaSelectionAction()
    object Cancel : ImageAreaSelectionAction()
    data class Confirm(val area: SelectionArea) : ImageAreaSelectionAction()
}
