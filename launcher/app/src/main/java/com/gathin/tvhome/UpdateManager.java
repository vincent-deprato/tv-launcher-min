package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * On-demand GitHub Releases updater with a single weekly foreground-start check.
 * No background service or scheduled wakeups. Installs only after the OS confirmation.
 */
final class UpdateManager {
    private static final String RELEASE_API =
            "https://api.github.com/repos/vincent-deprato/tv-launcher-min/releases/latest";
    private static final String DOWNLOAD_PREFIX =
            "https://github.com/vincent-deprato/tv-launcher-min/releases/download/";
    private static final long WEEK_MS = 7L * 24 * 60 * 60 * 1000;
    private static final long MAX_APK_BYTES = 100L * 1024 * 1024;
    private static final String PREFS = "launcher_updates_v44";
    private static final String KEY_CHECKED = "last_successful_check";
    private static final String KEY_NOTIFIED = "last_notified_code";
    private final Activity activity;
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private boolean busy;

    private static final class Release {
        final String version;
        final int code;
        final String url;
        final String digest;
        Release(String version, int code, String url, String digest) {
            this.version = version;
            this.code = code;
            this.url = url;
            this.digest = digest;
        }
    }

    UpdateManager(Activity activity) { this.activity = activity; }

    void checkAutomatic() {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        long last = prefs.getLong(KEY_CHECKED, 0);
        if (last > 0 && now >= last && now - last < WEEK_MS) return;
        check(false);
    }

    void checkManual() { check(true); }

    private void check(boolean manual) {
        if (busy) {
            if (manual) toast("Une vérification est déjà en cours");
            return;
        }
        busy = true;
        if (manual) toast("Recherche des mises à jour…");
        worker.execute(() -> {
            Release release = null;
            String error = null;
            try { release = fetchRelease(); }
            catch (Exception e) { error = e.getMessage(); }
            final Release result = release;
            final String failure = error;
            ui.post(() -> {
                busy = false;
                if (activity.isFinishing() || activity.isDestroyed()) return;
                if (failure != null) {
                    if (manual) showMessage("Mise à jour", "Vérification impossible : " + failure);
                    return; // retry on next start when offline or GitHub is unavailable
                }
                activity.getSharedPreferences(PREFS, Activity.MODE_PRIVATE)
                        .edit().putLong(KEY_CHECKED, System.currentTimeMillis()).apply();
                if (result == null || result.code <= BuildConfig.VERSION_CODE) {
                    if (manual) showMessage("Mise à jour",
                            "TV-Launcher Min " + BuildConfig.VERSION_NAME + " est à jour.");
                    return;
                }
                if (manual) showAvailable(result);
                else {
                    SharedPreferences prefs = activity.getSharedPreferences(PREFS, Activity.MODE_PRIVATE);
                    if (prefs.getInt(KEY_NOTIFIED, 0) != result.code) {
                        prefs.edit().putInt(KEY_NOTIFIED, result.code).apply();
                        toast("TV-Launcher " + result.version + " disponible dans Paramètres > Mise à jour");
                    }
                }
            });
        });
    }

