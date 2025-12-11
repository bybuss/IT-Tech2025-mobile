package bob.colbaskin.it_tech2025.scanner.presentation.image_selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bob.colbaskin.it_tech2025.R
import bob.colbaskin.it_tech2025.common.design_system.theme.CustomTheme
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun ImageAreaSelectionScreenRoot(
    navController: NavHostController,
    viewModel: ImageAreaSelectionViewModel = hiltViewModel()
) {
    val imageUri: String?
        = navController.currentBackStackEntry?.arguments?.getString("imageUri")

    LaunchedEffect(imageUri) {
        if (imageUri != null) {
            viewModel.onAction(ImageAreaSelectionAction.SetImageUri(imageUri))
        } else {
            navController.popBackStack()
        }
    }

    ImageAreaSelectionScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                ImageAreaSelectionAction.Cancel -> navController.popBackStack()
                is ImageAreaSelectionAction.Confirm -> {
                    navController.popBackStack()
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
fun ImageAreaSelectionScreen(
    state: ImageAreaSelectionState,
    onAction: (ImageAreaSelectionAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        state.imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .build()
                ),
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { onAction(ImageAreaSelectionAction.Cancel) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomTheme.colors.glass,
                    contentColor = CustomTheme.colors.black
                ),
                modifier = Modifier.size(60.dp),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Cancel",
                    modifier = Modifier.size(32.dp)
                )
            }

            Button(
                onClick = {
                    state.selectionArea?.let { area ->
                        onAction(ImageAreaSelectionAction.Confirm(area))
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomTheme.colors.glass,
                    contentColor = CustomTheme.colors.black
                ),
                modifier = Modifier.size(60.dp),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Confirm",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}



