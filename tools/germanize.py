from pathlib import Path

translations = {
  "Live TV": "Live-TV",
  "Favorites": "Favoriten",
  "On Demand": "Mediathek",
  "Movies": "Filme",
  "TV Shows": "Serien",
  "Settings": "Einstellungen",
  "Search": "Suche",
  "Your IPTV & Media Hub": "Deine IPTV- & Medienzentrale",
  "Android TV · Phone · Tablet": "Android TV · Handy · Tablet",
  "Sync via Google Account": "Über Google-Konto synchronisieren",
  "Sign in to mirror playlists, watch progress, reminders, and preferences across your devices.": "Melde dich an, um Wiedergabelisten, Wiedergabefortschritt, Erinnerungen und Einstellungen auf deinen Geräten zu synchronisieren.",
  "Off": "Aus",
  "On": "Ein",
  "Connect a Server": "Server verbinden",
  "Skip for now": "Vorerst überspringen",
  "Choose a Source": "Quelle auswählen",
  "Choose Source": "Quelle auswählen",
  "Choose a source": "Quelle auswählen",
  "Continue": "Weiter",
  "Not now": "Nicht jetzt",
  "Back": "Zurück",
  "Next": "Weiter",
  "Done": "Fertig",
  "Cancel": "Abbrechen",
  "Save": "Speichern",
  "Delete": "Löschen",
  "Remove": "Entfernen",
  "Edit": "Bearbeiten",
  "Add": "Hinzufügen",
  "Close": "Schließen",
  "Retry": "Erneut versuchen",
  "Refresh": "Aktualisieren",
  "Test": "Testen",
  "Apply": "Übernehmen",
  "Reset": "Zurücksetzen",
  "Enable": "Aktivieren",
  "Disable": "Deaktivieren",
  "Enabled": "Aktiviert",
  "Disabled": "Deaktiviert",
  "General": "Allgemein",
  "Appearance": "Darstellung",
  "Player": "Wiedergabe",
  "Movies & TV Shows": "Filme & Serien",
  "Remote Control": "Fernbedienung",
  "Sync": "Synchronisierung",
  "Developer": "Entwickler",
  "About": "Über",
  "Playlists": "Wiedergabelisten",
  "Add Playlist": "Wiedergabeliste hinzufügen",
  "Edit Playlist": "Wiedergabeliste bearbeiten",
  "Refresh EPG Data": "EPG-Daten aktualisieren",
  "Refresh Everything": "Alles aktualisieren",
  "Guide Window": "EPG-Zeitraum",
  "All Available": "Alles verfügbar",
  "All Channels": "Alle Sender",
  "Username is required": "Benutzername ist erforderlich",
  "Password is required": "Passwort ist erforderlich",
  "Dispatcharr API key is required": "Dispatcharr-API-Schlüssel ist erforderlich",
  "Xtream Codes username is required": "Xtream-Codes-Benutzername ist erforderlich",
  "Search movies, shows, programs…": "Filme, Serien und Sendungen suchen…",
  "Search movies, shows, programs...": "Filme, Serien und Sendungen suchen...",
  "Search recordings": "Aufnahmen suchen",
  "Search channels": "Sender suchen",
  "No results": "Keine Ergebnisse",
  "No Results": "Keine Ergebnisse",
  "No Recordings": "Keine Aufnahmen",
  "Record a program from the guide or Live TV.": "Nimm eine Sendung aus dem EPG oder Live-TV auf.",
  "Recording now": "Aufnahme läuft",
  "Recording Now": "Laufende Aufnahmen",
  "Watch from Start": "Von Anfang ansehen",
  "Jump to Live": "Zu Live springen",
  "Stop Recording": "Aufnahme stoppen",
  "Resume": "Fortsetzen",
  "Play": "Abspielen",
  "Pause": "Pause",
  "Play from Beginning": "Von Anfang abspielen",
  "Details": "Details",
  "Options": "Optionen",
  "Continue Watching": "Weiterschauen",
  "Scheduled": "Geplant",
  "Recent Recordings": "Letzte Aufnahmen",
  "Results": "Ergebnisse",
  "All Recordings": "Alle Aufnahmen",
  "Partial": "Unvollständig",
  "Recording": "Aufnahme",
  "Delete recording?": "Aufnahme löschen?",
  "Title": "Titel",
  "Description": "Beschreibung",
  "Jump To": "Springen zu",
  "Day": "Tag",
  "Today": "Heute",
  "Tomorrow": "Morgen",
  "Now": "Jetzt",
  "Manage Groups": "Gruppen verwalten",
  "All": "Alle",
  "None": "Keine",
  "Sort": "Sortieren",
  "Filter": "Filter",
  "Program Info": "Sendungsinfo",
  "Record": "Aufnehmen",
  "Reminder": "Erinnerung",
  "Set Reminder": "Erinnerung setzen",
  "Remove Reminder": "Erinnerung entfernen",
  "Channel": "Sender",
  "Channels": "Sender",
  "No guide data": "Keine EPG-Daten",
  "No program information": "Keine Sendungsinformationen",
  "Audio Track": "Audiospur",
  "Subtitle Track": "Untertitelspur",
  "Subtitles": "Untertitel",
  "Audio": "Audio",
  "Playback Speed": "Wiedergabegeschwindigkeit",
  "Sleep Timer": "Sleep-Timer",
  "Picture in Picture": "Bild-in-Bild",
  "Switch Stream": "Stream wechseln",
  "Switch Version": "Version wechseln",
  "Channel List": "Senderliste",
  "Recent Channels": "Letzte Sender",
  "Multiview": "Mehrfachansicht",
  "Add streams": "Streams hinzufügen",
  "Add Streams": "Streams hinzufügen",
  "Make Audio": "Audio aktivieren",
  "Full Screen": "Vollbild",
  "Fullscreen": "Vollbild",
  "Quality": "Qualität",
  "Auto": "Automatisch",
  "Default": "Standard",
  "Default Tab": "Standard-Tab",
  "Language": "Sprache",
  "Theme": "Design",
  "Dark": "Dunkel",
  "Light": "Hell",
  "System": "System",
  "Text Size": "Textgröße",
  "Player Settings": "Wiedergabe-Einstellungen",
  "Live TV Settings": "Live-TV-Einstellungen",
  "DVR Settings": "DVR-Einstellungen",
  "General Settings": "Allgemeine Einstellungen",
  "Appearance Settings": "Darstellung",
  "Remote Control Settings": "Fernbedienung",
  "Sync Settings": "Synchronisierung",
  "App Updates": "App-Updates",
  "Developer Settings": "Entwicklereinstellungen",
  "Open Source Licenses": "Open-Source-Lizenzen",
  "Licenses": "Lizenzen",
  "Check for Updates": "Nach Updates suchen",
  "Check for Update": "Nach Update suchen",
  "Update Available": "Update verfügbar",
  "Up to Date": "Aktuell",
  "Download": "Herunterladen",
  "Install": "Installieren",
  "Downloading…": "Wird heruntergeladen…",
  "Downloading...": "Wird heruntergeladen...",
  "Installing…": "Wird installiert…",
  "Installing...": "Wird installiert...",
  "A new release is being published. Try again in a minute.": "Eine neue Version wird gerade veröffentlicht. Versuche es in einer Minute erneut.",
  "Update check failed: ${outcome.message}": "Update-Prüfung fehlgeschlagen: ${outcome.message}",
  "Not enough free space to download the update.": "Nicht genügend freier Speicher zum Herunterladen des Updates.",
  "Download failed: ${t.message ?: t::class.simpleName}": "Download fehlgeschlagen: ${t.message ?: t::class.simpleName}",
  "A recording is in progress. Finish or stop it, then install the update.": "Eine Aufnahme läuft. Beende oder stoppe sie und installiere danach das Update.",
  "The downloaded update is missing. Download it again.": "Das heruntergeladene Update fehlt. Lade es erneut herunter.",
  "Install failed: ${t.message ?: t::class.simpleName}": "Installation fehlgeschlagen: ${t.message ?: t::class.simpleName}",
  "Install failed (status $status)": "Installation fehlgeschlagen (Status $status)",
  "The download is incomplete (size mismatch). Try again.": "Der Download ist unvollständig. Versuche es erneut.",
  "The downloaded file is not a readable APK.": "Die heruntergeladene Datei ist keine lesbare APK.",
  "The downloaded APK is not AerioTV (${archive.packageName}).": "Die heruntergeladene APK gehört nicht zu AerioTV Deutsch (${archive.packageName}).",
  "Could not read the APK's signing certificate.": "Das Signaturzertifikat der APK konnte nicht gelesen werden.",
  "The APK's signing certificate does not match this app. Refusing to install.": "Das Signaturzertifikat der APK passt nicht zu dieser App. Installation abgebrochen.",
  "network error": "Netzwerkfehler",
  "rate limited": "Zu viele Anfragen",
  "bad response: ${t.message}": "Ungültige Serverantwort: ${t.message}",
  "Would you like to close AerioTV?": "Möchtest du AerioTV Deutsch schließen?",
  "Close AerioTV": "AerioTV Deutsch schließen",
  "Playing on ${companionTvName ?: \"TV\"}": "Wiedergabe auf ${companionTvName ?: \"TV\"}",
  "Playing on $castDevice": "Wiedergabe auf $castDevice",
  "Episode": "Episode",
  "Searching for devices...": "Geräte werden gesucht...",
  "Streaming to Cast device": "Streaming zum Cast-Gerät",
  "AerioTV is relaying this channel to your TV": "AerioTV Deutsch überträgt diesen Sender auf deinen Fernseher",
  "Sports": "Sport",
  "Kids": "Kinder",
  "News": "Nachrichten",
  "Documentary": "Dokumentation",
  "Comedy": "Komödie",
  "Educational": "Bildung",
  "Music": "Musik",
  "Dispatcharr (Username & Password)": "Dispatcharr (Benutzername & Passwort)",
  "Dispatcharr Direct Connect - Username & Password": "Dispatcharr Direct Connect – Benutzername & Passwort",
  "Dispatcharr Direct Connect - Admin API Key": "Dispatcharr Direct Connect – Admin-API-Schlüssel",
  "Dispatcharr Direct Connect - Standard API Key": "Dispatcharr Direct Connect – Standard-API-Schlüssel",
  "Imported file is missing: ${local.name}. Re-import it from Edit Playlist.": "Die importierte Datei fehlt: ${local.name}. Importiere sie unter „Wiedergabeliste bearbeiten“ erneut.",
  "Your Dispatcharr account can view recordings but not manage them. ": "Dein Dispatcharr-Konto kann Aufnahmen ansehen, aber nicht verwalten. ",
  "Ask your server administrator for DVR manage access.": "Bitte deinen Serveradministrator um Berechtigung zur DVR-Verwaltung.",
  "Your Dispatcharr account does not have DVR access. ": "Dein Dispatcharr-Konto hat keinen DVR-Zugriff. ",
  "Your Dispatcharr account does not have access to movies. ": "Dein Dispatcharr-Konto hat keinen Zugriff auf Filme. ",
  "Your Dispatcharr account does not have access to TV shows. ": "Dein Dispatcharr-Konto hat keinen Zugriff auf Serien. ",
  "Catch-up is not enabled for your Dispatcharr account. ": "Catch-up ist für dein Dispatcharr-Konto nicht aktiviert. ",
  "Ask your server administrator to enable it.": "Bitte deinen Serveradministrator, den Zugriff zu aktivieren.",
  "Switching streams needs a Dispatcharr administrator account. ": "Zum Wechseln von Streams ist ein Dispatcharr-Administratorkonto erforderlich. ",
  "Ask your server administrator for access.": "Bitte deinen Serveradministrator um Zugriff.",
  "Your Dispatcharr account cannot change server playlists. ": "Dein Dispatcharr-Konto darf Server-Wiedergabelisten nicht ändern. ",
  "Your Dispatcharr account cannot read server settings. ": "Dein Dispatcharr-Konto darf Servereinstellungen nicht lesen. ",
  "The server refused this even though your Dispatcharr account has permission. ": "Der Server hat die Anfrage abgelehnt, obwohl dein Dispatcharr-Konto die Berechtigung besitzt. ",
  "This usually means a network restriction on the account (allowed networks). ": "Das deutet meist auf eine Netzwerkbeschränkung des Kontos hin. ",
  "Ask your server administrator to allow this device's network.": "Bitte deinen Serveradministrator, das Netzwerk dieses Geräts freizugeben."
}

