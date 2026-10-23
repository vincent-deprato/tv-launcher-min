Android-VersionCode: 19

# TV-Launcher Min v1.0.0 — Première version stable

TV-Launcher Min franchit sa première étape de publication stable après le cycle de développement 4.2.0 à 4.8.0. L'application reste légère et adaptée à la télécommande Android TV / Google TV.

## Fonctions incluses

- Accueil personnalisé : six favoris et grille d'applications réorganisable, avec masquage.
- Accès direct aux sources TV, au Wi-Fi et aux paramètres.
- Diagnostics légers à la demande : RAM système, RAM du launcher (PSS), CPU, stockage, préparation HOME et pression mémoire Android.
- Mise à jour intégrée via GitHub : vérification HTTPS, empreinte SHA-256, identité Android, signature et code de version, puis confirmation d'installation par Android.
- Pas de service permanent supplémentaire pour les diagnostics.

## Tests de la base stable

Les mises à jour **4.6 → 4.8** ont été réalisées avec succès sur **Sony Bravia** et **Xiaomi TV Box S 2e génération**, avec conservation des favoris et de tous les paramètres. Sur Sony, la première installation a nécessité d'autoriser l'application installatrice à proposer des APK.

La **1.0.0** reprend cette base fonctionnelle en réinitialisant uniquement la numérotation visible de la release. Le **versionCode Android passe de 18 à 19**, pour permettre la mise à jour en place depuis la 4.8. La migration 4.8 → 1.0.0 devra être testée sur les deux appareils après publication.

## Installer et mettre à jour

Télécharger `tv-launcher.apk` depuis cette Release. Pour une version 4.8 déjà installée, utiliser « Paramètres → Rechercher une mise à jour ». Conserver la même application installée ; **ne pas désinstaller** la 4.8 pour changer de numérotation.

Même identifiant `com.gathin.tvhome`, même certificat de publication et même stockage de préférences que les versions de développement. Le passage 1.0.0 n'est pas un retour arrière Android.
