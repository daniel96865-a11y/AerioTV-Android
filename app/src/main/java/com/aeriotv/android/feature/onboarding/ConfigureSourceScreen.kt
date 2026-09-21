package com.aeriotv.android.feature.onboarding

import com.aeriotv.android.ui.scale.subtext
import com.aeriotv.android.ui.theme.textAccent
import com.aeriotv.android.core.data.db.entity.sanitizeGuideDays
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aeriotv.android.ui.settings.TvSettingsMetrics
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeriotv.android.core.data.SourceType
import com.aeriotv.android.feature.onboarding.components.InfoBanner
import com.aeriotv.android.feature.onboarding.components.SourceTypeCard
import com.aeriotv.android.feature.playlist.PlaylistViewModel
import com.aeriotv.android.ui.settings.dpadFocusRing
import com.aeriotv.android.ui.settings.dpadFocusWash
import com.aeriotv.android.ui.settings.rememberIsTvDevice
import com.aeriotv.android.ui.textfield.aerioTextFieldKeyboardOptions
import com.aeriotv.android.ui.textfield.SecretRevealIconButton
import com.aeriotv.android.ui.textfield.rememberSecretRevealState
import com.aeriotv.android.ui.tv.TvKeyboardOnOkHost
import com.aeriotv.android.ui.tv.dpadFocusEscape
import com.aeriotv.android.ui.tv.tvFormFieldInput

