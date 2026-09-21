package com.aeriotv.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeriotv.android.core.preferences.PLAYER_EDGE_LEFT
import com.aeriotv.android.core.preferences.PLAYER_EDGE_RIGHT
import com.aeriotv.android.core.ui.SkipIntervals
import com.aeriotv.android.ui.adaptive.LocalTabBarBottomInset
import com.aeriotv.android.ui.adaptive.adaptiveFormWidth
import com.aeriotv.android.ui.settings.SettingsDetailTopBar
import com.aeriotv.android.ui.settings.SettingsSection
import com.aeriotv.android.ui.settings.SettingsSelectionRow
import com.aeriotv.android.ui.settings.SettingsToggleRow
import com.aeriotv.android.ui.settings.rememberIsTvDevice
import com.aeriotv.android.ui.theme.textAccent
import com.aeriotv.android.ui.tv.dpadFocusEscape
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Settings > Player. Everything about fullscreen playback: the info card, live
 * rewind, playback buffering and recovery, gestures, multiview tiles, and the
 * TV display-mode controls.
 *
 * Settings phase 1 regroup: rows came from App Behaviors (info card, rewind,
 * skip intervals, gestures, stream recovery, audio, display), Network (buffer
 * size) and the retired Multiview page. Keys, control types and copy are
 * carried over unchanged.
 */
