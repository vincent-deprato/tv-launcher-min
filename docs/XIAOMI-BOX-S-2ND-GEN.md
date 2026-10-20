# Xiaomi TV Box S 2nd gen — installation Google TV (FR / EN)

## 🇫🇷 Validation v4.3 sur Xiaomi

Test du 21 octobre 2026 : Xiaomi TV Box S 2e génération, `MiTV-AFKR0` (`jaws`), Android / Google TV 11, firmware `RTT0.211222.001/773`.

**Confirmé sur un appareil :** APK v4.3 signé installé ; réorganisation et masquage des applications, menu de diagnostic RAM/stockage et sauvegarde des préférences testés ; TV-Launcher Min (`com.gathin.tvhome/.MainActivity`) démarre comme accueil et reste HOME après redémarrage. La touche Home fonctionne. La connexion Wi-Fi était disponible après redémarrage.

**Limite confirmée :** les touches de raccourci **Netflix, Prime Video, YouTube et Apps ne fonctionnent plus** après la désactivation de l'accueil Google TV. Il reste à valider une solution de remappage. D'autres fonctionnalités (Cast, assistant vocal, mises à jour, plusieurs jours de fonctionnement) n'ont pas toutes été revalidées sur cet appareil. **Ne pas considérer la compatibilité comme universelle.**

