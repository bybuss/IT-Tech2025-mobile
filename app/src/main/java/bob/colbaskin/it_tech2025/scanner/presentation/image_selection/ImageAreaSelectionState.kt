package bob.colbaskin.it_tech2025.scanner.presentation.image_selection

import bob.colbaskin.it_tech2025.scanner.domain.models.SelectionArea

data class ImageAreaSelectionState(
    val imageUri: String? = null,
    val selectionArea: SelectionArea? = null,
    val isLoading: Boolean = false
)
