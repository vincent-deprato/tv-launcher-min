# Publication TV-Launcher Min / Release process

## Première version stable : 1.0.0

L'historique **4.2.0 à 4.8.0** correspond aux versions de développement/validation. La **1.0.0** est la première version publique stable, basée sur la v4.8 testée sur Sony Bravia et Xiaomi.

La publication est automatisée par `.github/workflows/publish-v1.yml` lors de sa fusion dans `main` (ou manuellement via **Actions → Publish TV-Launcher Min v1.0.0 → Run workflow** si besoin). Les étapes de la publication :

1. Installer Java et Android SDK, exécuter les tests et produire l'APK signé.
2. Vérifier l'identifiant Android `com.gathin.tvhome`, `versionName 1.0.0`, `versionCode 19` et l'empreinte du certificat de signature.
3. Publier l'APK dans la Release GitHub taguée `1.0.0`, **définie comme Latest**, avec `Android-VersionCode: 19` dans les notes.
4. Une fois la stable publiée, reclasser les versions 4.2.0–4.8.0 en **prerelease**, sans modifier ni supprimer les tags.
5. Vérifier que `releases/latest` pointe vers 1.0.0 et fournit `tv-launcher.apk`.

### Pré-requis

Le dépôt GitHub doit conserver les secrets de l'environnement `release-signing` : `TV_KEYSTORE_BASE64`, `TV_STORE_PASSWORD`, `TV_KEY_ALIAS`, `TV_KEY_PASSWORD`. La clé doit être identique à celle des anciennes Releases. Ne jamais publier une APK debug ni committer la clé ou des identifiants.

### Mise à jour en place

Ne **jamais** remettre le `versionCode` Android à 1 : la dernière version de développement installée utilise le code **18**. La stable 1.0.0 utilise donc **19**, et les prochaines versions devront utiliser 20, 21, etc. `UpdateManager` compare ces codes, pas les numéros lisibles.

Avant toute publication suivante, mettre à jour la version dans `launcher/app/build.gradle`, puis la Release et son fichier de notes. Conserver le même ID `com.gathin.tvhome`, la même clé de signature, et l'asset nommé `tv-launcher.apk`.

### Vérifications post-publication

- Contrôler `https://github.com/vincent-deprato/tv-launcher-min/releases/latest` et le téléchargement APK.
- Depuis une Sony et une Xiaomi en 4.8, lancer **Paramètres → Rechercher une mise à jour**, puis installer 1.0.0.
- Vérifier que les favoris, les paramètres, la grille HOME et la sélection du launcher sont conservés.
- Si l'installation est bloquée sur une TV, vérifier l'autorisation Android d'installer des APK pour l'application installatrice.

Les anciens guides locaux v4.2 de publication restent remplacés par ce workflow. Voir `docs/UPDATES.md` pour le contrat de l'updater.