    /** Only the official repository's stable APK; never take an arbitrary asset URL. */
    private Release fetchRelease() throws Exception {
        HttpURLConnection conn = connect(RELEASE_API);
        try {
            if (conn.getResponseCode() != 200) throw new Exception("GitHub HTTP " + conn.getResponseCode());
            JSONObject release = readJson(conn.getInputStream());
            if (release.optBoolean("draft", true) || release.optBoolean("prerelease", true)) return null;
            String tag = release.optString("tag_name", "").replaceFirst("^v", "");
            if (!tag.matches("[0-9]+\\.[0-9]+(\\.[0-9]+)?")) throw new Exception("Version GitHub invalide");
            // Older releases predate the updater metadata contract; show "up to date" instead of an error.
            // Android versionCode is authoritative. Read it from official release metadata, never guess
            // from the human-readable tag.
            String body = release.optString("body", "");
            java.util.regex.Matcher codeMatch = java.util.regex.Pattern
                    .compile("(?im)^\\s*Android-VersionCode:\\s*(\\d+)\\s*$").matcher(body);
            if (!codeMatch.find()) throw new Exception("La Release ne précise pas Android-VersionCode");
            int code = Integer.parseInt(codeMatch.group(1));
            if (code <= BuildConfig.VERSION_CODE) return null;
            JSONArray assets = release.optJSONArray("assets");
            if (assets == null) throw new Exception("APK absent");
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                if (!"tv-launcher.apk".equals(asset.optString("name"))) continue;
                String url = asset.optString("browser_download_url", "");
                if (!url.startsWith(DOWNLOAD_PREFIX) || !url.endsWith("/tv-launcher.apk"))
                    throw new Exception("URL APK non reconnue");
                String digest = asset.optString("digest", "");
                if (!digest.matches("sha256:[0-9a-fA-F]{64}"))
                    throw new Exception("Empreinte SHA-256 officielle absente");
                long size = asset.optLong("size", 0);
                if (size <= 0 || size > MAX_APK_BYTES) throw new Exception("Taille APK incorrecte");
                return new Release(tag, code, url, digest.substring(7).toLowerCase(Locale.ROOT));
            }
            throw new Exception("tv-launcher.apk introuvable");
        } finally { conn.disconnect(); }
    }

    private JSONObject readJson(InputStream in) throws Exception {
        try (InputStream stream = in; java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = stream.read(buffer)) != -1) {
                if (out.size() + n > 1024 * 1024) throw new Exception("Réponse GitHub trop grande");
                out.write(buffer, 0, n);
            }
            return new JSONObject(out.toString("UTF-8"));
        }
    }

    private static HttpURLConnection connect(String url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(20000);
        conn.setRequestProperty("User-Agent", "TV-Launcher-Min-Android-Updater");
        conn.setRequestProperty("Accept", "application/vnd.github+json");
        conn.setInstanceFollowRedirects(true);
        return conn;
    }

    private void showAvailable(Release release) {
        new AlertDialog.Builder(activity)
                .setTitle("Mise à jour disponible")
                .setMessage("Version actuelle : " + BuildConfig.VERSION_NAME
                        + "\nNouvelle version : " + release.version
                        + "\n\nTélécharger l'APK signé officiel depuis GitHub ?")
                .setNegativeButton("Plus tard", null)
                .setPositiveButton("Télécharger", (d, w) -> download(release))
                .show();
    }

    private void download(Release release) {
        if (busy) return;
        busy = true;
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        int p = Math.round(24 * activity.getResources().getDisplayMetrics().density);
        layout.setPadding(p, p, p, p);
        TextView label = new TextView(activity);
        label.setText("Téléchargement : 0 %");
        label.setTextColor(Color.WHITE);
        ProgressBar progress = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        layout.addView(label);
        layout.addView(progress);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("TV-Launcher " + release.version)
                .setView(layout)
                .setNegativeButton("Annuler", null)
                .create();
        final boolean[] cancelled = {false};
        dialog.setOnDismissListener(d -> cancelled[0] = true);
        dialog.show();

        worker.execute(() -> {
            File apk = new File(new File(activity.getCacheDir(), "updates"), "tv-launcher.apk");
            String error = null;
            try {
                File dir = apk.getParentFile();
                if (!dir.exists() && !dir.mkdirs()) throw new Exception("Stockage indisponible");
                HttpURLConnection conn = connect(release.url);
                try {
                    if (conn.getResponseCode() != 200) throw new Exception("Téléchargement HTTP " + conn.getResponseCode());
                    String resolved = conn.getURL().toString();
                    if (!resolved.startsWith("https://")) throw new Exception("Téléchargement non sécurisé");
                    long total = conn.getContentLengthLong();
                    if (total > MAX_APK_BYTES) throw new Exception("APK trop volumineux");
                    MessageDigest sha = MessageDigest.getInstance("SHA-256");
                    long done = 0;
                    try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(apk)) {
                        byte[] buffer = new byte[16384];
                        int n;
                        int previous = -1;
                        while ((n = in.read(buffer)) != -1) {
                            if (cancelled[0]) throw new Exception("Téléchargement annulé");
                            done += n;
                            if (done > MAX_APK_BYTES) throw new Exception("APK trop volumineux");
                            out.write(buffer, 0, n);
                            sha.update(buffer, 0, n);
                            int pct = total > 0 ? (int) Math.min(100, done * 100 / total) : 0;
                            if (pct != previous) {
                                previous = pct;
                                final int update = pct;
                                ui.post(() -> { if (!cancelled[0]) { progress.setProgress(update); label.setText("Téléchargement : " + update + " %"); } });
                            }
                        }
                    }
                    if (done == 0 || (total > 0 && total != done)) throw new Exception("Téléchargement incomplet");
                    StringBuilder hash = new StringBuilder();
                    for (byte b : sha.digest()) hash.append(String.format(Locale.ROOT, "%02x", b & 0xff));
                    if (!hash.toString().equals(release.digest)) throw new Exception("Empreinte SHA-256 incorrecte");
                    verifyApk(apk, release.code);
                } finally { conn.disconnect(); }
            } catch (Exception e) {
                error = e.getMessage();
                apk.delete();
            }
            String failure = error;
            ui.post(() -> {
                busy = false;
                if (!dialog.isShowing() || activity.isFinishing() || activity.isDestroyed()) return;
                dialog.setOnDismissListener(null);
                dialog.dismiss();
                if (failure != null) {
                    if (!"Téléchargement annulé".equals(failure)) showMessage("Échec de la mise à jour", failure);
                } else install(apk);
            });
        });
    }

    private void verifyApk(File apk, int code) throws Exception {
        PackageManager pm = activity.getPackageManager();
        int flags = Build.VERSION.SDK_INT >= 28
                ? PackageManager.GET_SIGNING_CERTIFICATES : PackageManager.GET_SIGNATURES;
        PackageInfo candidate = pm.getPackageArchiveInfo(apk.getAbsolutePath(), flags);
        PackageInfo installed = pm.getPackageInfo(activity.getPackageName(), flags);
        if (candidate == null || !activity.getPackageName().equals(candidate.packageName))
            throw new Exception("Identifiant de l'APK incorrect");
        if (candidate.versionCode != code || candidate.versionCode <= BuildConfig.VERSION_CODE)
            throw new Exception("Version Android incompatible");
        Signature[] expected = signatures(installed);
        Signature[] actual = signatures(candidate);
        // Some Android TV firmwares provide no signingInfo for an APK archive even
        // when GET_SIGNING_CERTIFICATES was requested. Retry using legacy signatures
        // rather than reporting an incorrect certificate mismatch.
        if (expected.length == 0 || actual.length == 0) {
            PackageInfo fallbackCandidate = pm.getPackageArchiveInfo(
                    apk.getAbsolutePath(), PackageManager.GET_SIGNATURES);
            PackageInfo fallbackInstalled = pm.getPackageInfo(
                    activity.getPackageName(), PackageManager.GET_SIGNATURES);
            if (expected.length == 0) expected = signatures(fallbackInstalled);
            if (actual.length == 0) actual = signatures(fallbackCandidate);
        }
        if (expected.length == 0 || actual.length == 0)
            throw new Exception("Signature APK non lisible sur cet appareil (vérification impossible)");
        if (!sameSigners(expected, actual))
            throw new Exception("Signature APK différente de la version installée");
    }

    /** Signer array ordering does not establish certificate identity. */
    private static boolean sameSigners(Signature[] expected, Signature[] actual) {
        if (expected.length != actual.length) return false;
        java.util.List<Signature> remaining = new java.util.ArrayList<>(Arrays.asList(actual));
        for (Signature signature : expected) {
            if (!remaining.remove(signature)) return false;
        }
        return remaining.isEmpty();
    }

    private Signature[] signatures(PackageInfo info) {
        if (info == null) return new Signature[0];
        if (Build.VERSION.SDK_INT >= 28 && info.signingInfo != null) {
            Signature[] signing = info.signingInfo.hasMultipleSigners()
                    ? info.signingInfo.getApkContentsSigners()
                    : info.signingInfo.getSigningCertificateHistory();
            if (signing != null && signing.length > 0) return signing;
        }
        return info.signatures == null ? new Signature[0] : info.signatures;
    }

    /**
     * Unknown-source permission is controlled by the firmware, not by the app.
     * Some Google TV builds omit sideloaded launchers from the permission list.
     * Never silently grant app-ops or claim we can bypass the Android installer.
     */
    private void install(File apk) {
        if (Build.VERSION.SDK_INT >= 26
                && !activity.getPackageManager().canRequestPackageInstalls()) {
            new AlertDialog.Builder(activity)
                    .setTitle("Autoriser les mises à jour")
                    .setMessage("Android n'autorise pas encore TV-Launcher à proposer une installation. "
                            + "Ouvrez les paramètres pour autoriser cette source.\n\n"
                            + "Si TV-Launcher n'apparaît pas dans la liste Google TV, essayez "
                            + "« Ouvrir l'installateur » : Android peut alors demander l'autorisation "
                            + "directement. Selon le firmware, cela peut rester impossible sans ADB.")
                    .setPositiveButton("Paramètres Android", (d, w) -> openUnknownSourceSettings())
                    .setNeutralButton("Ouvrir l'installateur", (d, w) -> launchInstaller(apk))
                    .setNegativeButton("Plus tard", null)
                    .show();
            return;
        }
        launchInstaller(apk);
    }

    private void openUnknownSourceSettings() {
        try {
            Intent settings = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(settings);
        } catch (ActivityNotFoundException | SecurityException e) {
            try {
                activity.startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
            } catch (ActivityNotFoundException | SecurityException ignored) {
                showMessage("Paramètres indisponibles",
                        "Votre firmware ne permet pas d'ouvrir l'autorisation d'installation depuis TV-Launcher.");
            }
        }
    }

    private void launchInstaller(File apk) {
        try {
            Uri uri = FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".fileprovider", apk);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // Carry a ClipData grant for OEM Package Installer implementations.
            intent.setClipData(android.content.ClipData.newRawUri("TV-Launcher APK", uri));
            activity.startActivity(intent); // Android always owns the confirmation.
        } catch (ActivityNotFoundException | SecurityException e) {
            showMessage("Installation impossible",
                    "L'installateur Android ne peut pas ouvrir l'APK : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showMessage("Installation impossible", "Le fichier APK n'est pas accessible.");
        }
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(activity).setTitle(title).setMessage(message)
                .setPositiveButton("OK", null).show();
    }

    private void toast(String text) { Toast.makeText(activity, text, Toast.LENGTH_LONG).show(); }

    void shutdown() { worker.shutdownNow(); }
}