gradle = Path("app/build.gradle.kts")
s = gradle.read_text(encoding="utf-8")
s = s.replace('applicationId = "com.aeriotv.android"', 'applicationId = "de.dgstudios.aeriotvde"')
s = s.replace('versionCode = 65', 'versionCode = 1')
s = s.replace('versionName = "0.5.9"', 'versionName = "0.5.9-de1"')
gradle.write_text(s, encoding="utf-8")

settings = Path("settings.gradle.kts")
s = settings.read_text(encoding="utf-8").replace('rootProject.name = "AerioTV"', 'rootProject.name = "AerioTV-Deutsch"')
settings.write_text(s, encoding="utf-8")

strings = Path("app/src/main/res/values/strings.xml")
s = strings.read_text(encoding="utf-8").replace('<string name="app_name">AerioTV</string>', '<string name="app_name">AerioTV Deutsch</string>')
strings.write_text(s, encoding="utf-8")

manifest = Path("app/src/main/AndroidManifest.xml")
s = manifest.read_text(encoding="utf-8").replace('android:scheme="aeriotv"', 'android:scheme="aeriotvde"')
manifest.write_text(s, encoding="utf-8")

updater = Path("app/src/github/java/com/aeriotv/android/core/update/UpdateChecker.kt")
s = updater.read_text(encoding="utf-8")
s = s.replace("https://api.github.com/repos/jonzey231/AerioTV-Android/releases/latest",
              "https://api.github.com/repos/daniel96865-a11y/AerioTV-Android/releases/latest")
