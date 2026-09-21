package com.aeriotv.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeriotv.android.ui.TmdbAttribution
import com.aeriotv.android.ui.adaptive.LocalTabBarBottomInset
import com.aeriotv.android.ui.adaptive.rememberViewport
import com.aeriotv.android.ui.scale.subtext
import com.aeriotv.android.ui.settings.SettingsDetailTopBar
import com.aeriotv.android.ui.settings.SettingsSection
import com.aeriotv.android.ui.settings.SettingsSelectionRow
import com.aeriotv.android.ui.settings.SettingsToggleRow
import com.aeriotv.android.ui.settings.dpadFocusRing
import com.aeriotv.android.ui.settings.rememberIsTvDevice
import com.aeriotv.android.ui.textfield.SecretRevealIconButton
import com.aeriotv.android.ui.textfield.rememberSecretRevealState
import com.aeriotv.android.ui.tv.TvKeyboardOnOkHost
import com.aeriotv.android.ui.tv.tvFormFieldInput

/**
 * Settings > Movies & TV Shows. Library refresh cadence, TMDB poster lookup,
 * and the Movies & Series display scale.
 *
 * Settings phase 1 regroup: the refresh cadence and Program Posters rows came
 * from App Behaviors, the scale row from Appearance. Keys, control types and
 * copy are carried over unchanged, including the TV-only gate on the refresh
 * cadence rows.
 */
@Composable
fun MoviesAndTvShowsSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val isTv = rememberIsTvDevice()
    val vodRefreshHours by viewModel.vodLibraryRefreshHours.collectAsStateWithLifecycle(initialValue = 24)
    val programPostersTmdb by viewModel.programPostersTmdbEnabled.collectAsStateWithLifecycle(initialValue = false)
    val savedTmdbKey by viewModel.tmdbApiKey.collectAsStateWithLifecycle(initialValue = "")
    val tmdbKeyState by viewModel.tmdbKeyTestState.collectAsStateWithLifecycle()
    val scaleMovies by viewModel.displayScaleMovies.collectAsStateWithLifecycle(initialValue = 1.0f)

    TvKeyboardOnOkHost {
        Column(modifier = Modifier.fillMaxSize()) {
            SettingsDetailTopBar(title = "Filme & Serien", onBack = onBack)

            val vp = rememberViewport()
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                LazyColumn(
                    modifier = if (vp.formMaxWidth != Dp.Unspecified) {
                        Modifier.widthIn(max = vp.formMaxWidth)
                    } else {
                        Modifier
                    },
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = LocalTabBarBottomInset.current,
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    // MARK: Refresh library
                    //
                    // Shown on every form factor (Apple parity): the cadence is
                    // read by the shared OnDemandViewModel, so it always applied
                    // everywhere even while the rows were TV-only.
                    item("refresh-library") {
                        SettingsSection(header = "Bibliothek aktualisieren") {
                            listOf(
                                0 to "Bei jedem Start",
                                24 to "Täglich",
                                168 to "Wöchentlich",
                            ).forEach { (hours, label) ->
                                SettingsSelectionRow(
                                    label = label,
                                    selected = vodRefreshHours == hours,
                                    onClick = { viewModel.setVodLibraryRefreshHours(hours) },
                                )
                            }
                            Text(
                                text = "Live-TV-Sender werden bei jedem Start aktualisiert. Filme und Serien werden aus der gespeicherten Bibliothek geöffnet und nach diesem Zeitplan beim Anbieter neu eingelesen. Ziehe in einem der Bereiche nach unten, um sofort zu aktualisieren.",
                                style = MaterialTheme.typography.bodySmall.subtext(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                        }
                    }

                    // MARK: Posters
                    item("posters") {
                        SettingsSection(
                            header = "Poster",
                            footer = "Zeigt Poster in den Sendungsinformationen und ergänzt fehlende Bilder in den Detailseiten der Mediathek über TMDB mit deinem eigenen kostenlosen API-Schlüssel (themoviedb.org). Standardmäßig ausgeschaltet. Der Schlüssel kann über Google Drive zwischen deinen Geräten synchronisiert werden und bleibt in den privaten App-Daten.",
                        ) {
                            SettingsToggleRow(
                                title = "TMDB-Poster als Ersatz",
                                subtitle = "Wenn ein Poster fehlt, wird es bei TMDB gesucht. Dafür wird der kostenlose API-Schlüssel unten benötigt.",
                                checked = programPostersTmdb,
                                onCheckedChange = viewModel::setProgramPostersTmdbEnabled,
                            )
                            if (programPostersTmdb) {
                                var keyDraft by remember(savedTmdbKey) { mutableStateOf(savedTmdbKey) }
                                val keyReveal = rememberSecretRevealState()
                                TmdbAttribution(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                                    long = false,
                                    isTv = isTv,
                                )
                                OutlinedTextField(
                                    value = keyDraft,
                                    onValueChange = {
                                        keyDraft = it
                                        viewModel.resetTmdbKeyTestState()
                                    },
                                    label = { Text("TMDB-API-Schlüssel (v3) oder Lesetoken (v4)") },
                                    singleLine = true,
                                    visualTransformation = keyReveal.transformation,
                                    trailingIcon = {
                                        SecretRevealIconButton(state = keyReveal, contentLabel = "Schlüssel")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .tvFormFieldInput(
                                            horizontalFocusEscape = true,
                                            okSuppressed = { keyReveal.controlFocused },
                                        ),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.testTmdbKey(keyDraft) },
                                        enabled = keyDraft.isNotBlank() &&
                                            tmdbKeyState != SettingsViewModel.TmdbKeyTestState.Testing,
                                        modifier = Modifier.dpadFocusRing(RoundedCornerShape(50)),
                                    ) { Text("Testen") }
                                    TextButton(
                                        onClick = { viewModel.saveTmdbKey(keyDraft) },
                                        enabled = keyDraft.isNotBlank() || savedTmdbKey.isNotBlank(),
                                        modifier = Modifier.dpadFocusRing(RoundedCornerShape(50)),
                                    ) { Text("Speichern") }
                                    Spacer(Modifier.weight(1f))
                                    val (statusText, statusColor) = when (tmdbKeyState) {
                                        SettingsViewModel.TmdbKeyTestState.Testing ->
                                            "Wird geprüft..." to MaterialTheme.colorScheme.onSurfaceVariant
                                        SettingsViewModel.TmdbKeyTestState.Valid ->
                                            "Gültiger Schlüssel" to androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        SettingsViewModel.TmdbKeyTestState.Invalid ->
                                            "Ungültiger Schlüssel" to MaterialTheme.colorScheme.error
                                        SettingsViewModel.TmdbKeyTestState.Saved ->
                                            "Gespeichert" to androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                        SettingsViewModel.TmdbKeyTestState.Idle ->
                                            "" to MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                    if (statusText.isNotEmpty()) {
                                        Text(
                                            statusText,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = statusColor,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // MARK: Display Scale
                    settingsCard(
                        header = "Darstellungsgröße",
                        footer = "Eigene Darstellungsgröße für Filme & Serien. 100 % entspricht dem Standard; mit 85–175 % kannst du zwischen mehr Inhalt und besserer Lesbarkeit wählen. Ab 150 % werden weniger, dafür größere Elemente angezeigt – praktisch auf dem Fernseher. Änderungen werden sofort übernommen.",
                    ) {
                        ScaleSliderRow(
                            label = "Filme & Serien",
                            value = scaleMovies,
                            onValueChange = viewModel::setDisplayScaleMovies,
                        )
                    }
                }
            }
        }
    }
}
