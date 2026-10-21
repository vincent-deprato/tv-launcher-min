# Installation / Installation guide

### 📥 Téléchargement et installation

**[Télécharger tv-launcher.apk — v4.6.0](https://github.com/vincent-deprato/tv-launcher-min/releases/download/4.6.0/tv-launcher.apk)**

Deux méthodes sont possibles. **Ne désactivez pas le launcher constructeur sans avoir validé une procédure spécifique à votre modèle, ainsi qu'une méthode de restauration** : testez d'abord TV-Launcher Min et assurez-vous de pouvoir revenir à l'accueil d'origine. Sur certaines box Google TV, forcer HOME nécessite de désactiver des composants système avec un risque de perte du Wi-Fi et des raccourcis télécommande. [Procédure Xiaomi validée sur firmware 773](XIAOMI-BOX-S-2ND-GEN.md).

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

> ⚠️ **Sécurité :** ne désactivez pas de services système au hasard pour forcer le changement de launcher. Les procédures avancées, comme celle [documentée pour Xiaomi MiTV-AFKR0 firmware 773](XIAOMI-BOX-S-2ND-GEN.md), sont spécifiques au matériel et peuvent couper le Wi-Fi ou l'accueil. Ne jamais désactiver SystemUI, les composants du tuner ou d'autres services essentiels.

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

---

### 📥 Download and installation

**[Download tv-launcher.apk — v4.6.0](https://github.com/vincent-deprato/tv-launcher-min/releases/download/4.6.0/tv-launcher.apk)**

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

> ⚠️ **Safety:** never disable system packages indiscriminately to force HOME. An advanced, device-specific [Xiaomi MiTV-AFKR0 firmware 773 guide](XIAOMI-BOX-S-2ND-GEN.md) documents the risks of Wi-Fi loss, inaccessible HOME, and disabled remote shortcuts. Do not disable SystemUI or essential services.

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
