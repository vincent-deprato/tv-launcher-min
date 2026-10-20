# 📺 TV-Launcher Min

**A lightweight, customizable Android TV launcher.**

Un launcher Android TV léger, personnalisable et pensé pour une navigation simple à la télécommande, notamment sur les téléviseurs disposant de peu de mémoire RAM.

Projet communautaire basé sur [CleanYourTV](https://github.com/Gathin23/cleanyourtv) / TV-VibeHack.

[🇫🇷 Français](#-français) · [🇬🇧 English](#-english)

**Version actuelle :** 4.3.0 · **Android :** 8.0+ · **Statut :** Release publique

**[⬇️ Télécharger TV-Launcher Min v4.3.0](https://github.com/vincent-deprato/tv-launcher-min/releases/download/4.3.0/tv-launcher.apk)** · [📦 Toutes les versions](https://github.com/vincent-deprato/tv-launcher-min/releases)

![TV-Launcher Min v4.2.0 — écran d'accueil sur Sony Bravia](https://github.com/user-attachments/assets/1fdadb7a-4a3a-49af-9b41-ab5b42b9f3c3)

---

## 🇫🇷 Français

### ✨ Présentation

**TV-Launcher Min v4.3.0** personnalise CleanYourTV pour proposer un écran d'accueil Android TV épuré, fluide et utilisable entièrement à la télécommande.

Son objectif : **afficher l'essentiel, faciliter l'accès aux applications et éviter les fonctionnalités exécutées inutilement en arrière-plan.**

Testé sur **Sony Bravia KD-55XF9005** (Android TV) et **Xiaomi TV Box S 2e génération** (Google TV 11, firmware 773). La compatibilité avec d'autres appareils n'est pas garantie.

### 🚀 Fonctionnalités

**Interface et navigation**
- Interface sombre inspirée de tvOS, avec horloge et date.
- Raccourcis Sources, Wi-Fi et Paramètres.
- Menus compacts et harmonisés, adaptés à la télécommande.

**Favoris et applications**
- Six favoris personnalisables : déplacer, réordonner ou remplacer les applications.
- Réorganisation de la grille principale ; possibilité de masquer et réafficher des applications.
- Appui long sur une application pour ouvrir ses informations système.
- Préférences enregistrées localement.

**Diagnostic**
- Consultation ponctuelle de la mémoire RAM et du stockage.
- Pas de service de surveillance permanente pour ces informations.

**Nouveautés v4.3 :** sauvegarde renforcée de l'ordre de la grille et des applications masquées, confirmations harmonisées avec les menus compacts, et diagnostic graphique RAM/stockage. Tests validés sur Sony Bravia et Xiaomi TV Box S 2e génération.

**Limite connue :** l'import/export JSON des préférences n'est pas disponible en v4.3.

### 🖼️ Aperçu des fonctionnalités

Captures historiques de la **Release v4.2.0**, réalisées sur Sony Bravia (l'interface v4.3 peut présenter des différences).

<table>
  <tr>
    <td width="50%"><img src="https://github.com/user-attachments/assets/3030eb60-a677-4c5d-b8f9-be658d0e28ac" alt="TV-Launcher Min v4.2.0 — capture des fonctionnalités 1" width="100%" /></td>
    <td width="50%"><img src="https://github.com/user-attachments/assets/8d7ac89d-775a-4e9f-8e8e-857074b483b0" alt="TV-Launcher Min v4.2.0 — capture des fonctionnalités 2" width="100%" /></td>
  </tr>
  <tr>
    <td width="50%"><img src="https://github.com/user-attachments/assets/d303019e-0cea-415c-8d41-555daf4abd79" alt="TV-Launcher Min v4.2.0 — capture des fonctionnalités 3" width="100%" /></td>
    <td width="50%"><img src="https://github.com/user-attachments/assets/dbede6d9-b3fd-453e-aeea-48528b8eca68" alt="TV-Launcher Min v4.2.0 — capture des fonctionnalités 4" width="100%" /></td>
  </tr>
</table>

### 📥 Téléchargement et installation

**[Télécharger tv-launcher.apk — v4.3.0](https://github.com/vincent-deprato/tv-launcher-min/releases/download/4.3.0/tv-launcher.apk)**

Deux méthodes sont possibles. **Ne désactivez pas le launcher constructeur sans avoir validé une procédure spécifique à votre modèle, ainsi qu'une méthode de restauration** : testez d'abord TV-Launcher Min et assurez-vous de pouvoir revenir à l'accueil d'origine. Sur certaines box Google TV, forcer HOME nécessite de désactiver des composants système avec un risque de perte du Wi-Fi et des raccourcis télécommande. [Procédure Xiaomi validée sur firmware 773](docs/XIAOMI-BOX-S-2ND-GEN.md).

#### Méthode A — Installation sans ordinateur

1. Téléchargez `tv-launcher.apk` depuis la Release officielle.
2. Transférez l'APK sur la TV (clé USB, gestionnaire de fichiers ou outil de transfert compatible).
3. Ouvrez le fichier et autorisez l'installation depuis cette source si Android le demande.
4. Installez l'application et lancez TV-Launcher Min.
5. Si Android propose de choisir une application d'accueil, sélectionnez **TV-Launcher Min**.

Certains firmwares Sony ne proposent pas de sélecteur d'accueil ; dans ce cas, la méthode ADB peut être nécessaire.

#### Méthode B — Installation via ADB (Windows)

**1. Préparer ADB**

Téléchargez les outils officiels [Android SDK Platform-Tools](https://developer.android.com/tools/releases/platform-tools), décompressez-les (par exemple dans `C:\platform-tools`) et ouvrez PowerShell dans ce dossier.

Sur la TV, activez les **Options pour les développeurs** puis le **débogage ADB/USB**. La connexion ADB par réseau doit être prise en charge et activée ; sur certains appareils, un branchement USB initial est nécessaire.

**2. Se connecter à la TV**

```powershell
.\adb.exe connect ADRESSE_IP_TV:5555
.\adb.exe devices
```

Adaptez **l'adresse IP et le port** à votre TV : `5555` n'est pas universel. La TV doit apparaître dans l'état `device`. Autorisez le débogage depuis la télévision si elle affiche une demande de confirmation.

**3. Noter l'accueil actuel avant de le changer**

```powershell
.\adb.exe shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

Conservez le résultat pour faciliter un retour arrière. Selon le firmware, cette commande peut retourner un sélecteur d'accueil plutôt que l'application constructeur.

**4. Installer l'APK**

Placez `tv-launcher.apk` dans le dossier courant et lancez :

```powershell
.\adb.exe install -r .\tv-launcher.apk
```

Le résultat attendu est `Success`.

**5. Lancer TV-Launcher Min**

```powershell
.\adb.exe shell am start -n com.gathin.tvhome/.MainActivity
```

**6. Choisir TV-Launcher comme accueil**

Utilisez d'abord les paramètres d'applications par défaut ou le sélecteur Home d'Android, si disponibles. Certains appareils acceptent cette commande :

```powershell
.\adb.exe shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
```

Vérifiez le résultat :

```powershell
.\adb.exe shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

Si TV-Launcher est sélectionné, le résultat doit mentionner `com.gathin.tvhome/.MainActivity`. Testez la touche **Home**, la navigation, les favoris, les sources HDMI et l'accès aux paramètres, puis redémarrez la TV pour vérifier le comportement au démarrage.

> ⚠️ **Sécurité :** ne désactivez pas de services système au hasard pour forcer le changement de launcher. Les procédures avancées, comme celle [documentée pour Xiaomi MiTV-AFKR0 firmware 773](docs/XIAOMI-BOX-S-2ND-GEN.md), sont spécifiques au matériel et peuvent couper le Wi-Fi ou l'accueil. Ne jamais désactiver SystemUI, les composants du tuner ou d'autres services essentiels.

### ↩️ Revenir au launcher d'origine

**Solution recommandée :** sélectionnez l'application d'accueil d'origine depuis les paramètres Android TV, si cette option est disponible.

**Via ADB :** si vous avez relevé le composant exact de l'accueil constructeur, essayez :

```powershell
.\adb.exe shell cmd package set-home-activity PACKAGE_ORIGINE/ACTIVITE_ORIGINE
```

Remplacez les valeurs par celles de **votre propre appareil**. La commande n'est pas disponible sur tous les firmwares.

**Désinstaller TV-Launcher Min :**

```powershell
.\adb.exe uninstall com.gathin.tvhome
```

La désinstallation supprime normalement les favoris et préférences locales. Elle ne réactive **pas automatiquement** un launcher constructeur qui aurait été désactivé séparément.

### 🔧 Problèmes fréquents

| Message ou symptôme | Que faire ? |
|---|---|
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | Une version installée utilise une signature différente (par exemple une ancienne version debug). La désinstallation peut être nécessaire, avec perte possible des réglages. |
| `device unauthorized` | Confirmez l'autorisation de débogage sur la TV. |
| `device offline` | Vérifiez la connexion puis reconnectez ADB. |
| Connexion ADB refusée | Vérifiez que le débogage réseau est disponible, activé et sur le bon port. |
| Home ouvre toujours l'ancien launcher | Vérifiez l'application d'accueil sélectionnée dans Android. |
| L'installation réussit, mais l'application ne s'ouvre pas | Vérifiez la compatibilité Android et consultez au besoin `adb logcat`. |

### 🔄 Mises à jour

Les futures versions officielles peuvent normalement être installées sans désinstallation :

```powershell
.\adb.exe install -r .\tv-launcher.apk
```

Pour conserver les préférences, l'APK doit utiliser le **même identifiant** (`com.gathin.tvhome`), le **même certificat de signature** et un `versionCode` compatible. Une ancienne version **debug** ne peut pas nécessairement être remplacée directement par une Release signée.

### 🛠️ Compiler depuis les sources

Prérequis : **JDK 17**, Android SDK Platform **34**, Build Tools **34.0.0** et Gradle Wrapper fourni.

```powershell
cd launcher
$env:JAVA_HOME = "CHEMIN_VERS_VOTRE_JDK"
$env:ANDROID_HOME = "CHEMIN_VERS_VOTRE_SDK"
"sdk.dir=$($env:ANDROID_HOME.Replace('\','/'))" | Set-Content local.properties -Encoding ASCII
.\gradlew.bat assembleDebug --console=plain
```

APK généré : `launcher/app/build/outputs/apk/debug/tv-launcher.apk`.

La clé privée utilisée pour signer les Releases publiques **n'est pas fournie dans le dépôt**. Les APK debug ne sont pas destinés à la distribution publique.

### 🧪 Version 4.3 — validation Xiaomi Google TV

Le build **v4.3 signé** a été testé sur **Xiaomi TV Box S 2e génération** (`MiTV-AFKR0`, Android 11, firmware `773`) : grille, masquage, diagnostic RAM/stockage et persistance des préférences. Le launcher reste sélectionné après redémarrage et la touche **Home** fonctionne.

**Limitation confirmée :** les touches dédiées **Netflix, Prime Video, YouTube et Apps** ne fonctionnent plus lorsque le launcher Google TV est désactivé. Un remappage est à étudier et n'est pas encore validé sur cette Xiaomi.

**[Procédure Xiaomi détaillée, avertissements Wi-Fi et restauration Google TV](docs/XIAOMI-BOX-S-2ND-GEN.md)**. La méthode ADB comporte un risque de perte de Wi-Fi et d'accueil ; elle n'est pas générale à tous les appareils.

### 📺 Compatibilité et limites

| Élément | Valeur |
|---|---|
| Version | 4.3.0 |
| Identifiant Android | `com.gathin.tvhome` |
| Android minimum | API 26 — Android 8.0 |
| Target SDK | API 29 |
| Compile SDK | API 34 |
| Appareil de référence | Sony Bravia KD-55XF9005 |
| Xiaomi TV Box S 2e génération | Google TV 11 / firmware 773 : accueil vérifié après redémarrage ; boutons rapides indisponibles après désactivation du launcher d'origine |
| Autres appareils | Compatibilité non garantie |

Certaines fonctions (sources HDMI, informations réseau, dialogues système) dépendent du fabricant. Les chiffres de mémoire affichés sont indicatifs et ne remplacent pas une mesure PSS détaillée.

### 🤝 Contributions

Retours, suggestions et signalements de bugs sont bienvenus dans les [issues GitHub](https://github.com/vincent-deprato/tv-launcher-min/issues). Indiquez si possible le modèle de TV, la version d'Android TV, la version de TV-Launcher et les étapes pour reproduire le problème.

### 📜 Crédits et licences

Fork communautaire basé sur [CleanYourTV](https://github.com/Gathin23/cleanyourtv) par **Gathin23**. Les droits d'auteur et conditions de licence du projet d'origine, ainsi que des composants tiers tels que la police Outfit, restent applicables. Les noms et logos des applications appartiennent à leurs propriétaires respectifs.

TV-Launcher Min est indépendant et n'est ni développé ni approuvé par Sony ou Google.

---

## 🇬🇧 English

### ✨ Overview

**TV-Launcher Min v4.3.0** is a lightweight, remote-friendly Android TV home screen based on CleanYourTV. It focuses on fast app access, a clean interface and minimal unnecessary background activity.

Tested on **Sony Bravia KD-55XF9005** and **Xiaomi TV Box S 2nd gen** (Google TV 11, firmware 773). Compatibility with other devices is not guaranteed.

### 🚀 Features

- Dark, tvOS-inspired interface with clock, date and Sources / Wi-Fi / Settings shortcuts.
- Six editable favorite apps: reorder or replace them from the TV.
- Reorder, hide and restore apps in the main grid.
- Consistent menus optimized for remote-control navigation.
- Long-press an app to access its Android information screen.
- On-demand RAM/storage information without a permanent monitoring service.
- Preferences saved locally.

**What's new in v4.3:** more reliable persistence of app ordering and hidden apps, consistent confirmation menus, and graphical RAM/storage diagnostics. Tested on Sony Bravia and Xiaomi TV Box S 2nd gen.

**Known limitation:** JSON backup and restore are not available in v4.3.

### 🖼️ Feature screenshots

Historical screenshots from the **v4.2.0 Release**, captured on Sony Bravia (v4.3 may look different).

<table>
  <tr>
    <td width="50%"><img src="https://github.com/user-attachments/assets/3030eb60-a677-4c5d-b8f9-be658d0e28ac" alt="TV-Launcher Min v4.2.0 — feature screenshot 1" width="100%" /></td>
    <td width="50%"><img src="https://github.com/user-attachments/assets/8d7ac89d-775a-4e9f-8e8e-857074b483b0" alt="TV-Launcher Min v4.2.0 — feature screenshot 2" width="100%" /></td>
  </tr>
  <tr>
    <td width="50%"><img src="https://github.com/user-attachments/assets/d303019e-0cea-415c-8d41-555daf4abd79" alt="TV-Launcher Min v4.2.0 — feature screenshot 3" width="100%" /></td>
    <td width="50%"><img src="https://github.com/user-attachments/assets/dbede6d9-b3fd-453e-aeea-48528b8eca68" alt="TV-Launcher Min v4.2.0 — feature screenshot 4" width="100%" /></td>
  </tr>
</table>

### 📥 Download and installation

**[Download tv-launcher.apk — v4.3.0](https://github.com/vincent-deprato/tv-launcher-min/releases/download/4.3.0/tv-launcher.apk)**

**Without a computer:** download the APK from GitHub, transfer it to the TV by USB drive or a compatible file manager, allow installation from that source if prompted, install it, and open TV-Launcher Min. Select it as the default Home app if Android offers that option.

**Using ADB on Windows:**

1. Install [Android SDK Platform-Tools](https://developer.android.com/tools/releases/platform-tools).
2. Enable Developer options and ADB debugging on the TV. Network ADB must also be supported and enabled.
3. From PowerShell, connect and confirm the `device` status (replace the IP address **and port** with your TV's actual details):

```powershell
adb connect TV_IP_ADDRESS:5555
adb devices
```

4. Record the existing Home component *before* making changes:

```powershell
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

5. Install the downloaded APK and launch it:

```powershell
adb install -r tv-launcher.apk
adb shell am start -n com.gathin.tvhome/.MainActivity
```

6. Select TV-Launcher Min through Android's Home/default-app settings if available. Some devices support:

```powershell
adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

Confirm that the output identifies `com.gathin.tvhome/.MainActivity`. Test the **Home** key, apps, HDMI inputs and settings before rebooting to test startup.

> ⚠️ **Safety:** never disable system packages indiscriminately to force HOME. An advanced, device-specific [Xiaomi MiTV-AFKR0 firmware 773 guide](docs/XIAOMI-BOX-S-2ND-GEN.md) documents the risks of Wi-Fi loss, inaccessible HOME, and disabled remote shortcuts. Do not disable SystemUI or essential services.

### ↩️ Restore the original launcher

**Preferred:** use Android's Home/default-app settings to choose the original launcher.

If you recorded the original component from your TV, you can try:

```powershell
adb shell cmd package set-home-activity ORIGINAL_PACKAGE/ORIGINAL_ACTIVITY
```

To uninstall TV-Launcher Min:

```powershell
adb uninstall com.gathin.tvhome
```

Uninstalling normally deletes its local preferences and favorites. It will **not** automatically re-enable a stock launcher disabled separately.

### 🔧 Troubleshooting

| Message or symptom | What to do |
|---|---|
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | The existing app has a different signing certificate (often an older debug build). Uninstalling may be required and may erase preferences. |
| `device unauthorized` | Authorize the computer on the TV. |
| `device offline` | Check the connection and reconnect ADB. |
| ADB connection refused | Ensure network ADB is available and listening on the correct port. |
| Home still opens the original launcher | Review Android's selected default Home app. |
| App installed but does not launch | Check compatibility and inspect `adb logcat` if needed. |

### 🔄 Updates

Official releases can normally be installed over existing versions:

```powershell
adb install -r tv-launcher.apk
```

Settings are generally preserved if the package name (`com.gathin.tvhome`), signing certificate and version code remain compatible. A debug build may not be directly upgradeable to the officially signed release.

### 🛠️ Build from source

Requirements: **JDK 17**, Android SDK Platform **34**, Build Tools **34.0.0** and the included Gradle Wrapper.

```powershell
cd launcher
$env:JAVA_HOME = "PATH_TO_YOUR_JDK"
$env:ANDROID_HOME = "PATH_TO_YOUR_ANDROID_SDK"
"sdk.dir=$($env:ANDROID_HOME.Replace('\','/'))" | Set-Content local.properties -Encoding ASCII
.\gradlew.bat assembleDebug --console=plain
```

Debug APK: `launcher/app/build/outputs/apk/debug/tv-launcher.apk`.

Public releases use a private signing key **not included** in the repository. Debug APKs are not recommended for public distribution.

### 🧪 Version 4.3 — Xiaomi Google TV validation

The **signed v4.3 build** was tested on a **Xiaomi TV Box S 2nd gen** (`MiTV-AFKR0`, Android 11, firmware `773`): grid reordering/hiding, RAM/storage diagnostics, preference persistence, and HOME surviving a reboot.

**Confirmed limitation:** dedicated **Netflix, Prime Video, YouTube, and Apps** remote keys stopped working after disabling the stock Google TV launcher. Remapping is not yet validated on this device.

**[Xiaomi-specific installation, Wi-Fi risks and Google TV recovery guide](docs/XIAOMI-BOX-S-2ND-GEN.md)**. This ADB procedure is firmware-specific and can interrupt Wi-Fi or HOME.

### 📺 Compatibility and limitations

- Minimum Android version: **Android 8.0 (API 26)**.
- Target SDK: **29**. Compile SDK: **34**.
- Tested devices: **Sony Bravia KD-55XF9005** and **Xiaomi TV Box S 2nd gen** (Google TV 11, firmware 773).
- Other Android TV / Google TV devices: **not guaranteed**.
- Xiaomi: dedicated Netflix, Prime Video, YouTube and Apps remote buttons do not work when the Google TV launcher is disabled.
- HDMI input controls, network information and system dialogs may be OEM-dependent.
- RAM information is indicative, not a full PSS measurement.

### 🤝 Feedback and contributions

Bug reports, compatibility feedback and suggestions are welcome. Please include the TV model, Android version, launcher version and steps to reproduce.

[Open an issue](https://github.com/vincent-deprato/tv-launcher-min/issues)

### 📜 Attribution and licenses

Community fork based on [CleanYourTV](https://github.com/Gathin23/cleanyourtv) by **Gathin23**. Original copyright notices and license requirements, including third-party fonts such as Outfit, remain applicable. App names/logos are trademarks of their respective owners.

This project is not affiliated with or endorsed by Sony or Google.
