# Contribuer à TV-Launcher Min

TV-Launcher Min est un **launcher Android TV / Google TV volontairement simple et léger**. Les contributions doivent privilégier les corrections mesurables, la stabilité de HOME et la navigation à la télécommande.

## Proposer une correction

1. Ouvrir une [issue](https://github.com/vincent-deprato/tv-launcher-min/issues) avec le modèle du téléviseur, le firmware, la version du launcher et les étapes pour reproduire le problème.
2. Créer une branche dédiée et une Pull Request avec une description précise des fichiers modifiés.
3. Faire passer la CI (tests documentaires, puis compilation Android Debug pour les changements du launcher).
4. Pour les changements d'affichage, vérifier le focus D-pad, les favoris, le retour à HOME et le redémarrage sur un appareil réel.

## Principes techniques

- Conserver l'identifiant `com.gathin.tvhome` et la compatibilité des préférences.
- Pas de publicité, de télémétrie, de service permanent ni d'animation coûteuse.
- L'application **dispose déjà d'un accès Internet limité à son système de mise à jour GitHub**. Toute nouvelle utilisation du réseau doit être explicitement justifiée.
- Ne jamais désactiver silencieusement des applications système ou supprimer les possibilités de restauration de HOME.
- Mesurer les performances sur Sony Bravia et Xiaomi TV Box avant d'affirmer une amélioration.
- Ne jamais committer de keystore, secret, identifiant d'appareil ou fichier local `signing.properties`.

## Compiler

Voir [la procédure de compilation](docs/BUILD.md) et [les règles de mise à jour](docs/UPDATES.md).

## Documents historiques

Ce dépôt conserve encore des listes de debloat et des scripts provenant de [CleanYourTV](https://github.com/Gathin23/cleanyourtv). Ils ne sont pas nécessaires au fonctionnement normal de l'APK. Leur éventuel archivage fait partie de [l'audit v4.7](docs/V4.7-FORK-CLEANUP-AUDIT.md) et ne doit pas supprimer les procédures ADB de restauration sans vérification.