/**
 * Configure-source form. Mirrors iOS App Store screenshots IMG_1078 (Dispatcharr
 * API Key), IMG_1079 (Xtream Codes), IMG_1080 (M3U + EPG), IMG_1081 (Dispatcharr
 * Username & Password): same source-type card at top, same field set per type,
 * same info banner, same "Verbindung testen" CTA.
 *
 * Field state mostly lives on PlaylistViewModel so it survives configuration
 * change and survives navigating away to the choose-type screen. A local
 * Dispatcharr-auth-mode toggle controls which form variant renders for the
 * Dispatcharr type.
 */
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ConfigureSourceScreen(
    sourceType: SourceType,
    onBack: () -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Make sure the VM knows which type we're configuring before the user enters anything.
    androidx.compose.runtime.LaunchedEffect(sourceType) {
        if (state.sourceType != sourceType) {
            viewModel.onSourceTypeChange(sourceType)
        }
    }

    // Dispatcharr has two flavours sharing the same Configure screen; track
    // the toggle locally. Key on the nav-passed `sourceType`, not
    // `state.sourceType` — the viewmodel sync happens in the LaunchedEffect
    // above which runs after first composition, so reading state.sourceType
    // here would pick up the previous session's value on first paint.
    var dispatcharrAuthMode by remember(sourceType) {
        mutableStateOf(
            when (sourceType) {
                SourceType.DispatcharrApiKey -> DispatcharrAuthMode.ApiKey
                else -> DispatcharrAuthMode.UsernamePassword
            }
        )
    }

    val cardIcon: ImageVector
    val cardTitle: String
    val cardSubtitle: String
    when (sourceType) {
        SourceType.DispatcharrApiKey, SourceType.DispatcharrUserPass -> {
            cardIcon = Icons.Filled.Key
            cardTitle = "Dispatcharr-Direktverbindung"
            cardSubtitle = "Verbinde dich mit Dispatcharr über deinen Admin-Zugang oder einen persönlichen API-Schlüssel " +
                    "(*Streamy ist nicht offiziell mit dem Dispatcharr-Projekt verbunden)"
        }
        SourceType.XtreamCodes -> {
            cardIcon = Icons.Filled.Tv
            cardTitle = "Xtream Codes"
            cardSubtitle = "Xtream-Codes-API. Live-TV, VOD-Filme und Serien."
        }
        SourceType.M3uUrl -> {
            cardIcon = Icons.Filled.Description
            cardTitle = "M3U + EPG"
            cardSubtitle = "Beliebige M3U-Wiedergabelisten-URL. Funktioniert mit Dispatcharr und IPTV-Anbietern."
        }
    }

    TvKeyboardOnOkHost {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    "Konfigurieren",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    // See ChooseSourceTypeScreen: hiding the TV back arrow
                    // collapses the nav-icon slot and leaves the title inside
                    // the title-safe margin.
                    modifier = if (rememberIsTvDevice()) {
                        Modifier.padding(start = TvSettingsMetrics.topAppBarTitleOverscanPadding)
                    } else {
                        Modifier
                    },
                )
            },
            navigationIcon = {
                // No back arrow on Android TV -- the remote BACK steps back to
                // source-type pick. Phones/tablets keep it.
                if (!rememberIsTvDevice()) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        )

        val vp = com.aeriotv.android.ui.adaptive.rememberViewport()
        // TV: deadband spec stops the form's +/-1px per-frame jiggle when a
        // focused text field sits at the floating IME's top edge (see
        // TvImeNoJitterBringIntoViewSpec). Same loop as Edit Playlist.
        val bringIntoViewSpec =
            if (rememberIsTvDevice()) com.aeriotv.android.ui.tv.TvImeNoJitterBringIntoViewSpec
            else androidx.compose.foundation.gestures.LocalBringIntoViewSpec.current
        androidx.compose.runtime.CompositionLocalProvider(
            androidx.compose.foundation.gestures.LocalBringIntoViewSpec provides bringIntoViewSpec,
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = vp.gutter, vertical = 12.dp),
            contentAlignment = androidx.compose.ui.Alignment.TopCenter,
        ) {
        Column(
            modifier = if (vp.onboardingMaxWidth != androidx.compose.ui.unit.Dp.Unspecified)
                Modifier.widthIn(max = vp.onboardingMaxWidth).fillMaxWidth()
            else
                Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SourceTypeCard(icon = cardIcon, title = cardTitle, subtitle = cardSubtitle)

            when (sourceType) {
                SourceType.DispatcharrApiKey, SourceType.DispatcharrUserPass -> {
                    DispatcharrFields(
                        state = state,
                        viewModel = viewModel,
                        authMode = dispatcharrAuthMode,
                        onAuthModeChange = { mode ->
                            dispatcharrAuthMode = mode
                            viewModel.onSourceTypeChange(
                                if (mode == DispatcharrAuthMode.ApiKey)
                                    SourceType.DispatcharrApiKey
                                else
                                    SourceType.DispatcharrUserPass
                            )
                        },
                    )
                }
                SourceType.XtreamCodes -> XtreamFields(state, viewModel)
                SourceType.M3uUrl -> M3uFields(state, viewModel)
            }

            // Per-playlist On Demand opt-in (iOS AddServerView.vodEnabledRow,
            // AddServerView.swift:337). Only meaningful for source types that
            // actually carry VOD; M3U playlists never have it. Default ON.
            // When off the OnDemand tab disappears and the multi-thousand-item
            // VOD sync is skipped -- useful for users who only want Live TV
            // from this playlist, or who have a second playlist already
            // providing VOD. Can be flipped later in Edit Playlist.
            if (sourceType.supportsVOD) {
                Spacer(Modifier.height(4.dp))
                VodEnabledRow(
                    checked = state.vodEnabled,
                    onCheckedChange = viewModel::onVodEnabledChange,
                )
            }

            // Task #45 DVR onboarding step (iOS AddServerView Destination
            // picker, Dispatcharr only): where recordings land by default.
            // Committed to the global DVR preference only when Test
            // Connection succeeds; changeable later in Settings > DVR.
            if (sourceType == SourceType.DispatcharrApiKey ||
                sourceType == SourceType.DispatcharrUserPass
            ) {
                Spacer(Modifier.height(4.dp))
                DvrDestinationRow(
                    server = state.dvrDestinationServer,
                    onSelect = viewModel::onDvrDestinationChange,
                )
            }

            // Catch-up guide-history retention (task #135): how many days of
            // already-aired guide data this playlist keeps. Past shows on
            // channels with catch-up can be replayed from the guide, so this
            // doubles as the catch-up browse depth. Default 7 days; also
            // editable later in Edit Playlist.
            Spacer(Modifier.height(4.dp))
            GuideHistoryRow(
                selectedDays = state.epgRetentionDays,
                onSelect = viewModel::onEpgRetentionDaysChange,
            )

            val validation = validate(sourceType, state, dispatcharrAuthMode)
            if (validation != null) {
                InfoBanner(text = validation)
            }
            state.error?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.loadPlaylist() },
                enabled = !state.isLoading && validation == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 54.dp)
                    .dpadFocusRing(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Hub,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.size(10.dp))
                    Text(
                        text = "Verbindung testen",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
        }
        }
    }
    }
}

