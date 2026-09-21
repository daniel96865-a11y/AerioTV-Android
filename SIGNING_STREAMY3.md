# Streamy 3.0 release signing

Streamy 3.0 uses a dedicated release key from version 3.0.1 onward.

- Key alias: `streamy3`
- Certificate SHA-256: `511D38CCD49511E9FCCE1C816D1186AD57AA6D2DD7D9155D6D029E1C7A97E9DA`
- GitHub Actions secret containing the base64-encoded keystore: `AERIOTV_KEYSTORE_B64`
- GitHub Actions secret containing the keystore/key password: `AERIOTV_SIGNING_PASSWORD`

The private keystore and password must never be committed to this public repository.

The GitHub release workflow verifies the certificate fingerprint before publishing an APK. The in-app updater uses the same fingerprint when validating downloaded updates.
