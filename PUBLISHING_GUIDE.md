# Publication sur GitHub / Publishing on GitHub

1. Copiez `README.md` et `docs/images/tv-launcher-sony.jpg` dans la racine du dépôt cloné `tv-launcher-min`. Laissez le fichier `LICENSE` du fork en place et vérifiez les autres licences/attributions.
2. Dans GitHub Desktop, contrôlez les fichiers modifiés, faites **Commit to main**, puis **Push origin**.
3. Compilez **et signez** un APK de publication. Ne publiez pas une compilation non signée ; préférez une clé de signature dédiée et sauvegardée hors du dépôt. Le script `BUILD_SIGNED_RELEASE.ps1` peut aider.
4. Vérifiez l'installation de l'APK signé sur la Sony, notamment si cette application était auparavant signée en debug (un changement de signature peut nécessiter une désinstallation et faire perdre les préférences locales).
5. Ouvrez https://github.com/vincent-deprato/tv-launcher-min/releases/new . Choisissez **Create new tag** : `v4.2.0`, target `main`, titre `TV-Launcher Min v4.2.0`.
6. Collez le contenu de `RELEASE_NOTES_v4.2.0.md` et ajoutez `tv-launcher.apk` en pièce jointe. Préférez une **pre-release** si vous n'avez testé que votre Sony. Cliquez sur **Publish release**.
7. Vérifiez le téléchargement et partagez le lien de la Release publiée.

Note : aucun APK binaire n'est inclus dans ce kit. La Release GitHub n'a pas été publiée automatiquement.
