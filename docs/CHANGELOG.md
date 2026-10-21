# Historique des versions / Release history

### 🆕 v4.6 — autorisations Android et diagnostic CPU

La v4.6.0 améliore le parcours d'autorisation d'installation sur Google TV avec des choix explicites pour ouvrir les paramètres ou tenter l'installateur Android. Certains firmwares peuvent toujours nécessiter ADB. Le diagnostic CPU affiche désormais le nombre de cœurs, la durée de mesure et précise si les valeurs concernent l'ensemble du système ou seulement TV-Launcher.

**Validation Xiaomi TV Box S 2e génération :** mise à jour intégrée v4.5 → v4.6, installation Android, nouveau diagnostic CPU et redémarrage avec HOME conservé : **OK**. **Reste à confirmer sur Sony Bravia :** autorisation d'installation d'une mise à jour sans ADB lorsque cette source n'a pas encore été autorisée. Le test Xiaomi ne valide pas ce cas car l'autorisation était déjà accordée.

### 🆕 v4.5 — diagnostic CPU et test des mises à jour

La v4.5.0 apporte une **barre graphique d'utilisation CPU** dans **Paramètres → Diagnostic RAM, CPU et stockage**. La mesure ponctuelle (~650 ms) ne nécessite pas de service résident. Android peut restreindre la mesure CPU globale ; dans ce cas, l'application affiche explicitement la mesure CPU propre à TV-Launcher. **La Release officielle 4.5.0 est disponible** et peut servir au premier test de mise à jour directement depuis la v4.4.

### 🔄 v4.4 — mises à jour directement dans TV-Launcher

**Version 4.4.0 officiellement publiée**, APK Release signé et contrôlé (versionCode 10), installation et redémarrage validés sur l'appareil de test.

- **Paramètres TV-Launcher → Rechercher une mise à jour** : consultation manuelle de la dernière Release GitHub stable.
- **Recherche discrète environ une fois par semaine** lors du démarrage de l'accueil, sans service en arrière-plan permanent.
- Téléchargement HTTPS avec progression, vérification de l'empreinte SHA-256 publiée par GitHub, du package `com.gathin.tvhome`, du certificat Release et du `versionCode`.
- **Installation uniquement après confirmation dans Android** ; l'autorisation d'installation depuis cette source peut être nécessaire.
- Pour que les futures mises à jour soient détectées, les notes de chaque nouvelle Release doivent inclure `Android-VersionCode: N` et son asset `tv-launcher.apk` doit porter une empreinte GitHub `sha256:…`.

**À tester encore :** détecter puis installer de bout en bout une Release plus récente depuis l'application. Les boutons rapides Netflix, Prime Video, YouTube et Apps restent indisponibles sur la Xiaomi si le launcher Google TV est désactivé.

[Guide technique de mise à jour](docs/UPDATER-V4.4.md) · [Release officielle v4.4.0](https://github.com/vincent-deprato/tv-launcher-min/releases/tag/4.4.0)

### 🧪 Version 4.3 — validation Xiaomi Google TV

Le build **v4.3 signé** a été testé sur **Xiaomi TV Box S 2e génération** (`MiTV-AFKR0`, Android 11, firmware `773`) : grille, masquage, diagnostic RAM/stockage et persistance des préférences. Le launcher reste sélectionné après redémarrage et la touche **Home** fonctionne.

**Limitation confirmée :** les touches dédiées **Netflix, Prime Video, YouTube et Apps** ne fonctionnent plus lorsque le launcher Google TV est désactivé. Un remappage est à étudier et n'est pas encore validé sur cette Xiaomi.

**[Procédure Xiaomi détaillée, avertissements Wi-Fi et restauration Google TV](docs/XIAOMI-BOX-S-2ND-GEN.md)**. La méthode ADB comporte un risque de perte de Wi-Fi et d'accueil ; elle n'est pas générale à tous les appareils.

---

### 🆕 v4.6 — Android install permissions and CPU details

v4.6.0 improves the Google TV update permission flow with explicit options to open settings or try Android's package installer. Some OEM firmwares may still require ADB. CPU diagnostics now show the available core count, measurement duration and whether usage is system-wide or limited to TV-Launcher.

**Xiaomi TV Box S 2nd gen verified:** in-app v4.5 → v4.6 update, Android installation, updated CPU diagnostics, and reboot with HOME preserved. **Still pending on Sony Bravia:** unknown-source installer permission without ADB when the source has not previously been authorized. This was not covered by the Xiaomi test because permission was already granted.

### 🆕 v4.5 — CPU diagnostics and updater test

v4.5.0 adds an on-demand **CPU usage bar** to **Settings → RAM, CPU and storage diagnostics**, using a short (~650 ms) sample without a resident service. If Android restricts system-wide CPU counters, the display explicitly identifies TV-Launcher process CPU instead. The **official 4.5.0 Release is available** to test in-app updating from v4.4.

### 🔄 v4.4 — in-app GitHub updates

**Version 4.4.0 is officially released.** Its signed Release APK (Android versionCode 10) passed certificate checks, and an installation/reboot smoke test passed.

- **TV-Launcher Settings → Check for updates** to manually check the latest stable GitHub Release.
- Quiet weekly check when Home starts, with no always-on service.
- HTTPS download with progress, GitHub-provided SHA-256 digest, package name, Release signing certificate and Android version-code validation.
- **Installation requires user confirmation in Android** and may require permission to install from this source.
- Future releases must include `Android-VersionCode: N` in release notes and a GitHub SHA-256 digest for the `tv-launcher.apk` asset.

**Still to test:** complete detection and installation of a newer stable GitHub Release from within the launcher. Xiaomi's dedicated remote shortcuts do not work when the original Google TV launcher is disabled.

[Updater technical guide](docs/UPDATER-V4.4.md) · [Official v4.4.0 release](https://github.com/vincent-deprato/tv-launcher-min/releases/tag/4.4.0)

### 🧪 Version 4.3 — Xiaomi Google TV validation

The **signed v4.3 build** was tested on a **Xiaomi TV Box S 2nd gen** (`MiTV-AFKR0`, Android 11, firmware `773`): grid reordering/hiding, RAM/storage diagnostics, preference persistence, and HOME surviving a reboot.

**Confirmed limitation:** dedicated **Netflix, Prime Video, YouTube, and Apps** remote keys stopped working after disabling the stock Google TV launcher. Remapping is not yet validated on this device.

**[Xiaomi-specific installation, Wi-Fi risks and Google TV recovery guide](docs/XIAOMI-BOX-S-2ND-GEN.md)**. This ADB procedure is firmware-specific and can interrupt Wi-Fi or HOME.
