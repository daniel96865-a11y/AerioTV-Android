package com.aeriotv.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeriotv.android.feature.main.AppTab
import com.aeriotv.android.ui.adaptive.LocalTabBarBottomInset
import com.aeriotv.android.ui.adaptive.adaptiveFormWidth
import com.aeriotv.android.ui.settings.SettingsDetailTopBar
import com.aeriotv.android.ui.settings.SettingsSection
import com.aeriotv.android.ui.settings.SettingsSelectionRow
import com.aeriotv.android.ui.settings.SettingsToggleRow
import com.aeriotv.android.ui.settings.dpadFocusRing
import com.aeriotv.android.ui.settings.rememberIsTvDevice
import com.aeriotv.android.ui.settings.settingsRowCard
import com.aeriotv.android.ui.theme.textAccent

/**
 * Settings > General. How the app starts, how often it refreshes in the
 * background, and the network request budget.
 *
 * Settings phase 1 regroup: Startup came from App Behaviors (Default Tab,
 * Launch, Orientation), Refresh and Network from the retired Network page.
 * Keys, control types and copy are carried over unchanged.
 */
@Composable
fun GeneralSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val isTv = rememberIsTvDevice()
    val defaultTab by viewModel.defaultTab.collectAsStateWithLifecycle(initialValue = "")
    val skipLoadingScreen by viewModel.skipLoadingScreen.collectAsStateWithLifecycle(initialValue = false)
    val autoResumeLastChannel by viewModel.autoResumeLastChannel.collectAsStateWithLifecycle(initialValue = false)
    val backgroundRefreshEnabled by viewModel.backgroundRefreshEnabled
        .collectAsStateWithLifecycle(initialValue = true)
    val backgroundRefreshIntervalMins by viewModel.backgroundRefreshIntervalMins
        .collectAsStateWithLifecycle(initialValue = 360)
    val timeoutSecs by viewModel.networkTimeoutSecs.collectAsStateWithLifecycle(initialValue = 15.0)
    val maxRetries by viewModel.maxRetries.collectAsStateWithLifecycle(initialValue = 3)

    Column(modifier = Modifier.fillMaxSize()) {
        SettingsDetailTopBar(title = "Allgemein", onBack = onBack)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Column(
                modifier = Modifier
                    .adaptiveFormWidth()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = LocalTabBarBottomInset.current,
                    ),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // MARK: Startup
                //
                // Apple phase 1 parity: Default Tab, the two launch toggles and
                // Auto-rotate are ONE section with one combined footer.
                SettingsSection(
                    header = "Start",
                    footer = "Legt fest, welcher Bereich beim App-Start angezeigt wird. Das Überspringen des Ladebildschirms kann beim Nachladen kurz ruckeln. „Letzten Sender fortsetzen“ öffnet beim Start den Player wieder, sofern der Sender noch in deiner Wiedergabeliste vorhanden ist." +
                        if (isTv) {
                            ""
                        } else {
                            " Die Vollbild-Schaltfläche des Players kann weiterhin ins Querformat wechseln, unabhängig von der Einstellung für automatische Drehung."
                        },
                ) {
                    // Search is a TV-only nav tab and not a sensible launch tab;
                    // on phones it does not exist at all. On Demand and Favorites
                    // are no longer tabs on any form factor (media center 2026-09-10).
                    AppTab.entries.filter {
                        it != AppTab.Search && it != AppTab.OnDemand && it != AppTab.Favorites
                    }.forEach { tab ->
                        val selected = (defaultTab.isEmpty() && tab == AppTab.LiveTV) ||
                            defaultTab == tab.name
                        SettingsSelectionRow(
                            label = tab.label,
                            selected = selected,
                            onClick = { viewModel.setDefaultTab(tab.name) },
                        )
                    }
                    SettingsToggleRow(
                        title = "Ladebildschirm überspringen",
                        subtitle = "Sofort Live-TV öffnen; Daten werden im Hintergrund geladen",
                        checked = skipLoadingScreen,
                        onCheckedChange = viewModel::setSkipLoadingScreen,
                    )
                    SettingsToggleRow(
                        title = "Letzten Sender fortsetzen",
                        subtitle = "Beim Start automatisch den zuletzt abgespielten Sender öffnen.",
                        checked = autoResumeLastChannel,
                        onCheckedChange = viewModel::setAutoResumeLastChannel,
                    )
                    // Auto-Rotate (Logan 2026-08-07, iOS twin): phones/tablets
                    // only - TVs have no rotation. Default ON; when off
                    // MainActivity locks the activity to its current orientation.
                    if (!isTv) {
                        val autoRotate by viewModel.autoRotate
                            .collectAsStateWithLifecycle(initialValue = true)
                        SettingsToggleRow(
                            title = "Automatisch drehen",
                            subtitle = "Geräteausrichtung übernehmen. Wenn ausgeschaltet, " +
                                "Streamy 3.0 bleibt in der aktuellen Ausrichtung",
                            checked = autoRotate,
                            onCheckedChange = viewModel::setAutoRotate,
                        )
                    }
                }

                // MARK: Refresh
                SettingsSection(
                    header = "Aktualisieren",
                    footer = "Aktualisiert Sender und EPG im WLAN im Hintergrund, solange der Akku nicht niedrig ist, damit der EPG beim Öffnen der App aktuell ist. " +
                        if (isTv) {
                            "Wenn ausgeschaltet, werden Daten nur beim Start von Streamy 3.0 oder über das Wiedergabelisten-Menü aktualisiert."
                        } else {
                            "Wenn ausgeschaltet, werden Daten nur beim Start von Streamy 3.0 oder durch Herunterziehen zum Aktualisieren geladen."
                        },
                ) {
                    SettingsToggleRow(
                        title = "Im Hintergrund aktualisieren",
                        checked = backgroundRefreshEnabled,
                        onCheckedChange = viewModel::setBackgroundRefreshEnabled,
                    )
                    if (backgroundRefreshEnabled) {
                        // iOS bgRefreshIntervalMins (audit P1 #7): how often the
                        // periodic refresh fires. Hidden when the master toggle is
                        // off so the UI doesn't suggest setting frequency on a
                        // disabled worker.
                        BG_REFRESH_INTERVAL_OPTIONS.forEach { opt ->
                            SettingsSelectionRow(
                                label = opt.label,
                                selected = opt.mins == backgroundRefreshIntervalMins,
                                onClick = { viewModel.setBackgroundRefreshIntervalMins(opt.mins) },
                            )
                        }
                    }
                }

                // MARK: Network
                //
                // tvOS Network (s_10) presents Request Timeout as a selection list
                // (5/10/15/30/60 seconds), not a slider: cleaner with a remote.
                SettingsSection(
                    header = "Netzwerk",
                    footer = "Passe die Zeitüberschreitungen bei einer langsamen oder instabilen Verbindung an.",
                ) {
                    TIMEOUT_OPTIONS.forEach { secs ->
                        SettingsSelectionRow(
                            label = if (secs == 1) "1 Sekunde" else "$secs Sekunden",
                            selected = timeoutSecs.toInt() == secs,
                            onClick = { viewModel.setNetworkTimeoutSecs(secs.toDouble()) },
                        )
                    }
                }

                // Max Retries stays a stepper (no tvOS equivalent), on a resting card.
                SettingsSection(
                    header = "Maximale Wiederholungen",
                    footer = "Maximale Wiederholungsversuche pro Anfrage (0–10).",
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .settingsRowCard(focused = false)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Retries",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            onClick = { if (maxRetries > 0) viewModel.setMaxRetries(maxRetries - 1) },
                            enabled = maxRetries > 0,
                            modifier = Modifier.dpadFocusRing(CircleShape),
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Verringern")
                        }
                        Text(
                            text = maxRetries.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.textAccent,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(
                            onClick = { if (maxRetries < 10) viewModel.setMaxRetries(maxRetries + 1) },
                            enabled = maxRetries < 10,
                            modifier = Modifier.dpadFocusRing(CircleShape),
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Erhöhen")
                        }
                    }
                }
            }
        }
    }
}

/** tvOS Request Timeout options (s_10): 5/10/15/30/60 seconds. */
private val TIMEOUT_OPTIONS: List<Int> = listOf(5, 10, 15, 30, 60)

private data class BgRefreshIntervalOption(val mins: Int, val label: String)

/** iOS bgRefreshIntervalMins picker options. 360 (6h) is the default;
 *  match the iOS picker so synced preferences round-trip cleanly. */
private val BG_REFRESH_INTERVAL_OPTIONS: List<BgRefreshIntervalOption> = listOf(
    BgRefreshIntervalOption(60, "Jede Stunde"),
    BgRefreshIntervalOption(180, "Alle 3 Stunden"),
    BgRefreshIntervalOption(360, "Alle 6 Stunden"),
    BgRefreshIntervalOption(720, "Alle 12 Stunden"),
    BgRefreshIntervalOption(1440, "Alle 24 Stunden"),
    BgRefreshIntervalOption(2880, "Alle 48 Stunden"),
)