enum class DispatcharrAuthMode { UsernamePassword, ApiKey }

@Composable
private fun DispatcharrFields(
    state: PlaylistViewModel.UiState,
    viewModel: PlaylistViewModel,
    authMode: DispatcharrAuthMode,
    onAuthModeChange: (DispatcharrAuthMode) -> Unit,
) {
    LabeledField(label = "Name") {
        IconTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            placeholder = "Mein IPTV-Server",
            leading = Icons.Outlined.Sell,
            enabled = !state.isLoading,
        )
    }

    LabeledField(label = "Server-URL") {
        IconTextField(
            value = state.url,
            onValueChange = viewModel::onUrlChange,
            placeholder = "http://your-dispatcharr-server:9191",
            leading = Icons.Outlined.Link,
            enabled = !state.isLoading,
        )
    }

    LanUrlField(state = state, viewModel = viewModel)

    SegmentedAuthControl(
        selected = authMode,
        onSelect = onAuthModeChange,
        enabled = !state.isLoading,
    )

    when (authMode) {
        DispatcharrAuthMode.ApiKey -> {
            LabeledField(label = "Admin-API-Schlüssel") {
                val apiKeyReveal = rememberSecretRevealState()
                IconTextField(
                    value = state.apiKey,
                    onValueChange = viewModel::onApiKeyChange,
                    placeholder = "Admin-API-Schlüssel einfügen",
                    leading = Icons.Filled.Key,
                    visualTransformation = apiKeyReveal.transformation,
                    trailing = {
                        SecretRevealIconButton(
                            state = apiKeyReveal,
                            iconSize = 18.dp,
                            contentLabel = "API-Schlüssel",
                        )
                    },
                    trailingFocused = { apiKeyReveal.controlFocused },
                    enabled = !state.isLoading,
                )
            }
            InfoBanner(
                text = "Verwende einen Dispatcharr-Admin-API-Schlüssel (System -> Benutzer -> Benutzer bearbeiten -> API & XC). " +
                        "Damit werden die nativen Dispatcharr-Schnittstellen für Live-TV, EPG, Filme und " +
                        "Serien aktiviert. Wenn dein Administrator den Schlüssel ändert, musst du ihn hier " +
                        "neu eingeben. Für eine automatische Aktualisierung verwende Benutzername & Passwort.",
            )
        }
        DispatcharrAuthMode.UsernamePassword -> {
            LabeledField(label = "Benutzername") {
                IconTextField(
                    value = state.username,
                    onValueChange = viewModel::onUsernameChange,
                    placeholder = "Dispatcharr-Admin-Benutzername",
                    leading = Icons.Outlined.Person,
                    enabled = !state.isLoading,
                )
            }
            PasswordField(
                label = "Passwort",
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Dispatcharr-Admin-Passwort",
                enabled = !state.isLoading,
            )
            Text(
                text = "Verwende dein Dispatcharr-Dashboard-Passwort (System -> Benutzer -> Konto), " +
                        "nicht dein Dispatcharr-XC-Passwort.",
                style = MaterialTheme.typography.bodySmall.subtext(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            InfoBanner(
                text = "Streamy verwendet diese Zugangsdaten zur Anmeldung und hält die Sitzung im Hintergrund aktiv. " +
                        "Wenn dein Dispatcharr-Administrator den API-Schlüssel ändert, meldet sich Streamy automatisch neu an. " +
                        "Die Zugangsdaten werden verschlüsselt auf deinem Android-Gerät gespeichert.",
            )
        }
    }
}

@Composable
private fun XtreamFields(state: PlaylistViewModel.UiState, viewModel: PlaylistViewModel) {
    LabeledField(label = "Name") {
        IconTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            placeholder = "Mein IPTV-Server",
            leading = Icons.Outlined.Sell,
            enabled = !state.isLoading,
        )
    }
    LabeledField(label = "Server-URL") {
        IconTextField(
            value = state.url,
            onValueChange = viewModel::onUrlChange,
            placeholder = "http://your-server.com:8080",
            leading = Icons.Outlined.Link,
            enabled = !state.isLoading,
        )
    }
    LanUrlField(state = state, viewModel = viewModel)
    LabeledField(label = "Benutzername") {
        IconTextField(
            value = state.username,
            onValueChange = viewModel::onUsernameChange,
            placeholder = "XC-Benutzername",
            leading = Icons.Outlined.Person,
            enabled = !state.isLoading,
        )
    }
    PasswordField(
        label = "Passwort",
        value = state.password,
        onValueChange = viewModel::onPasswordChange,
        placeholder = "XC-Passwort",
        enabled = !state.isLoading,
    )
    InfoBanner(
        text = "Gib die Xtream-Codes-Server-URL und Zugangsdaten ein. Dispatcharr-Nutzer verwenden " +
                "die Dispatcharr-URL zusammen mit dem Xtream-Codes-Benutzernamen und -Passwort aus den " +
                "Dispatcharr-Benutzereinstellungen.",
    )
}

