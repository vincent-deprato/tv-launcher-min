# Mises à jour / Updates

### 🔄 Mises à jour

En v4.4, le menu **Paramètres TV-Launcher → Rechercher une mise à jour** propose la vérification GitHub et, lorsqu'une version plus récente est disponible et vérifiée, son installation après confirmation Android. Pour la distribution initiale ou la récupération manuelle, les versions officielles peuvent normalement être installées sans désinstallation :

```powershell
.\adb.exe install -r .\tv-launcher.apk
```

Pour conserver les préférences, l'APK doit utiliser le **même identifiant** (`com.gathin.tvhome`), le **même certificat de signature** et un `versionCode` compatible. Une ancienne version **debug** ne peut pas nécessairement être remplacée directement par une Release signée.

---

### 🔄 Updates

In v4.4, **TV-Launcher Settings → Check for updates** checks GitHub and can offer a verified newer APK through the Android system installer. For initial or manual installs, official releases can normally be installed over existing versions:

```powershell
adb install -r tv-launcher.apk
```

Settings are generally preserved if the package name (`com.gathin.tvhome`), signing certificate and version code remain compatible. A debug build may not be directly upgradeable to the officially signed release.

---

[Contrat technique des Releases et sécurité / Technical release contract](UPDATER-V4.4.md)

**Testé :** v4.4 → v4.5 puis v4.5 → v4.6 sur Xiaomi TV Box S 2e génération. L'autorisation d'installation initiale sans ADB reste à confirmer sur Sony.
