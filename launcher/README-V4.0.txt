TV-VibeHack v4.0 — Sony Android TV
=================================
Origine: v3.5 personnalisée.

Bouton Paramètres -> Paramètres TV-Launcher :
- Organiser/masquer les applications de la grille (hors 6 favoris)
- Diagnostic mémoire et stockage à la demande
- Sauvegarder la configuration JSON
- Restaurer la configuration JSON (confirmation)
- Paramètres Android de la TV

Bouton Modifier les favoris : intact.
Appui long sur une application : informations Android de l'application.

Compilation PowerShell (ouvrir le dossier launcher):
$env:JAVA_HOME = 'E:\Android\JDK'
$env:ANDROID_HOME = 'E:\Android'
'sdk.dir=E:/Android' | Set-Content '.\local.properties' -Encoding ASCII
.\gradlew.bat assembleDebug --console=plain

Installation sans désactiver le launcher Sony :
& 'E:\Android\platform-tools\adb.exe' connect 192.168.1.22:5555
& 'E:\Android\platform-tools\adb.exe' install -r '.\app\build\outputs\apk\debug\tv-launcher.apk'

Notes :
- Le sélecteur Android de documents doit être installé/activé pour l'export/import JSON.
- Les apps masquées restent installées et accessibles depuis les paramètres Android.
- La sauvegarde ne contient ni fichiers applicatifs ni données des applications tierces.
- Le bouton Paramètres existant ouvre désormais un menu TV-Launcher (entrée Paramètres Android).
- Validation de la compilation et du fonctionnement sur matériel Sony encore nécessaire.
