# Première signature de publication

À effectuer **une seule fois** sur votre ordinateur, dans un dossier privé **hors du dépôt Git** :

```powershell
& "E:\Android\JDK\bin\keytool.exe" -genkeypair -v -keystore "E:\Android\private-signing\tv-launcher-release.jks" -alias tvlauncher -keyalg RSA -keysize 3072 -validity 10000
```

Créez d'abord le répertoire `E:\Android\private-signing` si nécessaire. Choisissez et conservez les mots de passe. **Sauvegardez la clé hors ligne** : vous en aurez besoin pour signer toutes les mises à jour destinées aux mêmes installations.

Depuis `launcher`, lancez :

```powershell
$env:JAVA_HOME = "E:\Android\JDK"
.\BUILD_SIGNED_RELEASE.ps1 -SdkRoot "E:\Android" -KeystorePath "E:\Android\private-signing\tv-launcher-release.jks" -Alias tvlauncher
```

Copiez d'abord `BUILD_SIGNED_RELEASE.ps1` dans le dossier `launcher` ou lancez-le par son chemin.

La sortie signée se trouve dans `launcher\release-dist\tv-launcher.apk` après vérification. Si l'application actuellement installée sur votre TV a été signée avec une **autre clé** (par exemple la clé debug), Android refusera la mise à jour en place ; il faudra choisir entre conserver l'ancienne installation ou la désinstaller avant d'installer le nouvel APK. Une désinstallation peut supprimer les préférences du launcher.

**Ne committez jamais** votre `.jks`, vos mots de passe, votre `local.properties` ou vos APK temporaires.
