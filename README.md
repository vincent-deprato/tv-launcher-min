# 📺 TV-Launcher Min

**Un écran d'accueil Android TV / Google TV léger, personnalisable et pensé pour la télécommande.**  
*A lightweight, customizable launcher for Android TV & Google TV.*

**Version stable : v4.6.0 · Android 8.0+**

[**⬇️ Télécharger l'APK v4.6.0 / Download APK**](https://github.com/vincent-deprato/tv-launcher-min/releases/download/4.6.0/tv-launcher.apk) · [Toutes les versions / Releases](https://github.com/vincent-deprato/tv-launcher-min/releases)

![TV-Launcher Min — accueil Sony Bravia, capture v4.2](docs/images/tv-launcher-sony.png)

## 🇫🇷 En bref

- **Accueil personnalisable** : six favoris, grille réorganisable et applications masquées.
- **Navigation à la télécommande** : horloge, Wi-Fi, sources TV et paramètres.
- **Diagnostic système** : RAM, CPU et stockage, mesurés à la demande.
- **Mises à jour intégrées** : détection GitHub, téléchargement vérifié et confirmation Android.
- **Sans surveillance permanente** : les diagnostics ne tournent pas en arrière-plan.

### Aperçu du diagnostic

![Diagnostic RAM, CPU et stockage — capture Xiaomi TV Box](docs/images/diagnostic-cpu-v46.webp)

### Installer en trois étapes

1. Téléchargez `tv-launcher.apk` depuis la **Release officielle**.
2. Installez l'APK sur la télévision ou la box, puis lancez TV-Launcher Min.
3. Choisissez TV-Launcher Min comme accueil **si Android le propose**.

⚠️ **Important :** ne désactivez pas le launcher constructeur sans procédure de retour arrière. Sur certains appareils Sony et Xiaomi, la configuration nécessite ADB et peut affecter le Wi-Fi ou les touches de la télécommande.

[Installation détaillée](docs/INSTALLATION.md) · [Guide Xiaomi](docs/XIAOMI-BOX-S-2ND-GEN.md)

### Appareils testés

**Sony Bravia KD-55XF9005** (Android TV) et **Xiaomi TV Box S 2e génération** (Google TV 11, firmware 773). Sur Xiaomi, la mise à jour intégrée v4.5 → v4.6, le diagnostic CPU et la persistance de HOME après redémarrage ont été validés. Le parcours d'autorisation initiale sans ADB reste à confirmer sur Sony.

## 🇬🇧 English

TV-Launcher Min offers a **clean, remote-friendly home screen**, editable favourites, an organized app grid, on-demand RAM/CPU/storage diagnostics, and GitHub-based in-app updates.

**Quick start:** download the signed APK, install it on your Android TV device, then select it as Home if your device offers that option. **Do not disable the stock launcher without a device-specific recovery plan.**

[Installation guide](docs/INSTALLATION.md) · [Device compatibility and Xiaomi precautions](docs/XIAOMI-BOX-S-2ND-GEN.md)

## 📚 Documentation

| Guide | Contenu / Contents |
|---|---|
| [Installation](docs/INSTALLATION.md) | Installation, ADB, choix de HOME et restauration |
| [Mises à jour / Updates](docs/UPDATES.md) | Mise à jour intégrée, signature et autorisations |
| [Xiaomi TV Box S](docs/XIAOMI-BOX-S-2ND-GEN.md) | Procédure spécifique, risques et retour arrière |
| [Dépannage / Troubleshooting](docs/TROUBLESHOOTING.md) | Problèmes fréquents |
| [Historique / Changelog](docs/CHANGELOG.md) | Nouveautés et validations des versions |
| [Compilation / Build](docs/BUILD.md) | Compiler depuis les sources |
| [Spécification technique de l'updater](docs/UPDATER-V4.4.md) | Contrôles de sécurité et format des Releases |

## 🤝 Projet et crédits

Retours et suggestions : [GitHub Issues](https://github.com/vincent-deprato/tv-launcher-min/issues).

Projet communautaire basé sur [CleanYourTV](https://github.com/Gathin23/cleanyourtv) / TV-VibeHack. TV-Launcher Min n'est ni développé ni approuvé par Sony, Xiaomi ou Google. Les droits et licences du projet d'origine et des composants tiers restent applicables.
