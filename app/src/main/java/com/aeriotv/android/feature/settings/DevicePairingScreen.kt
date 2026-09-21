package com.aeriotv.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeriotv.android.core.pairing.DevicePairingTransport
import com.aeriotv.android.feature.playlist.PlaylistViewModel
import com.aeriotv.android.ui.adaptive.LocalTabBarBottomInset
import com.aeriotv.android.ui.adaptive.adaptiveFormWidth
import com.aeriotv.android.ui.settings.SettingsActionRow
import com.aeriotv.android.ui.settings.SettingsDetailTopBar
import com.aeriotv.android.ui.settings.SettingsInfoRow
import com.aeriotv.android.ui.settings.SettingsSection
import com.aeriotv.android.ui.settings.rememberIsTvDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * Account-free playlist transfer between Streamy devices.
 *
 * TV: starts a receiver and shows a six-digit one-time code.
 * Phone/tablet: enters that code and sends the currently active playlist.
 * Discovery stays on the local network; the playlist payload is authenticated
 * with J-PAKE and encrypted with AES-GCM before credentials leave the sender.
 */
@Composable
fun DevicePairingScreen(
    onBack: () -> Unit,
    viewModel: PlaylistViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isTv = rememberIsTvDevice()
    val scope = rememberCoroutineScope()
    var code by rememberSaveable { mutableStateOf("") }
    var receiveCode by rememberSaveable { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    fun importPayload(raw: String) {
        runCatching {
            val json = JSONObject(raw)
            val url = json.optString("url").trim()
            require(url.isNotEmpty())
            viewModel.importPairedPlaylist(
                sourceTypeName = json.optString("sourceType", "M3uUrl"),
                name = json.optString("name").takeIf { it.isNotBlank() },
                url = url,
                lanUrl = json.optString("lanUrl").takeIf { it.isNotBlank() },
                epgUrl = json.optString("epgUrl").takeIf { it.isNotBlank() },
                apiKey = json.optString("apiKey").takeIf { it.isNotBlank() },
                username = json.optString("username").takeIf { it.isNotBlank() },
                password = json.optString("password").takeIf { it.isNotBlank() },
                vodEnabled = json.optBoolean("vodEnabled", true),
                epgRetentionDays = json.optInt("epgRetentionDays", 7),
            )
        }.onSuccess {
            status = "Wiedergabeliste empfangen. Streamy lädt sie jetzt."
            receiveCode = ""
            busy = false
        }.onFailure {
            status = "Die empfangenen Daten konnten nicht übernommen werden."
            busy = false
        }
    }

    fun startReceiving() {
        DevicePairingTransport.stop()
        status = null
        val pin = DevicePairingTransport.startReceiver { payload ->
            scope.launch { importPayload(payload) }
        }
        if (pin == null) {
            receiveCode = ""
            status = "Kopplung konnte nicht gestartet werden."
        } else {
            receiveCode = pin
            status = "Code ist 2 Minuten gültig. Beide Geräte müssen im gleichen WLAN sein."
        }
    }

    DisposableEffect(Unit) {
        onDispose { DevicePairingTransport.stop() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SettingsDetailTopBar(title = "Geräte koppeln", onBack = onBack)
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
                if (isTv) {
                    SettingsSection(
                        header = "Auf diesem Fernseher empfangen",
                        footer = "Auf dem Handy Streamy öffnen → Einstellungen → Geräte koppeln. " +
                            "Den Code vom Fernseher eingeben und die Wiedergabeliste senden.",
                    ) {
                        if (receiveCode.isNotBlank()) {
                            SettingsInfoRow(label = "Code", value = receiveCode)
                        }
                        SettingsActionRow(
                            label = if (receiveCode.isBlank()) "Code anzeigen" else "Neuen Code erzeugen",
                            leadingIcon = Icons.Filled.Refresh,
                            onClick = { startReceiving() },
                        )
                    }
                } else {
                    val playlist = state.playlist
                    SettingsSection(
                        header = "An Fernseher senden",
                        footer = "Am Fernseher Streamy öffnen → Einstellungen → Geräte koppeln → Code anzeigen. " +
                            "Die Übertragung läuft direkt im gleichen WLAN und benötigt kein Google-Konto.",
                    ) {
                        SettingsInfoRow(
                            label = "Wiedergabeliste",
                            value = playlist?.name?.takeIf { it.isNotBlank() } ?: "Keine ausgewählt",
                        )
                        OutlinedTextField(
                            value = code,
                            onValueChange = { value ->
                                code = value.filter(Char::isDigit).take(6)
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                            label = { Text("6-stelliger Code vom Fernseher") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        )
                        SettingsActionRow(
                            label = if (busy) "Wird gesendet…" else "Wiedergabeliste senden",
                            leadingIcon = Icons.Filled.Upload,
                            enabled = !busy && code.length == 6 && playlist != null,
                            onClick = {
                                val current = state.playlist ?: return@SettingsActionRow
                                val payload = JSONObject()
                                    .put("v", 1)
                                    .put("sourceType", current.sourceType)
                                    .put("name", current.name.orEmpty())
                                    .put("url", current.urlString)
                                    .put("lanUrl", current.lanUrlString.orEmpty())
                                    .put("epgUrl", current.epgUrl.orEmpty())
                                    .put("apiKey", current.apiKey.orEmpty())
                                    .put("username", current.username.orEmpty())
                                    .put("password", current.password.orEmpty())
                                    .put("vodEnabled", current.vodEnabled)
                                    .put("epgRetentionDays", current.epgRetentionDays)
                                    .toString()
                                busy = true
                                status = "Fernseher wird gesucht…"
                                scope.launch {
                                    val sent = withContext(Dispatchers.IO) {
                                        DevicePairingTransport.send(code, payload, 30_000)
                                    }
                                    busy = false
                                    status = if (sent) {
                                        code = ""
                                        "Wiedergabeliste wurde sicher an den Fernseher übertragen."
                                    } else {
                                        "Fernseher nicht gefunden. Prüfe WLAN und Code und versuche es erneut."
                                    }
                                }
                            },
                        )
                    }
                }

                status?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }

                SettingsSection(
                    header = "Datenschutz",
                    footer = "Der sechsstellige Code wird nicht übertragen. Die Zugangsdaten werden erst " +
                        "nach erfolgreicher Code-Prüfung verschlüsselt zwischen den beiden Geräten übertragen. " +
                        "Es gibt keine Google-Anmeldung und keinen Cloud-Speicher.",
                ) {
                    SettingsInfoRow(label = "Verbindung", value = "Lokales WLAN")
                    SettingsInfoRow(label = "Schutz", value = "J-PAKE + AES-GCM")
                }
            }
        }
    }
}
