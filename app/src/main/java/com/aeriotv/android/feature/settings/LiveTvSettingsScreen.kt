package com.aeriotv.android.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeriotv.android.core.category.CategoryPaletteState
import com.aeriotv.android.core.category.ProgramCategory
import com.aeriotv.android.core.tv.TvActionMenuDialog
import com.aeriotv.android.core.tv.TvMenuAction
import com.aeriotv.android.core.tv.rememberTvMenuGuard
import com.aeriotv.android.ui.adaptive.LocalTabBarBottomInset
import com.aeriotv.android.ui.adaptive.rememberViewport
import com.aeriotv.android.ui.settings.SettingsDetailTopBar
import com.aeriotv.android.ui.settings.SettingsRowContainer
import com.aeriotv.android.ui.settings.SettingsSection
import com.aeriotv.android.ui.settings.SettingsSelectionRow
import com.aeriotv.android.ui.settings.SettingsToggleRow
import com.aeriotv.android.ui.settings.dpadFocusWash
import com.aeriotv.android.ui.settings.rememberIsTvDevice
import com.aeriotv.android.ui.theme.textAccent

/**
 * Settings > Live TV. Everything that shapes the guide and the channel list:
 * presentation toggles, the List view choice, the TV guide layout, groups,
 * program badges, the Live TV display scale, and the category colors.
 *
 * Settings phase 1 regroup: the rows here came from App Behaviors (default
 * view, groups, guide layout, badges), Appearance (presentation, artwork,
 * display scale, category colors and palette) and, on TV, the Group Selection
 * row that used to sit under Remote Control. Every persisted key, control type
 * and string is carried over unchanged.
 */
