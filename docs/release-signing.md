# Streamy Release-Signatur

Seit Streamy 3.0.1 werden veröffentlichte APKs mit dem festen Streamy-Release-Schlüssel signiert.

- Alias: `streamy3`
- Zertifikat SHA-256: `1719EF5261A5520899A8FCBED1898A52F4775ABFC87A62D6DF4ACE93BAA1EF05`
- Der private Schlüssel und das Passwort dürfen niemals in dieses öffentliche Repository eingecheckt werden.
- Der Release-Workflow prüft den Zertifikat-Fingerabdruck vor der Veröffentlichung.
- Der In-App-Updater akzeptiert nur APKs mit derselben Signatur.

Das vollständige private Backup wurde als geschütztes GitHub-Actions-Artefakt erzeugt. Zusätzlich sollte dauerhaft eine private Offline-Kopie aufbewahrt werden, da Actions-Artefakte eine begrenzte Aufbewahrungszeit haben.