Cette procédure est inspirée des [notes Xiaomi originales de CleanYourTV](https://github.com/Gathin23/cleanyourtv/blob/main/examples/xiaomi-mi-box-s-2nd-gen/debloat.txt).

### ⚠️ Risques et prérequis

Ces commandes désactivent des composants système sans les désinstaller. Elles peuvent entraîner **un écran d'accueil vide et/ou la perte du Wi-Fi**. Si ADB passe seulement par Wi-Fi et que celui-ci tombe, la restauration ADB devient impossible à distance ; une réinitialisation peut alors être nécessaire. Ne pas procéder sans accepter ce risque. Tester d'abord la télécommande, TV-Launcher, l'accès aux paramètres et la connexion ADB. Noter l'IP et conserver le mot de passe Wi-Fi. Ne pas désactiver d'autres services Android/Google/Xiaomi.

Sur l'appareil testé, le réseau initial enregistré par `setupwraith` a été remplacé en oubliant puis reconnectant le Wi-Fi depuis **Paramètres > Réseau et Internet**. `dumpsys wifi` a alors confirmé le réseau actuellement connecté avec `cname=com.android.tv.settings` et `mostRecentlyConnected=true`. Un ancien profil appartenait encore à `setupwraith` mais n'était plus le réseau actif. Cela **réduit sans supprimer** le risque de coupure réseau.

### Diagnostic avant toute modification (PowerShell)

```powershell
$adb = "C:\platform-tools\adb.exe" # adapter
$tv = "IP_DE_VOTRE_BOX:5555"
& $adb connect $tv
& $adb -s $tv shell getprop ro.build.fingerprint
& $adb -s $tv shell cmd package query-activities --brief -a android.intent.action.MAIN -c android.intent.category.HOME
& $adb -s $tv shell pm list packages -d
& $adb -s $tv shell dumpsys wifi | Select-String 'SSID:|cname=|mostRecentlyConnected='
& $adb -s $tv shell am start -a android.settings.SETTINGS
& $adb -s $tv shell am start -n com.gathin.tvhome/.MainActivity
```

Ne pas continuer si le réseau actif dépend de `com.google.android.tungsten.setupwraith`, si le propriétaire Wi-Fi n'est pas identifiable, si l'APK ne démarre pas ou si l'accès ADB est instable. Éviter de partager la sortie Wi-Fi intégrale : elle contient des données locales.

### Préparer impérativement la restauration

```powershell
function Restore-GoogleTV {
    & $adb -s $tv shell pm enable --user 0 com.google.android.tungsten.setupwraith
    & $adb -s $tv shell pm enable --user 0 com.google.android.apps.tv.launcherx
    & $adb -s $tv shell am start -n com.google.android.apps.tv.launcherx/.home.HomeActivity
}
```

### Bascule progressive (testée sur firmware 773)

**Une opération à la fois, sans redémarrer avant la vérification HOME.**

```powershell
& $adb -s $tv shell pm disable-user --user 0 com.google.android.tungsten.setupwraith
& $adb -s $tv shell echo ADB_OK
& $adb -s $tv shell dumpsys wifi | Select-String 'mWifiInfo SSID:|mostRecentlyConnected=true'
```

Vérifier le Wi-Fi, ADB, les paramètres et Home. Si une anomalie survient, exécuter `Restore-GoogleTV` **pendant qu'ADB fonctionne encore**.

```powershell
& $adb -s $tv shell pm disable-user --user 0 com.google.android.apps.tv.launcherx
& $adb -s $tv shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
& $adb -s $tv shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

Le dernier résultat doit être `com.gathin.tvhome/.MainActivity`. Testez Home depuis plusieurs applications, les paramètres et le Wi-Fi. **Ne pas redémarrer si l'accueil ne fonctionne pas.**

```powershell
& $adb -s $tv reboot
# après redémarrage
& $adb connect $tv
& $adb -s $tv shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
```

Sur le firmware testé, HOME restait `com.gathin.tvhome/.MainActivity` après redémarrage. Désactiver `launcherx` seul **ne fonctionne pas** sur ce modèle : HOME se reporte sur `setupwraith/.RecoveryActivity` et l'appareil peut rester sans accueil après reboot.

### Restaurer Google TV

Si ADB est toujours accessible :

```powershell
& $adb -s $tv shell pm enable --user 0 com.google.android.tungsten.setupwraith
& $adb -s $tv shell pm enable --user 0 com.google.android.apps.tv.launcherx
& $adb -s $tv shell am start -n com.google.android.apps.tv.launcherx/.home.HomeActivity
```

Réactiver `setupwraith` avant une réinitialisation d'usine. Si ADB Wi-Fi a été perdu, **ces commandes ne fonctionnent pas à distance**.

### Touches Netflix / Prime Video / YouTube / Apps

**Limitation confirmée le 21/10/2026 :** ces quatre touches ne fonctionnent plus avec `launcherx` désactivé. Le [profil Xiaomi original](https://github.com/Gathin23/cleanyourtv/blob/main/examples/xiaomi-mi-box-s-2nd-gen/debloat.txt) décrit le même effet ; le [profil Chromecast](https://github.com/Gathin23/cleanyourtv/blob/main/examples/google-chromecast-gtv-4k/debloat.txt) documente une solution possible via [Key Mapper](https://github.com/keymapperorg/KeyMapper) pour une touche YouTube envoyant le keycode 190 sur **un autre appareil**. Ne pas supposer que les quatre touches Xiaomi utilisent les mêmes événements. Tester les touches physiques et les autorisations d'accessibilité au cas par cas, sans écraser globalement les paramètres d'accessibilité existants.

## 🇬🇧 English

**Tested:** Xiaomi TV Box S 2nd gen (`MiTV-AFKR0` / `jaws`), Google TV Android 11, firmware `RTT0.211222.001/773`, on 21 October 2026. Signed v4.3 build, grid reordering/hiding, diagnostics and persistence tested. `com.gathin.tvhome/.MainActivity` remained the HOME activity after a reboot, and the Home key worked.

**Confirmed limitation:** Netflix, Prime Video, YouTube and Apps remote shortcut keys stopped working once Google TV's `launcherx` package was disabled. A Key Mapper workaround may be possible but is **not validated on this Xiaomi**.

**Warning:** Disabling `setupwraith` may remove Wi-Fi networks owned by it and sever wireless ADB recovery. Check `dumpsys wifi` before attempting this procedure; the active Wi-Fi network was owned by `com.android.tv.settings` on the tested unit. Keep recovery commands ready. Follow the French step-by-step commands above. This is a firmware-specific test, not a blanket compatibility claim.