@Composable
private fun M3uFields(state: PlaylistViewModel.UiState, viewModel: PlaylistViewModel) {
    LabeledField(label = "Name") {
        IconTextField(
            value = state.name,
            onValueChange = viewModel::onNameChange,
            placeholder = "Mein IPTV-Server",
            leading = Icons.Outlined.Sell,
            enabled = !state.isLoading,
        )
    }
    LabeledField(label = "M3U URL") {
        IconTextField(
            value = state.url,
            onValueChange = viewModel::onUrlChange,
            placeholder = "https://example.com/playlist.m3u",
            leading = Icons.Outlined.Link,
            enabled = !state.isLoading,
        )
    }
    // Task #45 file import (iOS M3UImportView fileImporter twin). Phone and
    // tablet only: TVs have no document-picker UI. The picked file is copied
    // into filesDir/imports/ and its file:// URI dropped into the URL field,
    // so the rest of the add/refresh pipeline treats it like any source
    // (fetchViaTempFile parses file: URIs in place).
    val importContext = androidx.compose.ui.platform.LocalContext.current
    val importScope = androidx.compose.runtime.rememberCoroutineScope()
    if (!rememberIsTvDevice()) {
        val m3uPicker = androidx.activity.compose.rememberLauncherForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let {
                importScope.launch {
                    importPickedFile(importContext, it, "m3u")?.let(viewModel::onUrlChange)
                }
            }
        }
        ImportFileLink(label = "M3U stattdessen aus einer Datei importieren") {
            // M3U MIME registration is a mess in the wild (x-mpegurl,
            // audio/mpegurl, octet-stream, text/plain) -- accept anything,
            // like iOS's [.data, .plainText] allowance.
            m3uPicker.launch(arrayOf("*/*"))
        }
    }
    LabeledField(label = "EPG-URL (optional)") {
        IconTextField(
            value = state.epgUrl,
            onValueChange = viewModel::onEpgUrlChange,
            placeholder = "https://example.com/epg.xml",
            leading = Icons.Outlined.CalendarToday,
            enabled = !state.isLoading,
        )
    }
    if (!rememberIsTvDevice()) {
        val epgPicker = androidx.activity.compose.rememberLauncherForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let {
                importScope.launch {
                    importPickedFile(importContext, it, "xml")?.let(viewModel::onEpgUrlChange)
                }
            }
        }
        ImportFileLink(label = "XMLTV stattdessen aus einer Datei importieren") {
            epgPicker.launch(arrayOf("*/*"))
        }
    }
    InfoBanner(
        text = "Füge die URL deiner M3U-Wiedergabeliste ein. Funktioniert mit Dispatcharr /output/m3u und IPTV-" +
                "provider, or a direct .m3u file link.",
    )
}

/** Task #45: small inline action under a URL field that swaps it for an
 *  imported local file. */
@Composable
private fun ImportFileLink(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.textAccent,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp),
    )
}

/** Copies a picked document into filesDir/imports/ and returns its file://
 *  URI, or null when the stream can't be opened. The copy makes the source
 *  durable across reboots and permission-grant expiry (same reason iOS
 *  copies imports into its container) -- refresh re-parses the snapshot. */
