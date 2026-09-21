<p align="center">
  <img src="app/src/main/res/drawable/streamy_logo.webp" width="180" alt="Streamy 3.0 Logo">
</p>

<h1 align="center">Streamy 3.0</h1>

<p align="center">
  IPTV für Android-Handys, Tablets, Android TV und Google TV.
</p>

## Download

Die aktuelle signierte APK findest du immer unter **Releases**:

**[Neueste Streamy-Version herunterladen](../../releases/latest)**

Eine APK unterstützt Handy, Tablet und Android TV / Google TV.

## Funktionen

- Live-TV mit EPG
- Filme und Serien / Mediathek
- Xtream Codes, M3U + XMLTV und Dispatcharr
- Media3 / ExoPlayer
- Favoriten und Sendergruppen
- Catch-up und EPG-Verlauf
- DVR / Aufnahmen
- Multiview mit mehreren Sendern
- Bild-in-Bild
- Untertitel und Audiospur-Auswahl
- Android-TV-Fernbedienung und D-Pad-Navigation
- Google-Drive-Synchronisierung optional
- automatische Update-Prüfung in Streamy

## Updates

Streamy prüft selbstständig, ob unter **GitHub Releases** eine neuere Version veröffentlicht wurde.

Zusätzlich kann unter **Einstellungen → Updates → Nach Updates suchen** manuell geprüft werden. Ist eine neue Version verfügbar, kann die APK direkt über Streamy heruntergeladen und installiert werden.

## Unterstützte Quellen

### Xtream Codes
Server-URL, Benutzername und Passwort. Unterstützt Live-TV, Filme, Serien und EPG, soweit der Anbieter diese Daten bereitstellt.

### M3U + XMLTV
M3U-Wiedergabelisten mit optionaler XMLTV-EPG-Quelle.

### Dispatcharr
Direkte Verbindung per Benutzername/Passwort oder API-Schlüssel. Unterstützt zusätzliche Funktionen wie serverseitige Aufnahmen und erweiterte EPG-/Mediathek-Daten, sofern der Server sie bereitstellt.

## Systemanforderungen

- Android 8.0 oder neuer
- Android-Handy oder Tablet
- Android TV / Google TV
- Internet- oder Netzwerkzugang zur eigenen IPTV-Quelle

## Entwicklung

- Kotlin
- Jetpack Compose / Material 3
- Media3 / ExoPlayer
- Room + DataStore
- Ktor
- Hilt
- Coroutines / Flow

Build:

```bash
./gradlew :app:assembleGithubRelease
```

## Datenschutz und Zugangsdaten

Streamy stellt selbst keine TV-Sender, Wiedergabelisten oder Zugangsdaten bereit. Nutzer verbinden ihre eigenen kompatiblen Quellen. Zugangsdaten werden auf dem Gerät geschützt gespeichert.

## Open Source und Lizenz

Streamy 3.0 ist eine modifizierte Open-Source-Version und wird gemäß **GNU GPL v3 oder später** bereitgestellt. Die für das Ausgangsprojekt erforderlichen Lizenz-, Urheber- und Drittanbieterhinweise bleiben erhalten.

Siehe:

- [LICENSE](LICENSE)
- [LICENSE-EXCEPTIONS.md](LICENSE-EXCEPTIONS.md)
- [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md)

Änderungen und Streamy-Branding: Stand 2026.
