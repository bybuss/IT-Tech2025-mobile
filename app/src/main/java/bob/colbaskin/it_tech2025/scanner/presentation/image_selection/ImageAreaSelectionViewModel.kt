package bob.colbaskin.it_tech2025.scanner.presentation.image_selection

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageAreaSelectionViewModel @Inject constructor() : ViewModel() {

    var state by mutableStateOf(ImageAreaSelectionState())
        private set

    fun onAction(action: ImageAreaSelectionAction) {
        when (action) {
            is ImageAreaSelectionAction.SetImageUri -> {
                state = state.copy(imageUri = action.uri)
            }
            is ImageAreaSelectionAction.UpdateSelectionArea -> {
                state = state.copy(selectionArea = action.area)
            }
            else -> {}
        }
    }
}