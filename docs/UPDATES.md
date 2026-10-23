# Mises à jour / Updates

### Version 1.0.0 : continuité avec les versions de développement

Les versions 4.2.0 à 4.8.0 forment l'historique de développement. La première stable publique est **1.0.0** (Android `versionCode 19`, après `18` sur v4.8). Ne jamais remettre `versionCode` à 1 : Android refuserait l'installation par-dessus la 4.8.

La vérification intégrée utilise le `versionCode` fourni dans les notes de la dernière Release GitHub stable (`Android-VersionCode: 19`), pas l'ordre des libellés 4.8 et 1.0.0. La Release 1.0.0 doit être explicitement marquée **Latest** pour l'API `releases/latest`.

**Recette réalisée le 23 octobre 2026 :** mise à jour **4.6 → 4.8 réussie sur Sony et Xiaomi**, favoris et autres paramètres conservés. Sur Sony, Android a demandé l'autorisation d'installer des APK avant de poursuivre. **À tester après publication :** migration **4.8 → 1.0.0**, détection « à jour », conservation des réglages et fonctionnement de HOME.

Les mises à jour suivantes utiliseront `1.0.1` avec un `versionCode` strictement supérieur à 19, sans changement d'ID Android ni de certificat de signature.

---

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
