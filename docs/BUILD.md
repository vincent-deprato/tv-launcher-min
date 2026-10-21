# Compiler le projet / Build from source

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

---



Les APK Release publics utilisent une clé privée externe au dépôt. Ne jamais committer de keystore ou de secret dans GitHub.
