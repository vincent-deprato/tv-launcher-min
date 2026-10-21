# Problèmes fréquents / Troubleshooting

### 🔧 Problèmes fréquents

| Message ou symptôme | Que faire ? |
|---|---|
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | Une version installée utilise une signature différente (par exemple une ancienne version debug). La désinstallation peut être nécessaire, avec perte possible des réglages. |
| `device unauthorized` | Confirmez l'autorisation de débogage sur la TV. |
| `device offline` | Vérifiez la connexion puis reconnectez ADB. |
| Connexion ADB refusée | Vérifiez que le débogage réseau est disponible, activé et sur le bon port. |
| Home ouvre toujours l'ancien launcher | Vérifiez l'application d'accueil sélectionnée dans Android. |
| L'installation réussit, mais l'application ne s'ouvre pas | Vérifiez la compatibilité Android et consultez au besoin `adb logcat`. |

---

### 🔧 Troubleshooting

| Message or symptom | What to do |
|---|---|
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | The existing app has a different signing certificate (often an older debug build). Uninstalling may be required and may erase preferences. |
| `device unauthorized` | Authorize the computer on the TV. |
| `device offline` | Check the connection and reconnect ADB. |
| ADB connection refused | Ensure network ADB is available and listening on the correct port. |
| Home still opens the original launcher | Review Android's selected default Home app. |
| App installed but does not launch | Check compatibility and inspect `adb logcat` if needed. |