private suspend fun importPickedFile(
    context: android.content.Context,
    uri: android.net.Uri,
    ext: String,
): String? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
    runCatching {
        val dir = java.io.File(context.filesDir, "imports").apply { mkdirs() }
        // Carry the picked document's display name into the stored copy so
        // the auto-derived playlist name reads "sample" instead of a UUID
        // (deriveName takes the URL's last path segment minus extension).
        val display = context.contentResolver.query(
            uri,
            arrayOf(android.provider.OpenableColumns.DISPLAY_NAME),
            null, null, null,
        )?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
            ?.substringBeforeLast('.')
            ?.replace(Regex("[^A-Za-z0-9 ._-]"), "")
            ?.take(40)
            ?.ifBlank { null }
        val suffix = java.util.UUID.randomUUID().toString().take(8)
        val dest = java.io.File(dir, "${display ?: "import"}-$suffix.$ext")
        context.contentResolver.openInputStream(uri)?.use { input ->
            dest.outputStream().use { output -> input.copyTo(output) }
        } ?: return@runCatching null
        android.net.Uri.fromFile(dest).toString()
    }.getOrNull()
}

/**
 * Optional LAN URL field. Pairs with the Server URL so the user can capture
 * both the public/remote and the LAN-side host in one pass during onboarding;
 * AerioTV swaps to the LAN value automatically whenever the server answers
 * locally (a reachability probe, checked at launch, on network changes, and
 * after edits). M3U sources skip this — they're already URL-based and there's
 * no auth credential reuse implied.
 */
@Composable
private fun LanUrlField(state: PlaylistViewModel.UiState, viewModel: PlaylistViewModel) {
    LabeledField(label = "LAN-URL (optional)") {
        IconTextField(
            value = state.lanUrl,
            onValueChange = viewModel::onLanUrlChange,
            placeholder = "http://192.168.1.50:9191",
            leading = Icons.Outlined.Wifi,
            enabled = !state.isLoading,
        )
    }
    Text(
        text = "Streamy verwendet diese URL automatisch, wenn dein Server über das lokale Netzwerk erreichbar ist. " +
                "Andernfalls wird die oben angegebene öffentliche Server-URL verwendet. Keine weitere Einrichtung nötig.",
        style = MaterialTheme.typography.bodySmall.subtext(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun LabeledField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
        )
        content()
    }
}

@Composable
private fun IconTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: ImageVector,
    enabled: Boolean,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable (() -> Unit)? = null,
    // True while the trailing control (the reveal eye) holds D-pad focus, so
    // the field leaves OK alone and the button can actually be clicked.
    trailingFocused: () -> Boolean = { false },
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = aerioTextFieldKeyboardOptions(),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth()
            .tvFormFieldInput(
                horizontalFocusEscape = trailing != null,
                okSuppressed = trailingFocused,
            ),
        singleLine = true,
        placeholder = { Text(placeholder, style = androidx.compose.material3.LocalTextStyle.current.subtext(), color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = {
            Icon(
                imageVector = leading,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        },
        trailingIcon = trailing,
        enabled = enabled,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
    )
}


@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
) {
    val reveal = rememberSecretRevealState()
    LabeledField(label = label) {
        IconTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            leading = Icons.Outlined.Lock,
            enabled = enabled,
            visualTransformation = reveal.transformation,
            trailing = {
                SecretRevealIconButton(
                    state = reveal,
                    iconSize = 18.dp,
                    contentLabel = "Passwort",
                )
            },
            trailingFocused = { reveal.controlFocused },
        )
    }
}

@Composable
private fun SegmentedAuthControl(
    selected: DispatcharrAuthMode,
    onSelect: (DispatcharrAuthMode) -> Unit,
    enabled: Boolean,
) {
    val cornerRadius = 22.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                RoundedCornerShape(cornerRadius),
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SegmentItem(
            label = "Benutzername & Passwort",
            isSelected = selected == DispatcharrAuthMode.UsernamePassword,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(DispatcharrAuthMode.UsernamePassword) },
        )
        SegmentItem(
            label = "API-Schlüssel",
            isSelected = selected == DispatcharrAuthMode.ApiKey,
            enabled = enabled,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(DispatcharrAuthMode.ApiKey) },
        )
    }
}