@Composable
fun LiveTvSettingsScreen(
    onBack: () -> Unit,
    onOpenAddMoreCategories: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val isTv = rememberIsTvDevice()
    // TV has no Live TV List view (Logan 2026-09-16, see core.ui.TvListView),
    // so the list-only options are hidden and the list-facing copy is written
    // for the Guide's channel column instead. The keys keep their values.
    val listViewShown = !isTv || com.aeriotv.android.core.ui.TvListView.ENABLED

    val showChannelLogos by viewModel.showChannelLogos.collectAsStateWithLifecycle(initialValue = true)
    val showChannelNumbers by viewModel.showChannelNumbers.collectAsStateWithLifecycle(initialValue = true)
    val showChannelNames by viewModel.showChannelNames.collectAsStateWithLifecycle(initialValue = true)
    val showProgramSubtitles by viewModel.showProgramSubtitles.collectAsStateWithLifecycle(initialValue = true)
    val roundedArtwork by viewModel.roundedArtwork.collectAsStateWithLifecycle(initialValue = true)
    val roundedArtworkGuide by viewModel.roundedArtworkGuide.collectAsStateWithLifecycle(initialValue = false)
    val defaultLiveTVView by viewModel.defaultLiveTVView.collectAsStateWithLifecycle(initialValue = "")
    val liveTvLayout by viewModel.liveTvLayout.collectAsStateWithLifecycle(initialValue = "basic")
    val defaultGroupToken by viewModel.defaultGroupToken.collectAsStateWithLifecycle()
    val defaultGroupOptions by viewModel.defaultGroupOptions.collectAsStateWithLifecycle()
    val phoneGroupSelector by viewModel.phoneGroupSelector.collectAsStateWithLifecycle(initialValue = "sidebar")
    val guideGroupSelector by viewModel.guideGroupSelector.collectAsStateWithLifecycle(initialValue = "pills")
    val showEpgBadges by viewModel.showEpgBadges(isTv).collectAsStateWithLifecycle(initialValue = true)
    val hiddenEpgBadges by viewModel.hiddenEpgBadges.collectAsStateWithLifecycle(initialValue = emptySet())
    val scaleLiveTV by viewModel.displayScaleLiveTV.collectAsStateWithLifecycle(initialValue = 1.0f)
    val palette by viewModel.categoryPalette.collectAsStateWithLifecycle(initialValue = CategoryPaletteState.Default)

    var editingGroupSelector by remember { mutableStateOf(false) }
    var pickerTarget by remember { mutableStateOf<ProgramCategory?>(null) }
    val menuGuard = rememberTvMenuGuard()

    Column(modifier = Modifier.fillMaxSize()) {
        SettingsDetailTopBar(title = "Live-TV", onBack = onBack)

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
                // MARK: Guide Presentation
                item("guide-presentation") {
                    SettingsSection(
                        header = "EPG-Darstellung",
                        footer = (
                            if (listViewShown) {
                                "Schalte Logos oder Nummern aus, damit lange Sendernamen mehr Platz haben. Gilt für die Live-TV-Liste und den EPG."
                            } else {
                                "Schalte Logos oder Nummern aus, damit lange Sendernamen in der Sender-Spalte des EPG mehr Platz haben."
                            }
                            ) + " Abgerundete Ecken passen Senderlogos und Sendungsbilder an die Form der Karte oder Zelle an. " +
                            "Bilder mit transparentem Hintergrund bleiben unabhängig davon eckig.",
                    ) {
                        SettingsToggleRow(
                            title = "Senderlogos anzeigen",
                            subtitle = if (listViewShown) {
                                "Senderlogo in der Live-TV-Liste anzeigen."
                            } else {
                                "Senderlogo in der Sender-Spalte des EPG anzeigen."
                            },
                            checked = showChannelLogos,
                            onCheckedChange = viewModel::setShowChannelLogos,
                        )
                        SettingsToggleRow(
                            title = "Sendernummern anzeigen",
                            subtitle = if (listViewShown) {
                                "Sendernummer in der Live-TV-Liste und im EPG anzeigen."
                            } else {
                                "Sendernummer in der Sender-Spalte des EPG anzeigen."
                            },
                            checked = showChannelNumbers,
                            onCheckedChange = viewModel::setShowChannelNumbers,
                        )
                        SettingsToggleRow(
                            title = "Sendernamen anzeigen",
                            subtitle = "Sendernamen in der Sender-Spalte des EPG anzeigen.",
                            checked = showChannelNames,
                            onCheckedChange = viewModel::setShowChannelNames,
                        )
                        SettingsToggleRow(
                            title = "Sendungsuntertitel anzeigen",
                            subtitle = if (listViewShown) {
                                "Folgen- oder Spielnamen unter dem Sendungstitel im EPG und in der Live-TV-Liste anzeigen. Ausschalten, wenn dein EPG dort nur die Beschreibung wiederholt."
                            } else {
                                "Folgen- oder Spielnamen unter dem Sendungstitel im EPG anzeigen. Ausschalten, wenn dein EPG dort nur die Beschreibung wiederholt."
                            },
                            checked = showProgramSubtitles,
                            onCheckedChange = viewModel::setShowProgramSubtitles,
                        )
                        SettingsToggleRow(
                            title = "Abgerundete Ecken in der EPG-Ansicht",
                            checked = roundedArtworkGuide,
                            onCheckedChange = viewModel::setRoundedArtworkGuide,
                        )
                    }
                }

                // MARK: List view
                //
                // Hidden on TV under the TvListView gate, exactly as the two
                // source screens gated these rows before the regroup.
                if (listViewShown) item("list-view") {
                    SettingsSection(
                        header = "Listenansicht",
                        footer = "Legt fest, in welcher Ansicht Live-TV geöffnet wird. Automatisch verwendet die Liste auf " +
                            "Handys und den EPG auf Fernsehern und größeren Tablets. Auf Handys kannst du " +
                            "für die aktuelle Sitzung weiterhin über die Schaltfläche Liste / EPG wechseln; " +
                            "auf dem Fernseher ist diese Einstellung der Umschalter.",
                    ) {
                        SettingsToggleRow(
                            title = "Abgerundete Ecken in der Listenansicht",
                            checked = roundedArtwork,
                            onCheckedChange = viewModel::setRoundedArtwork,
                        )
                        val current = defaultLiveTVView.lowercase()
                        DEFAULT_LIVE_TV_VIEW_OPTIONS.forEach { (value, label) ->
                            SettingsSelectionRow(
                                label = label,
                                selected = current == value,
                                onClick = { viewModel.setDefaultLiveTVView(value) },
                            )
                        }
                    }
                }

                // MARK: Guide Layout (TV)
                if (isTv) item("guide-layout") {
                    SettingsSection(header = "EPG-Layout") {
                        SettingsSelectionRow(
                            label = "Einfach",
                            subtitle = "Vollständige Sendungsdetails in jeder EPG-Zelle",
                            selected = liveTvLayout != "preview",
                            onClick = { viewModel.setLiveTvLayout("basic") },
                        )
                        SettingsSelectionRow(
                            label = "Sendervorschau",
                            subtitle = "Ein Banner zeigt die markierte Sendung; Zellen behalten Titel und Markierungen",
                            selected = liveTvLayout == "preview",
                            onClick = { viewModel.setLiveTvLayout("preview") },
                        )
                    }
                }

                // MARK: Groups
                item("groups") {
                    SettingsSection(
                        header = "Gruppen",
                        footer = "Die Live-TV-Gruppe, die beim Öffnen dieser Wiedergabeliste angezeigt wird. " +
                            "„Zuletzt angesehen“ enthält die letzten 25 abgespielten Sender, die neuesten " +
                            "zuerst. Bei Auswahl wird sie auch unter „Gruppen verwalten“ angezeigt. Eine Gruppe, " +
                            "die später aus der Wiedergabeliste verschwindet, wird automatisch " +
                            "auf eine verfügbare Gruppe zurückgesetzt.",
                    ) {
                        val fixedTokens = listOf(
                            com.aeriotv.android.feature.playlist.PlaylistViewModel.ALL_GROUPS,
                            com.aeriotv.android.feature.playlist.PlaylistViewModel.FAVORITES_GROUP,
                            com.aeriotv.android.feature.playlist.PlaylistViewModel.RECENT_GROUP,
                        )
                        (fixedTokens + defaultGroupOptions).forEach { token ->
                            val isAll =
                                token == com.aeriotv.android.feature.playlist.PlaylistViewModel.ALL_GROUPS
                            SettingsSelectionRow(
                                label = com.aeriotv.android.feature.livetv.groupDisplayName(token),
                                // Nothing stored reads as All Channels, which is
                                // where a first launch lands.
                                selected = defaultGroupToken == token ||
                                    (isAll && defaultGroupToken.isBlank()),
                                onClick = { viewModel.setDefaultGroupToken(token) },
                            )
                        }
                    }
                }

                item("group-selection") {
                    if (isTv) {
                        // Moved here from Remote Control (Settings phase 1). Same
                        // row, same dialog, same persisted key.
                        SettingsSection(
                            header = "Gruppenauswahl",
                            footer = "Legt fest, wie Sendergruppen im EPG ausgewählt werden. Gruppen-Schaltflächen bleiben oberhalb des EPG; das Seitenleisten-Menü blendet diese Zeile aus und öffnet sich durch Gedrückthalten von Links im EPG. Es kann jeweils nur eine Variante aktiv sein.",
                        ) {
                            GroupSelectionRow(
                                slotName = "Gruppenauswahl",
                                valueName = if (guideGroupSelector == "sidebar") "Seitenleisten-Menü" else "Gruppen-Schaltflächen oben",
                                onClick = { editingGroupSelector = true },
                            )
                        }
                    } else {
                        SettingsSection(
                            header = "Gruppenauswahl",
                            footer = "Legt fest, wie Live-TV eine Sendergruppe auswählt. „Seitenleiste“ öffnet eine " +
                                "Gruppenliste über die Schaltfläche oben; „Schaltflächen“ zeigt die Gruppen " +
                                "als Leiste im Kopfbereich an.",
                        ) {
                            SettingsSelectionRow(
                                label = "Seitenleiste",
                                selected = phoneGroupSelector != "pills",
                                onClick = { viewModel.setPhoneGroupSelector("sidebar") },
                            )
                            SettingsSelectionRow(
                                label = "Schaltflächen",
                                selected = phoneGroupSelector == "pills",
                                onClick = { viewModel.setPhoneGroupSelector("pills") },
                            )
                        }
                    }
                }

                // MARK: Badges
                item("badges") {
                    SettingsSection(
                        header = "Markierungen",
                        footer = "Sendungsmarkierungen sind LIVE, NEU, PREMIERE, FINALE, " +
                            "WIEDERHOLUNG sowie Staffel-/Episodenmarkierungen im EPG und in der Sender" +
                            "liste. Die Einstellung wird getrennt gespeichert für " +
                            (if (isTv) "Fernseher" else "Handys und Tablets") +
                            " und getrennt für deine " +
                            (if (isTv) "Fernseher" else "Mobilgeräte") + " verwendet.",
                    ) {
                        SettingsToggleRow(
                            title = "Sendungsmarkierungen anzeigen",
                            subtitle = "LIVE, NEU und Staffel-/Episodenmarkierungen im EPG",
                            checked = showEpgBadges,
                            onCheckedChange = { viewModel.setShowEpgBadges(isTv, it) },
                        )
                        if (showEpgBadges) {
                            for (badge in listOf("NEW", "REPEAT", "LIVE", "PREMIERE", "FINALE")) {
                                SettingsToggleRow(
                                    title = "${badge.first()}${badge.drop(1).lowercase()}-Markierung",
                                    checked = badge !in hiddenEpgBadges,
                                    onCheckedChange = { on ->
                                        viewModel.setBadgeHidden(badge, hidden = !on)
                                    },
                                )
                            }
                        }
                    }
                }

                // MARK: Display Scale
                settingsCard(
                    header = "Darstellungsgröße",
                    footer = if (listViewShown) {
                        "Unabhängige Größe für die Live-TV-Liste. 100 % entspricht dem Standard; mit 85–175 % kannst du zwischen mehr Inhalt und besserer Lesbarkeit wählen. Ab 150 % werden weniger, dafür größere Einträge angezeigt. Änderungen gelten sofort."
                    } else {
                        "Unabhängige Größe für Live-TV. 100 % entspricht dem Standard; mit 85–175 % kannst du zwischen mehr Inhalt und besserer Lesbarkeit wählen. Ab 150 % werden weniger, dafür größere Einträge angezeigt. Änderungen gelten sofort."
                    },
                ) {
                    ScaleSliderRow(
                        label = if (listViewShown) "Live-TV-Liste" else "Live-TV",
                        value = scaleLiveTV,
                        onValueChange = viewModel::setDisplayScaleLiveTV,
                    )
                }

                // MARK: Colors
                settingsCard(
                    header = "Farben",
                    footer = "EPG-Zellen und Senderkarten nach Sendungskategorie einfärben. Wähle unten eine Kategorie, um ihren Hex-Farbwert anzupassen.",
                ) {
                    ToggleRow(
                        title = "Sendungen nach Kategorie einfärben",
                        subtitle = "Kategoriefarben auf EPG und Senderzeilen anwenden.",
                        checked = palette.masterEnabled,
                        onCheckedChange = viewModel::setCategoryColorsEnabled,
                    )
                    DividerRow()
                    BoxWithConstraints {
                        val dim = if (palette.masterEnabled) 1f else 0.4f
                        if (twoUpInPane(maxWidth)) {
                            Column {
                                ProgramCategory.defaultBuckets.chunked(2)
                                    .forEachIndexed { rowIndex, pair ->
                                        if (rowIndex > 0) DividerRow()
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            pair.forEach { bucket ->
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .alpha(dim),
                                                ) {
                                                    CategoryPaletteRow(
                                                        bucket = bucket,
                                                        hex = palette.hexFor(bucket),
                                                        enabled = palette.masterEnabled,
                                                        onClick = { pickerTarget = bucket },
                                                    )
                                                }
                                            }
                                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                                        }
                                    }
                            }
                        } else {
                            Column {
                                ProgramCategory.defaultBuckets.forEachIndexed { idx, bucket ->
                                    if (idx > 0) DividerRow()
                                    Box(modifier = Modifier.alpha(dim)) {
                                        CategoryPaletteRow(
                                            bucket = bucket,
                                            hex = palette.hexFor(bucket),
                                            enabled = palette.masterEnabled,
                                            onClick = { pickerTarget = bucket },
                                        )
                                    }
                                }
                            }
                        }
                    }
                    DividerRow()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .dpadFocusWash()
                            .clickable(enabled = palette.masterEnabled) { viewModel.resetCategoryPalette() }
                            .alpha(if (palette.masterEnabled) 1f else 0.4f)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Farben auf Standard zurücksetzen",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFB8C00),
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    DividerRow()
                    Box(modifier = Modifier.alpha(if (palette.masterEnabled) 1f else 0.4f)) {
                        AddMoreCategoriesRow(
                            extraOn = ProgramCategory.additionalBuckets.count { palette.isBucketEnabled(it) },
                            customCount = palette.custom.size,
                            enabled = palette.masterEnabled,
                            onClick = onOpenAddMoreCategories,
                        )
                    }
                }
            }
        }
    }

    pickerTarget?.let { bucket ->
        HexPickerDialog(
            bucket = bucket,
            currentHex = palette.hexFor(bucket),
            onDismiss = { pickerTarget = null },
            onSave = { hex ->
                viewModel.setCategoryBucketHex(bucket, hex)
                pickerTarget = null
            },
            onReset = {
                viewModel.setCategoryBucketHex(bucket, null)
                pickerTarget = null
            },
        )
    }

    if (editingGroupSelector) {
        TvActionMenuDialog(
            title = "Group Selection",
            actions = listOf(
                TvMenuAction(
                    label = if (guideGroupSelector != "sidebar") "Gruppen-Schaltflächen oben  (aktuell)" else "Gruppen-Schaltflächen oben",
                ) {
                    viewModel.setGuideGroupSelector("pills")
                    editingGroupSelector = false
                },
                TvMenuAction(
                    label = if (guideGroupSelector == "sidebar") "Seitenleisten-Menü  (aktuell)" else "Seitenleisten-Menü",
                ) {
                    viewModel.setGuideGroupSelector("sidebar")
                    editingGroupSelector = false
                },
            ),
            onDismiss = { editingGroupSelector = false },
            guard = menuGuard,
        )
    }
}

/** Label + current value row, as Remote Control drew the Group Selection row
 *  before it moved here. Same shared container, no new chrome. */
@Composable
private fun GroupSelectionRow(
    slotName: String,
    valueName: String,
    onClick: () -> Unit,
) {
    SettingsRowContainer(onClick = onClick) {
        Text(
            text = slotName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        if (valueName.isNotEmpty()) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = valueName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.textAccent,
            )
        }
    }
}

/** Default Live TV View choices. Empty string = "Automatic" (form-factor
 *  default: List on compact phones, Guide on tablets / TV). "list" / "guide"
 *  are explicit overrides. Written to the same [defaultLiveTVView] pref the
 *  Live TV screen reads; the in-screen List / Guide button is session-only and
 *  never writes here. */
private val DEFAULT_LIVE_TV_VIEW_OPTIONS = listOf(
    "" to "Automatisch",
    "list" to "Liste",
    "guide" to "EPG",
)
