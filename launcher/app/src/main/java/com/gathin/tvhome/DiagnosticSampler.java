package com.gathin.tvhome;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Build;
import android.os.Process;
import android.os.PowerManager;

/** On-demand, local-only diagnostic snapshot. No polling and no extra permissions. */
final class DiagnosticSampler {
    private DiagnosticSampler() {}

    static final class Snapshot {
        final long pssBytes; // -1 means unavailable
        final String memoryPressure;
        final String thermalStatus;
        Snapshot(long pssBytes, String memoryPressure, String thermalStatus) {
            this.pssBytes = pssBytes;
            this.memoryPressure = memoryPressure;
            this.thermalStatus = thermalStatus;
        }
    }

    static Snapshot measure(Activity activity, ActivityManager.MemoryInfo memory) {
        long pssBytes = -1;
        try {
            ActivityManager am = (ActivityManager) activity.getSystemService(Activity.ACTIVITY_SERVICE);
            if (am != null) {
                android.os.Debug.MemoryInfo[] infos =
                        am.getProcessMemoryInfo(new int[] {Process.myPid()});
                if (infos != null && infos.length == 1 && infos[0] != null) {
                    pssBytes = infos[0].getTotalPss() * 1024L;
                }
            }
        } catch (RuntimeException | LinkageError ignored) {
            // Some vendor firmwares restrict diagnostic APIs.
        }
        String pressure = memory.lowMemory ? "Faible (signalée par Android)" : "Normale (selon Android)";
        String thermal = "Indisponible (Android antérieur à 10)";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            thermal = "Indisponible";
            try {
                PowerManager power = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
                if (power != null) {
                    switch (power.getCurrentThermalStatus()) {
                        case PowerManager.THERMAL_STATUS_NONE: thermal = "Normal"; break;
                        case PowerManager.THERMAL_STATUS_LIGHT: thermal = "Léger"; break;
                        case PowerManager.THERMAL_STATUS_MODERATE: thermal = "Modéré"; break;
                        case PowerManager.THERMAL_STATUS_SEVERE: thermal = "Élevé"; break;
                        case PowerManager.THERMAL_STATUS_CRITICAL: thermal = "Critique"; break;
                        case PowerManager.THERMAL_STATUS_EMERGENCY: thermal = "Urgence"; break;
                        case PowerManager.THERMAL_STATUS_SHUTDOWN: thermal = "Arrêt imminent"; break;
                        default: thermal = "Indisponible"; break;
                    }
                }
            } catch (RuntimeException | LinkageError ignored) {
                thermal = "Indisponible";
            }
        }
        return new Snapshot(pssBytes, pressure, thermal);
    }
}
