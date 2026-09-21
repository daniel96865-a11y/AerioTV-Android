package com.aeriotv.android.core.sync

/**
 * One category of synced data. iOS mirrors the same taxonomy under iCloud
 * Sync > Sync Categories. Each category persists to its own Drive AppData
 * file ([fileName]) so partial-category sync is atomic.
 */
enum class SyncCategory(
    val displayName: String,
    val subtitle: String,
    val fileName: String,
    val storageSuffix: String,
) {
    Playlists(
        displayName = "Wiedergabelisten & Server",
        subtitle = "M3U-URLs, Dispatcharr-Server, Xtream-Zugangsdaten (nur serverseitig)",
        fileName = "playlists.v1.json",
        storageSuffix = "playlists",
    ),
    WatchProgress(
        displayName = "Wiedergabefortschritt",
        subtitle = "Fortsetzungspunkte für Filme, Episoden und Aufnahmen",
        fileName = "watch_progress.v1.json",
        storageSuffix = "watch_progress",
    ),
    Reminders(
        displayName = "Erinnerungen",
        subtitle = "Geplante Sendungserinnerungen",
        fileName = "reminders.v1.json",
        storageSuffix = "reminders",
    ),
    Favorites(
        displayName = "Favoriten",
        subtitle = "Favorisierte Sender + deine manuelle Reihenfolge",
        fileName = "favorites.v1.json",
        storageSuffix = "favorites",
    ),
    Watchlist(
        displayName = "Merkliste",
        subtitle = "Filme und Serien für später sowie ausgeblendete Titel",
        fileName = "watchlist.v1.json",
        storageSuffix = "watchlist",
    ),
    Preferences(
        displayName = "App-Einstellungen",
        subtitle = "Design, Darstellungsmodus, Akzentfarbe, Standard-Tab, ausgeblendete Gruppen und Farbanpassungen",
        fileName = "preferences.v1.json",
        storageSuffix = "preferences",
    ),
    Credentials(
        displayName = "Zugangsdaten",
        subtitle = "Server-Passwörter + API-Schlüssel (Drive-AppData ist nur für die App sichtbar)",
        fileName = "credentials.v1.json",
        storageSuffix = "credentials",
    );

    fun enabledStorageKey(): String = "syncCategory.$storageSuffix"
}