updater.write_text(s, encoding="utf-8")

candidates = list(Path("app/src/main/java/com/aeriotv/android/feature").rglob("*.kt"))
candidates += [
    Path("app/src/main/java/com/aeriotv/android/Navigation.kt"),
    Path("app/src/main/java/com/aeriotv/android/MainActivity.kt"),
    Path("app/src/main/java/com/aeriotv/android/core/category/ProgramCategory.kt"),
    Path("app/src/main/java/com/aeriotv/android/core/data/SourceType.kt"),
    Path("app/src/main/java/com/aeriotv/android/core/data/capability/DispatcharrCapability.kt"),
    Path("app/src/main/java/com/aeriotv/android/core/data/repository/PlaylistRepository.kt"),
    Path("app/src/main/java/com/aeriotv/android/core/cast/hlsproxy/CastHlsProxyService.kt"),
    Path("app/src/github/java/com/aeriotv/android/core/update/GithubUpdateManager.kt"),
    Path("app/src/github/java/com/aeriotv/android/core/update/UpdateChecker.kt"),
]
changed = 0
replacements = 0
for p in candidates:
    if not p.exists():
        continue
    src = p.read_text(encoding="utf-8")
    dst = src
    for en, de in translations.items():
        old = '"' + en.replace('"', '\\"') + '"'
        new = '"' + de.replace('"', '\\"') + '"'
        count = dst.count(old)
        if count:
            dst = dst.replace(old, new)
            replacements += count
    dst = dst.replace('text = "AerioTV"', 'text = "AerioTV Deutsch"')
    if dst != src:
        p.write_text(dst, encoding="utf-8")
        changed += 1

readme = Path("README.md")
original = readme.read_text(encoding="utf-8")
note = """# AerioTV Deutsch

**Inoffizielle deutsche Modifikation von AerioTV for Android.**
Ausgangsprojekt: jonzey231/AerioTV-Android. Diese Variante wurde am 21.09.2026 verändert und übersetzt. Sie bleibt gemäß dem Ausgangsprojekt unter GNU GPL v3 oder später; LICENSE, LICENSE-EXCEPTIONS.md und THIRD_PARTY_LICENSES.md bleiben Bestandteil des Projekts.

Die Variante verwendet die eigene Android-App-ID de.dgstudios.aeriotvde und kann dadurch neben dem Original installiert werden.

---

"""
if not original.startswith("# AerioTV Deutsch"):
    readme.write_text(note + original, encoding="utf-8")

Path("GERMAN_TRANSLATION_STATUS.md").write_text(
    f"# Deutsche Variante\n\nErster Übersetzungslauf: {replacements} exakte UI-Textvorkommen in {changed} Dateien ersetzt.\n"
    "Technische IDs, API-Felder, URLs und Codec-Namen wurden absichtlich nicht übersetzt.\n",
    encoding="utf-8",
)
print(f"Translated {replacements} occurrences across {changed} files")
