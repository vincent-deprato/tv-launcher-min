# v4.4 GitHub updater — release contract and test plan

The updater on branch `feature/v4.4-updater` looks only at the **latest stable release** of `vincent-deprato/tv-launcher-min`, not prereleases. It checks at most once a week when Home starts successfully and can be checked on demand via **Paramètres TV-Launcher > Rechercher une mise à jour**. It does not run a permanent background service.

## Release metadata requirements

For the updater to offer an APK, each **future** stable GitHub Release must:

1. Use a tag like `4.5.0` or `v4.5.0`.
2. Have one asset named exactly `tv-launcher.apk` with a valid GitHub-provided `digest: sha256:<64 hexadecimal digits>`.
3. Include a standalone release-note line with the APK's Android build code, e.g.:

   ```
   Android-VersionCode: 11
   ```

4. Be signed with the **same certificate** as the installed release and use the application ID `com.gathin.tvhome`.
5. Increase `versionCode` above the version installed on the TV.

**Important:** This updater was added in v4.4 (`versionCode 10`) and will first detect **v4.5+**. The already published v4.3 Release need not be changed; the updater cannot infer a versionCode from an arbitrary release tag and deliberately fails closed when integrity metadata is absent. Do not manually invent a SHA-256 value.

## Security and operational behavior

- HTTPS GitHub API, official repository and release assets only, timeout and size limits.
- SHA-256 over the fully downloaded APK and certificate/package/version checks before prompting Android to install.
- Android's Package Installer always asks the user to approve the install; never silently install packages.
- Uses a cache-scoped `FileProvider` URI, not shared file storage; does not ask for broad storage access.
- Any download, missing integrity digest, incompatible signature or version errors stop installation.
- Android may require **Allow installs from this source** for TV-Launcher Min.
- The progress window can cancel downloading; network checks happen on a worker thread.
- After an update, launcher preferences should persist thanks to the same package/signing identity.
- The updater does not alter system packages (`launcherx`, `setupwraith`), Home preferences or the Wi-Fi.

## Manual test matrix before publishing v4.4

- Build debug APK in GitHub Actions. Then build the **signed** test APK with existing release key; debug signatures cannot upgrade a signed release in place.
- Verify v4.4 signed APK upgrades the signed v4.3 APK on Sony and Xiaomi without uninstalling and preserves preferences and default HOME.
- No internet / GitHub outage / API rate limit: no crash, manual error message, automatic silent retry later.
- v4.4 is current latest: show "à jour" manually; no prompt automatically.
- Publish a newer **test release** with correct version code, GitHub asset digest and same signing key (avoid public stable publication solely for testing; a local mock server/test environment is preferable).
- Check manual download progress, cancellation, corrupted APK or tampered digest refusal, package mismatch and same/lower version rejection.
- Test Android "Install unknown apps" settings then return and retry.
- Confirm Android installer confirmation can be navigated by the D-pad; canceling install leaves v4.4 intact.
- Verify idle weekly check causes no visible popup or interference with HOME.

The updater code has not yet been hardware-tested or certified for release. Validate the signed APK and installation flow on Sony + Xiaomi before shipping.