@Composable
private fun SegmentItem(
    label: String,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent
    val fg = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .dpadFocusRing(RoundedCornerShape(18.dp), washTint = MaterialTheme.colorScheme.primary)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = fg,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun validate(
    sourceType: SourceType,
    state: PlaylistViewModel.UiState,
    authMode: DispatcharrAuthMode,
): String? {
    val missing = mutableListOf<String>()
    if (state.url.isBlank()) missing += "Server-URL"
    when (sourceType) {
        // Dispatcharr accepts EITHER an admin API key OR a username +
        // password -- they're alternatives, never both. Validate against
        // whichever the user has actually filled rather than forcing the
        // segmented toggle's mode: a filled API key is accepted even if the
        // control still reads "Benutzername & Passwort" (and vice versa), so a
        // stuck/mis-set toggle can't demand the other set of fields. Only
        // when NEITHER credential is present do we prompt -- for the field(s)
        // of the currently-selected mode.
        SourceType.DispatcharrApiKey, SourceType.DispatcharrUserPass -> {
            val hasApiKey = state.apiKey.isNotBlank()
            val hasUserPass = state.username.isNotBlank() && state.password.isNotBlank()
            if (!hasApiKey && !hasUserPass) {
                if (authMode == DispatcharrAuthMode.ApiKey) {
                    missing += "API-Schlüssel"
                } else {
                    if (state.username.isBlank()) missing += "Benutzername"
                    if (state.password.isBlank()) missing += "Passwort"
                }
            }
        }
        SourceType.XtreamCodes -> {
            if (state.username.isBlank()) missing += "Benutzername"
            if (state.password.isBlank()) missing += "Passwort"
        }
        SourceType.M3uUrl -> Unit
    }
    return when {
        missing.isEmpty() -> null
        missing.size == 1 -> "${missing.first()} ist erforderlich."
        else -> "${missing.size} Felder müssen ausgefüllt werden."
    }
}

/**
 * On Demand opt-in row for Add / Edit Playlist. Mirrors the iOS
 * AddServerView.vodEnabledRow (AddServerView.swift:337-348): title + help
 * text + accent-tinted Switch. Title is wrapped in a Row so the Switch
 * sits on the right with the text claiming the rest of the width. The
 * help paragraph reuses the bodySmall + onSurfaceVariant pair the other
 * form descriptions use.
 */
@Composable
private fun VodEnabledRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        // Whole-row toggle target: on TV the bare Switch's focus state is
        // invisible, so the row carries the D-pad focus wash instead.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .dpadFocusWash()
                .toggleable(
                    value = checked,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Switch,
                    onValueChange = onCheckedChange,
                ),
        ) {
            Text(
                text = "Mediathek aus dieser Wiedergabeliste laden",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                ),
            )
        }
        Text(
            text = "Wenn ausgeschaltet, werden Filme und Serien dieser Wiedergabeliste nicht in die Mediathek geladen. Nützlich, wenn du von diesem Server nur Live-TV möchtest oder eine zweite Wiedergabeliste bereits die Mediathek liefert. Dies kann später in den Einstellungen geändert werden.",
            style = MaterialTheme.typography.bodySmall.subtext(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

/**
 * Guide-history retention picker for Add Playlist (task #135): a compact pill
 * row (1/3/7/14/30 days) matching the form's typography, with the same
 * bodySmall help paragraph the other rows use. Each pill carries the D-pad
 * focus wash so the choice is navigable on TV.
 */
@Composable
private fun DvrDestinationRow(
    server: Boolean,
    onSelect: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "Standard-Speicherort für Aufnahmen",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(true to "Server (Dispatcharr)", false to "Dieses Gerät").forEach { (isServer, label) ->
                val selected = isServer == server
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .dpadFocusWash()
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        )
                        .clickable { onSelect(isServer) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
        Text(
            text = "Legt fest, wo Aufnahmen standardmäßig gespeichert werden. Server-Aufnahmen benötigen ein Dispatcharr-Administratorkonto. Später unter Einstellungen > DVR änderbar.",
            style = MaterialTheme.typography.bodySmall.subtext(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun GuideHistoryRow(
    selectedDays: Int,
    onSelect: (Int) -> Unit,
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "EPG-Tage",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 0 = All Available (Logan 2026-09-11); legacy 30 reads as All.
            listOf(1, 3, 7, 14, 0).forEach { days ->
                val selected = days == sanitizeGuideDays(selectedDays)
                Text(
                    text = when (days) {
                        0 -> "Alles verfügbar"
                        1 -> "1 Tag"
                        else -> "$days Tage"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .dpadFocusWash()
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        )
                        .clickable { onSelect(days) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
        Text(
            text = "Wie viele Tage EPG-Daten rückwirkend und im Voraus geladen werden. Nur für Dispatcharr; andere Quellen zeigen die gelieferten EPG-Daten.",
            style = MaterialTheme.typography.bodySmall.subtext(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