@Composable
fun PlayerSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val isTv = rememberIsTvDevice()

    val cardChannelLogo by viewModel.playerCardShowChannelLogo
        .collectAsStateWithLifecycle(initialValue = true)
    val cardChannelName by viewModel.playerCardShowChannelName
        .collectAsStateWithLifecycle(initialValue = true)
    val cardProgramName by viewModel.playerCardShowProgramName
        .collectAsStateWithLifecycle(initialValue = true)
    val cardProgramTime by viewModel.playerCardShowProgramTime
        .collectAsStateWithLifecycle(initialValue = true)
    val cardProgramSubtitle by viewModel.playerCardShowProgramSubtitle
        .collectAsStateWithLifecycle(initialValue = true)
    val cardProgramDescription by viewModel.playerCardShowProgramDescription
        .collectAsStateWithLifecycle(initialValue = true)

    val liveRewindEnabled by viewModel.liveRewindEnabled.collectAsStateWithLifecycle(initialValue = false)
    val liveRewindDepth by viewModel.liveRewindDepthMinutes.collectAsStateWithLifecycle(initialValue = 30)
    val keepRecent by viewModel.liveRewindKeepRecent.collectAsStateWithLifecycle(initialValue = false)
    val keepCount by viewModel.liveRewindKeepCount.collectAsStateWithLifecycle(initialValue = 2)

    val skipBackSeconds by viewModel.skipBackSeconds
        .collectAsStateWithLifecycle(initialValue = SkipIntervals.DEFAULT_BACK_SECONDS)
    val skipForwardSeconds by viewModel.skipForwardSeconds
        .collectAsStateWithLifecycle(initialValue = SkipIntervals.DEFAULT_FORWARD_SECONDS)
    val bufferSize by viewModel.streamBufferSize.collectAsStateWithLifecycle(initialValue = "default")
    val autoRecoverFrozenStreams by viewModel.autoRecoverFrozenStreams
        .collectAsStateWithLifecycle(initialValue = true)
    val audioPassthrough by viewModel.audioPassthroughEnabled.collectAsStateWithLifecycle(initialValue = false)

    val appleTVChannelFlip by viewModel.appleTVChannelFlip.collectAsStateWithLifecycle(initialValue = true)
    val playerBrightnessGesture by viewModel.playerBrightnessGesture.collectAsStateWithLifecycle(initialValue = false)
    val playerVolumeGesture by viewModel.playerVolumeGesture.collectAsStateWithLifecycle(initialValue = false)
    val playerBrightnessEdge by viewModel.playerBrightnessEdge
        .collectAsStateWithLifecycle(initialValue = PLAYER_EDGE_LEFT)

    val multiviewStyle by viewModel.multiviewAudioFocusStyle.collectAsStateWithLifecycle(initialValue = "centerIcon")
    val multiviewPadding by viewModel.multiviewTilePadding.collectAsStateWithLifecycle(initialValue = false)
    val multiviewRounded by viewModel.multiviewTileCornersRounded.collectAsStateWithLifecycle(initialValue = false)

    val startupRefreshRate by viewModel.startupRefreshRate.collectAsStateWithLifecycle(initialValue = "off")
    val matchContentResolution by viewModel.matchContentResolution.collectAsStateWithLifecycle(initialValue = false)

    Column(modifier = Modifier.fillMaxSize()) {
        SettingsDetailTopBar(title = "Wiedergabe", onBack = onBack)

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
                // MARK: Info Card
                SettingsSection(
                    header = "Infokarte",
                    footer = "Wähle aus, was auf der Sendungsinfokarte im " +
                        "Player angezeigt wird, während die Bedienelemente sichtbar sind.",
                ) {
                    SettingsToggleRow(
                        title = "Senderlogo",
                        checked = cardChannelLogo,
                        onCheckedChange = viewModel::setPlayerCardShowChannelLogo,
                    )
                    SettingsToggleRow(
                        title = "Sendername",
                        checked = cardChannelName,
                        onCheckedChange = viewModel::setPlayerCardShowChannelName,
                    )
                    SettingsToggleRow(
                        title = "Sendungsname",
                        checked = cardProgramName,
                        onCheckedChange = viewModel::setPlayerCardShowProgramName,
                    )
                    SettingsToggleRow(
                        title = "Sendezeit",
                        checked = cardProgramTime,
                        onCheckedChange = viewModel::setPlayerCardShowProgramTime,
                    )
                    SettingsToggleRow(
                        title = "Sendungsuntertitel",
                        checked = cardProgramSubtitle,
                        onCheckedChange = viewModel::setPlayerCardShowProgramSubtitle,
                    )
                    SettingsToggleRow(
                        title = "Sendungsbeschreibung",
                        checked = cardProgramDescription,
                        onCheckedChange = viewModel::setPlayerCardShowProgramDescription,
                    )
                }

                // MARK: Live Rewind
                SettingsSection(
                    header = "Live-Zurückspulen",
                    footer = "Puffert den aktuell angesehenen Sender, damit du Live-TV pausieren und " +
                        "zurückspulen kannst. Während der Wiedergabe wird Gerätespeicher verwendet; gepufferte " +
                        "Videodaten werden automatisch gelöscht.",
                ) {
                    SettingsToggleRow(
                        title = "Live-TV pausieren & zurückspulen",
                        subtitle = "Live-Wiedergabe im Vollbild auf diesem Gerät puffern",
                        checked = liveRewindEnabled,
                        onCheckedChange = viewModel::setLiveRewindEnabled,
                    )
                }
                if (liveRewindEnabled) {
                    SettingsSection(
                        header = "Verfügbar halten",
                        footer = "Legt fest, wie weit du beim aktuell angesehenen Sender " +
                            "zurückspulen kannst. Gepufferte Videodaten werden freigegeben, sobald du " +
                            "den Sender verlässt. " +
                            depthEstimateText(liveRewindDepth),
                    ) {
                        SteppedSliderRow(
                            label = "Zurückspulen bis",
                            values = REWIND_DEPTH_MINUTES,
                            selected = liveRewindDepth,
                            format = ::formatDepthMinutes,
                            onSelect = viewModel::setLiveRewindDepthMinutes,
                        )
                    }
                    SettingsSection(
                        header = "Letzte Sender live halten",
                        footer = if (keepRecent) {
                            "Verlassene Sender werden weiter gepuffert, damit du zurückwechseln " +
                                "und über die Zeit deiner Abwesenheit zurückspulen kannst. Jeder gehaltene " +
                                "Sender benötigt eine zusätzliche Verbindung zu deinem Anbieter; Konten " +
                                "mit nur einer Verbindung sollten dies ausgeschaltet lassen. Die ältesten " +
                                "Sender werden zuerst beendet; alle werden beendet, wenn die App diese Ansicht verlässt."
                        } else {
                            "Puffert die zuletzt verlassenen Sender weiter " +
                                "und benötigt pro gehaltenem Sender eine zusätzliche Anbieter-Verbindung."
                        },
                    ) {
                        SettingsToggleRow(
                            title = "Letzte Sender live halten",
                            subtitle = "Verlassene Sender im Hintergrund weiter puffern",
                            checked = keepRecent,
                            onCheckedChange = viewModel::setLiveRewindKeepRecent,
                        )
                        if (keepRecent) {
                            SteppedSliderRow(
                                label = "Anzahl der Sender",
                                values = listOf(1, 2, 3, 4, 5),
                                selected = keepCount,
                                format = { it.toString() },
                                onSelect = viewModel::setLiveRewindKeepCount,
                            )
                        }
                    }
                }

                // MARK: Playback
                //
                // Apple phase 1 parity: skip intervals, buffer size and stream
                // recovery share one section.
                SettingsSection(
                    header = "Wiedergabe",
                    footer = (
                        if (isTv) {
                            "Legt fest, wie weit die Sprungtasten sowie ein einzelner Druck auf Links oder Rechts " +
                                "bei Live-Zurückspulen, Catch-up, Aufnahmen, Filmen und Serien springen. " +
                                "Gedrückthalten spult weiterhin zunehmend schneller."
                        } else {
                            "Legt fest, wie weit die Sprungtasten bei Live-Zurückspulen, Catch-up, " +
                                "Aufnahmen, Filmen und Serien springen – einschließlich Cast-Fernbedienung " +
                                "und Wiedergabebenachrichtigung."
                        }
                        ) + " Die Puffergröße bestimmt, wie viele Streamdaten vorgeladen werden: größere " +
                        "Puffer verringern Ruckler bei schlechten Verbindungen, verlängern aber den Start. " +
                        "Wenn ein Live-Stream kein Bild mehr liefert, lädt die automatische Wiederherstellung " +
                        "ihn neu. Schalte sie aus, falls Sender während Werbepausen neu starten oder ruckeln. " +
                        "Die Änderung gilt ab dem nächsten Senderwechsel.",
                ) {
                    SteppedSliderRow(
                        label = "Zurückspringen",
                        values = SkipIntervals.CHOICES,
                        selected = skipBackSeconds,
                        format = ::formatSkipSeconds,
                        onSelect = viewModel::setSkipBackSeconds,
                    )
                    SteppedSliderRow(
                        label = "Vorspringen",
                        values = SkipIntervals.CHOICES,
                        selected = skipForwardSeconds,
                        format = ::formatSkipSeconds,
                        onSelect = viewModel::setSkipForwardSeconds,
                    )
                    BUFFER_OPTIONS.forEach { opt ->
                        SettingsSelectionRow(
                            label = opt.label,
                            subtitle = opt.detail,
                            selected = opt.id == bufferSize,
                            onClick = { viewModel.setStreamBufferSize(opt.id) },
                        )
                    }
                    SettingsToggleRow(
                        title = "Hängende Streams automatisch neu laden",
                        subtitle = "Lädt einen Live-Stream neu, wenn kein Video mehr kommt. Aus lässt den Stream bei kurzen Werbepausen unverändert.",
                        checked = autoRecoverFrozenStreams,
                        onCheckedChange = viewModel::setAutoRecoverFrozenStreams,
                    )
                }

                SettingsSection(
                    header = "Audio",
                    footer = "Die Surround-Durchleitung sendet Mehrkanalton unverändert an Fernseher oder Receiver. Einige Fernseher verarbeiten ihn verzögert, wodurch Bild und Ton bei Live-TV auseinanderlaufen können. Wenn ausgeschaltet, decodiert Streamy das Audio selbst. Die Änderung gilt ab der nächsten Wiedergabe.",
                ) {
                    SettingsToggleRow(
                        title = "Surround-Sound-Durchleitung",
                        subtitle = "AC-3- und E-AC-3-Audio unverändert an den Receiver senden. Ausschalten, wenn Bild und Ton auseinanderlaufen.",
                        checked = audioPassthrough,
                        onCheckedChange = viewModel::setAudioPassthroughEnabled,
                    )
                }

                // MARK: Gestures
                SettingsSection(
                    header = "Gesten",
                    // tvOS / Android TV flip channels with D-pad up/down, not a
                    // swipe, so the "accidental swipes" caution is meaningless on
                    // a remote (user request: drop the note on TV). Phones keep it.
                    footer = if (isTv) {
                        null
                    } else {
                        "Ausschalten, wenn versehentliches Wischen während der Wiedergabe unbeabsichtigt Sender wechselt. " +
                            "Helligkeits- und Lautstärkegesten werden nur in einem schmalen Bereich am Bildschirmrand erkannt, " +
                            "damit sie nicht mit Senderwechsel oder Herunterwischen zum Minimieren kollidieren."
                    },
                ) {
                    SettingsToggleRow(
                        title = "Senderwechsel mit Hoch/Runter",
                        subtitle = if (isTv) {
                            "Während die Player-Steuerung sichtbar ist, wechselt Hoch zum nächsten und Runter zum vorherigen Sender. Nur bei Live-Einzelstream-Wiedergabe."
                        } else {
                            "Während die Player-Steuerung sichtbar ist, nach oben für den nächsten und nach unten für den vorherigen Sender wischen. Nur bei Live-Einzelstream-Wiedergabe."
                        },
                        checked = appleTVChannelFlip,
                        onCheckedChange = viewModel::setAppleTVChannelFlip,
                    )
                    if (!isTv) {
                        SettingsToggleRow(
                            title = "Helligkeit am Bildschirmrand",
                            subtitle = "Am Helligkeitsrand nach oben oder unten wischen, um das Bild heller oder dunkler zu stellen. Gilt nur in dieser App.",
                            checked = playerBrightnessGesture,
                            onCheckedChange = viewModel::setPlayerBrightnessGesture,
                        )
                        SettingsToggleRow(
                            title = "Lautstärke am Bildschirmrand",
                            subtitle = "Am anderen Rand nach oben oder unten wischen, um die Medienlautstärke zu ändern.",
                            checked = playerVolumeGesture,
                            onCheckedChange = viewModel::setPlayerVolumeGesture,
                        )
                        if (playerBrightnessGesture || playerVolumeGesture) {
                            listOf(
                                PLAYER_EDGE_LEFT to "Helligkeit: links, Lautstärke: rechts",
                                PLAYER_EDGE_RIGHT to "Helligkeit: rechts, Lautstärke: links",
                            ).forEach { (wire, label) ->
                                SettingsSelectionRow(
                                    label = label,
                                    selected = playerBrightnessEdge == wire,
                                    onClick = { viewModel.setPlayerBrightnessEdge(wire) },
                                )
                            }
                        }
                    }
                }

                // MARK: Multiview
                //
                // Apple phase 1 parity: indicator, spacing and tile corners
                // share one section. tvOS presents corners as a Square /
                // Rounded selection (s_09); same underlying Boolean.
                SettingsSection(
                    header = "Mehrfachansicht",
                    footer = "Legt fest, wie die Mehrfachansicht die aktive Ton-Kachel markiert. Das mittlere Symbol blendet mit der Steuerung aus, die graue Umrandung bleibt sichtbar und die Akzent-Umrandung erscheint beim Wechsel und verschwindet nach 5 Sekunden. Ein Abstand trennt die Kacheln optisch voneinander.",
                ) {
                    AUDIO_FOCUS_OPTIONS.forEach { opt ->
                        SettingsSelectionRow(
                            label = opt.label,
                            subtitle = opt.detail,
                            selected = multiviewStyle == opt.id,
                            onClick = { viewModel.setMultiviewAudioFocusStyle(opt.id) },
                        )
                    }
                    SettingsToggleRow(
                        title = "Abstand zwischen Kacheln",
                        subtitle = "Kleinen Abstand zwischen Kacheln für bessere Trennung hinzufügen.",
                        checked = multiviewPadding,
                        onCheckedChange = viewModel::setMultiviewTilePadding,
                    )
                    SettingsSelectionRow(
                        label = "Eckig",
                        selected = !multiviewRounded,
                        onClick = { viewModel.setMultiviewTileCornersRounded(false) },
                    )
                    SettingsSelectionRow(
                        label = "Abgerundet",
                        selected = multiviewRounded,
                        onClick = { viewModel.setMultiviewTileCornersRounded(true) },
                    )
                }

                // MARK: Display (TV only)
                //
                // GH #38 + #40: each switch is a real HDMI mode change (brief
                // black screen), deliberate and user-opted.
                if (isTv) {
                    SettingsSection(
                        header = "Anzeige",
                        footer = "Die Start-Bildwiederholrate stellt die Anzeige beim App-Start einmal um, damit sie schon vor dem ersten Sender zur Haupt-Bildrate passt. „Inhaltsauflösung anpassen“ gibt den Stream in seiner Auflösung aus, damit der Fernseher hochskaliert; beim Wechsel kann der Bildschirm kurz blinken.",
                    ) {
                        SettingsToggleRow(
                            title = "Inhaltsauflösung anpassen",
                            subtitle = "1080p-Streams in 1080p ausgeben und den Fernseher hochskalieren lassen. Aus behält den nativen Anzeigemodus.",
                            checked = matchContentResolution,
                            onCheckedChange = viewModel::setMatchContentResolution,
                        )
                        listOf(
                            "off" to "Aus (Systemstandard)",
                            "50" to "50 Hz",
                            "59.94" to "59.94 Hz",
                            "60" to "60 Hz",
                        ).forEach { (wire, label) ->
                            SettingsSelectionRow(
                                label = label,
                                selected = startupRefreshRate == wire,
                                onClick = { viewModel.setStartupRefreshRate(wire) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class AudioFocusOption(val id: String, val label: String, val detail: String)

private val AUDIO_FOCUS_OPTIONS: List<AudioFocusOption> = listOf(
    AudioFocusOption("centerIcon", "Mittiges Symbol", "Lautsprechersymbol mittig auf der aktiven Kachel. Standard."),
    AudioFocusOption("grayPersistent", "Graue Umrandung", "Dezente graue Umrandung dauerhaft um die aktive Kachel."),
    AudioFocusOption("themeFading", "Akzent-Umrandung (ausblendend)", "Akzentfarbene Umrandung, die nach 5 Sekunden automatisch ausgeblendet wird."),
)

data class BufferOption(val id: String, val label: String, val detail: String, val cachingMs: Int)

/**
 * Discord (di5cord20, Formuler Z11, 2026-08-09): "Default -> Small made
 * zapping worse." It cannot have. The live player applies
 * `minBufferMs = maxOf(4_000, bufferFloorMs)` (AerioExoPlayerHolder), and the
 * old ladder was 300 / 1000 / 3000 / 8000 ms, so **Small, Default and Large
 * all collapsed onto the same 4s/8s LoadControl**: three of the four options
 * did nothing at all, while their subtitles advertised latencies (300 ms,
 * 1 second, 3 seconds) the player never used. Extra Large was worse than
 * inert - 8000 produced min == max == 8s, a LoadControl with no headroom
 * between its two bounds.
 *
 * The 4s floor is not an accident: it is the measured fix for the recurring
 * mid-stream micro-stutter on the Streamer (see the LoadControl comment in
 * AerioExoPlayerHolder, which walks through why 500ms and 2.5s both failed).
 * Nothing may sit below it, which leaves no room for a real "Small" - so it
 * is gone, and anyone on it is mapped to Default, which is exactly what they
 * were already getting.
 *
 * Default keeps its current behaviour to the millisecond, so no existing
 * install changes. Large and Extra Large start doing what their labels always
 * claimed.
 */
internal val BUFFER_OPTIONS: List<BufferOption> = listOf(
    BufferOption("default", "Standard", "4 Sekunden – empfohlen", 4_000),
    BufferOption("large", "Groß", "8 Sekunden – instabile Verbindungen", 8_000),
    BufferOption("xlarge", "Sehr groß", "16 Sekunden – sehr schlechte Verbindungen", 16_000),
)

/** Unknown ids (including the retired "small") resolve to Default. */
internal fun bufferMillisFor(id: String): Int =
    BUFFER_OPTIONS.firstOrNull { it.id == id }?.cachingMs ?: 4_000

/** Keep Available ladder (2026-07-11 rework, user directive round 2:
 *  the user-meaningful knob is HOW FAR BACK you can rewind, not how
 *  long files persist - retention is now a fixed internal 1 hour). */
private val REWIND_DEPTH_MINUTES = listOf(15, 30, 60, 90, 120, 180)

private fun formatDepthMinutes(mins: Int): String = when {
    mins < 60 -> "$mins Minuten"
    mins == 60 -> "1 Stunde"
    mins % 60 == 0 -> "${mins / 60} Stunden"
    else -> "${mins / 60} Std. ${mins % 60} Min."
}

/** Skip Intervals value readout: "10 seconds". */
private fun formatSkipSeconds(seconds: Int): String = "$seconds Sekunden"

/**
 * Storage estimate under the Keep Available slider: scales with the
 * depth choice (which bounds live disk usage now that retention is a
 * fixed short window) at typical stream bitrates - HD ~4 Mbps, FHD ~8,
 * UHD ~20 - so the user can pick what fits their streams and disk.
 */
private fun depthEstimateText(mins: Int): String {
    fun gb(mbps: Int): String {
        val v = mbps * 7.5 * mins / 1024.0
        return if (v < 10) String.format("~%.1f GB", v) else "~${v.roundToInt()} GB"
    }
    return "Benötigt beim Ansehen bis zu ${gb(4)} bei HD, ${gb(8)} bei FHD oder ${gb(20)} bei UHD."
}

/**
 * Discrete-stop slider row: label left, current value right, a stepped
 * Material slider beneath. TV-safe via [dpadFocusEscape] (the #90
 * lesson: UP/DOWN must move focus off the slider, LEFT/RIGHT adjust).
 */
@Composable
internal fun SteppedSliderRow(
    label: String,
    values: List<Int>,
    selected: Int,
    format: (Int) -> String,
    onSelect: (Int) -> Unit,
) {
    // Snap legacy/custom persisted values (e.g. 48h from the removed
    // custom dialog) to the nearest ladder stop for display; the pref
    // itself is only rewritten when the user moves the slider.
    val idx = values.indices.minByOrNull { abs(values[it] - selected) } ?: 0
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = format(values[idx]),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.textAccent,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Slider(
            value = idx.toFloat(),
            onValueChange = { raw ->
                val newIdx = raw.roundToInt().coerceIn(0, values.lastIndex)
                if (values[newIdx] != selected) onSelect(values[newIdx])
            },
            valueRange = 0f..values.lastIndex.toFloat(),
            steps = (values.size - 2).coerceAtLeast(0),
            modifier = Modifier.dpadFocusEscape(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}
