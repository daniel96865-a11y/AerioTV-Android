package com.aeriotv.android.feature.settings

import com.aeriotv.android.ui.scale.subtext
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeriotv.android.BuildConfig
import com.aeriotv.android.core.update.UpdateState
import com.aeriotv.android.feature.update.UpdateViewModel
import com.aeriotv.android.ui.adaptive.adaptiveFormWidth
import com.aeriotv.android.ui.settings.SettingsActionRow
import com.aeriotv.android.ui.settings.SettingsDetailTopBar
import com.aeriotv.android.ui.settings.SettingsInfoRow
import com.aeriotv.android.ui.settings.SettingsRowContainer
import com.aeriotv.android.ui.settings.SettingsSection
import com.aeriotv.android.ui.adaptive.LocalTabBarBottomInset

/**
 * Settings > App Updates (github flavor only; the row that opens this screen
 * is hidden when the updater is disabled). Manual check + the full
 * download/install state, mirroring the launch prompt's actions.
 *
 * Every actionable element is a [SettingsActionRow] (the SettingsRowContainer
 * family), NOT a Material button: those rows are the screen's D-pad focus
 * unit, with the card highlight TV users can see. A plain OutlinedButton here
 * was unreachable/invisible to D-pad focus on the Streamer.
 */
@Composable
fun AppUpdatesScreen(
    onBack: () -> Unit,
    viewModel: UpdateViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        SettingsDetailTopBar(title = "App-Updates", onBack = onBack)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .adaptiveFormWidth()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = LocalTabBarBottomInset.current),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                SettingsSection(
                    header = "Dieses Gerät",
                    footer = "Updates für diese Variante kommen aus den GitHub-" +
                        "Releases des Projekts. Beim Installieren bleiben Sender, Einstellungen und " +
                        "Aufnahmen erhalten; AerioTV Deutsch wird für die Installation geschlossen und danach " +
                        "über den Startbildschirm wieder geöffnet.",
                ) {
                    SettingsInfoRow(label = "Version", value = BuildConfig.VERSION_NAME)
                    SettingsInfoRow(label = "Sender", value = "GitHub-Releases")
                    SettingsActionRow(
                        label = "Nach Updates suchen",
                        leadingIcon = Icons.Filled.Refresh,
                        onClick = { viewModel.manualCheck() },
                    )
                }

                when (val s = state) {
                    is UpdateState.UpToDate -> StatusText("Du verwendest die neueste Version.")
                    is UpdateState.Available -> SettingsSection(
                        header = "Update verfügbar",
                        footer = s.info.notes.ifBlank { null },
                    ) {
                        SettingsActionRow(
                            label = "AerioTV Deutsch ${s.info.versionName} herunterladen",
                            subtitle = "${s.info.apkSizeBytes / (1024 * 1024)} MB von GitHub",
                            leadingIcon = Icons.Filled.Download,
                            onClick = { viewModel.download() },
                        )
                    }
                    is UpdateState.Downloading -> Column {
                        StatusText("AerioTV Deutsch ${s.info.versionName} wird heruntergeladen... ${s.progressPercent}%")
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { s.progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    is UpdateState.Verifying -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.width(22.dp).height(22.dp))
                        Spacer(Modifier.width(10.dp))
                        StatusText("Download wird geprüft...")
                    }
                    is UpdateState.ReadyToInstall -> SettingsSection(
                        header = "Bereit zur Installation",
                        footer = "Deine Daten bleiben erhalten. AerioTV Deutsch wird für die Installation geschlossen; öffne die App anschließend " +
                            "über den Startbildschirm wieder geöffnet.",
                    ) {
                        SettingsActionRow(
                            label = "AerioTV Deutsch ${s.info.versionName} installieren",
                            leadingIcon = Icons.Filled.SystemUpdate,
                            onClick = { viewModel.install() },
                        )
                    }
                    is UpdateState.AwaitingInstallPermission -> SettingsSection(
                        header = "Einmalige Berechtigung erforderlich",
                        footer = "Erlaube AerioTV Deutsch in den Android-Einstellungen, Updates zu installieren, " +
                            "und kehre danach zurück. Wenn du es bereits erlaubt hast, wird die Installation " +
                            "sofort fortgesetzt.",
                    ) {
                        SettingsActionRow(
                            label = "Installieren",
                            leadingIcon = Icons.Filled.SystemUpdate,
                            onClick = { viewModel.install() },
                        )
                    }
                    is UpdateState.Installing -> StatusText(
                        "Bestätige das Update im Android-Dialog. AerioTV Deutsch wird für die " +
                            "Installation geschlossen.",
                    )
                    is UpdateState.Error -> SettingsSection(
                        header = "Update-Problem",
                        footer = s.message,
                    ) {
                        SettingsActionRow(
                            label = if (s.info != null) "Erneut versuchen" else "Erneut prüfen",
                            leadingIcon = Icons.Filled.Refresh,
                            onClick = {
                                if (s.info != null) viewModel.download() else viewModel.manualCheck()
                            },
                        )
                        SettingsActionRow(
                            label = "Schließen",
                            leadingIcon = Icons.Filled.Close,
                            onClick = { viewModel.dismissError() },
                        )
                    }
                    UpdateState.Idle -> Unit
                }
            }
        }
    }
}

@Composable
private fun StatusText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.subtext(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}
