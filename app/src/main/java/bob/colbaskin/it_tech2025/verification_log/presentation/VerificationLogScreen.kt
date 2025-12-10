package bob.colbaskin.it_tech2025.verification_log.presentation

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import bob.colbaskin.it_tech2025.common.UiState
import bob.colbaskin.it_tech2025.common.biometric.BiometricAuthManager
import bob.colbaskin.it_tech2025.common.design_system.ErrorScreen
import bob.colbaskin.it_tech2025.common.design_system.LoadingScreen
import bob.colbaskin.it_tech2025.navigation.Screens
import bob.colbaskin.it_tech2025.verification_log.domain.models.DocumentStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VerificationLogScreenRoot(
    navController: NavHostController
) {
    val viewModel: VerificationLogViewModel = hiltViewModel()

    VerificationLogScreenWithAuth(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
private fun VerificationLogScreenWithAuth(
    navController: NavHostController,
    viewModel: VerificationLogViewModel
) {
    val context = LocalContext.current

    var showAuthScreen by remember { mutableStateOf(true) }
    var authError by remember { mutableStateOf<String?>(null) }
    var needsBioSetup by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            BiometricAuthActivity.RESULT_AUTH_SUCCESS -> {
                showAuthScreen = false
            }
            BiometricAuthActivity.RESULT_AUTH_FAILED -> {
                val errorMsg = result.data?.getStringExtra(
                    BiometricAuthActivity.EXTRA_ERROR_MESSAGE
                ) ?: "Ошибка аутентификации"
                needsBioSetup = result.data?.getBooleanExtra(
                    BiometricAuthActivity.EXTRA_NEEDS_BIO_SETUP, false
                ) ?: false
                authError = errorMsg
                showAuthScreen = true
            }
            BiometricAuthActivity.RESULT_AUTH_CANCELLED -> {
                navController.popBackStack()
            }
        }
    }

    LaunchedEffect(Unit) {
        if (showAuthScreen && authError == null) {
            val intent = Intent(context, BiometricAuthActivity::class.java)
            authLauncher.launch(intent)
        }
    }

    if (showAuthScreen) {
        AuthScreenContent(
            authError = authError,
            needsBioSetup = needsBioSetup,
            onRetry = {
                authError = null
                val intent = Intent(context, BiometricAuthActivity::class.java)
                authLauncher.launch(intent)
            },
            onBack = { navController.popBackStack() }
        )
    } else {
        VerificationLogScreenContent(
            state = state,
            onAction = { action ->
                when (action) {
                    VerificationLogAction.NavigateToScan -> {
                        navController.navigate(Screens.ScannerScreen)
                    }
                    else -> viewModel.onAction(action)
                }
            }
        )
    }
}

@Composable
private fun AuthScreenContent(
    authError: String?,
    needsBioSetup: Boolean,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (authError != null) {
            Text(
                text = authError,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (needsBioSetup) {
                Text(
                    text = "Пожалуйста, настройте биометрическую аутентификацию в настройках устройства и попробуйте снова.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Повторить")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("Назад")
            }
        } else {
            Text(
                text = "Запуск биометрической аутентификации...",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator()
        }
    }
}

@Composable
private fun VerificationLogScreenContent(
    state: VerificationLogState,
    onAction: (VerificationLogAction) -> Unit
) {
    when (val logs = state.logs) {
        is UiState.Error -> {
            ErrorScreen(
                message = logs.text,
                onError = { onAction(VerificationLogAction.LoadLogs) }
            )
        }
        UiState.Loading -> {
            LoadingScreen(
                onError = { onAction(VerificationLogAction.LoadLogs) }
            )
        }
        is UiState.Success -> {
            if (logs.data.isEmpty()) {
                ErrorScreen(
                    message = "Просканируйте документ, чтобы у вас были данные для отображения!",
                    onError = { onAction(VerificationLogAction.NavigateToScan) }
                )
            } else {
                VerificationLogScreen(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerificationLogScreen(
    state: VerificationLogState,
    onAction: (VerificationLogAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Журнал верификаций") },
                actions = {
                    IconButton(onClick = { onAction(VerificationLogAction.ToggleFilters) }) {
                        Icon(
                            imageVector = if (state.selectedFilter == null) {
                                Icons.Default.FilterList
                            } else {
                                Icons.Default.Close
                            },
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.showFilters) {
                FiltersSheet(
                    selectedFilter = state.selectedFilter,
                    onFilterSelected = { status ->
                        onAction(VerificationLogAction.FilterByStatus(status))
                    },
                    onClearFilter = {
                        onAction(VerificationLogAction.ClearFilter)
                    },
                    onDismiss = {
                        onAction(VerificationLogAction.ToggleFilters)
                    }
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(VerificationLogAction.Refresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                when (val logs = state.logs) {
                    is UiState.Error -> {
                        ErrorScreen(
                            message = logs.text,
                            onError = { onAction(VerificationLogAction.LoadLogs) }
                        )
                    }
                    UiState.Loading -> {
                        LoadingScreen(
                            onError = { onAction(VerificationLogAction.LoadLogs) }
                        )
                    }
                    is UiState.Success -> {
                        if (logs.data.isEmpty()) {
                            ErrorScreen(
                                message = "Просканируйте документ, чтобы у вас были данные для отображения!",
                                onError = { onAction(VerificationLogAction.NavigateToScan) }
                            )
                        } else {
                            LogsList(
                                logs = logs.data,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltersSheet(
    selectedFilter: DocumentStatus?,
    onFilterSelected: (DocumentStatus) -> Unit,
    onClearFilter: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Фильтры по статусу",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            DocumentStatus.entries.forEach { status ->
                FilterChip(
                    selected = selectedFilter == status,
                    onClick = { onFilterSelected(status) },
                    label = {
                        Text(
                            text = when (status) {
                                DocumentStatus.GREEN -> "Зеленые"
                                DocumentStatus.YELLOW -> "Желтые"
                                DocumentStatus.RED -> "Красные"
                            }
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (status) {
                            DocumentStatus.GREEN -> MaterialTheme.colorScheme.primaryContainer
                            DocumentStatus.YELLOW -> MaterialTheme.colorScheme.secondaryContainer
                            DocumentStatus.RED -> MaterialTheme.colorScheme.errorContainer
                        }
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedFilter != null) {
                Button(
                    onClick = onClearFilter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Очистить фильтр")
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text("Закрыть", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun LogsList(
    logs: List<bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(logs) { log ->
            VerificationLogCard(log = log)
        }
    }
}

@Composable
private fun VerificationLogCard(
    log: bob.colbaskin.it_tech2025.verification_log.domain.models.VerificationLog
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (log.status) {
                DocumentStatus.GREEN -> MaterialTheme.colorScheme.primaryContainer
                DocumentStatus.YELLOW -> MaterialTheme.colorScheme.secondaryContainer
                DocumentStatus.RED -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Документ ${log.documentId}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatDate(log.checkedAt),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDate(log.createdAt, showTime = false),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatDate(log.expirationDate, showTime = false),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

private fun formatDate(date: Date, showTime: Boolean = true): String {
    val pattern = if (showTime) "dd.MM.yyyy HH:mm" else "dd.MM.yyyy"
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(date)
}
