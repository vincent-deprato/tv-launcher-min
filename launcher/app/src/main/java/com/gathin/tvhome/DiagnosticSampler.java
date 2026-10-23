package com.gathin.tvhome;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.os.PowerManager;

/** On-demand readings only; no observers, background tasks or new permissions. */
final class DiagnosticSampler {
    private DiagnosticSampler() { }

    static final class Snapshot {
        final int pssKb;
        final boolean lowMemory;
        final int thermalStatus;
        Snapshot(int pssKb, boolean lowMemory, int thermalStatus) {
            this.pssKb = pssKb;
            this.lowMemory = lowMemory;
            this.thermalStatus = thermalStatus;
        }
    }

    static Snapshot sample(Context context, ActivityManager.MemoryInfo memory) {
        int pssKb = -1;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            try {
                android.os.Debug.MemoryInfo[] infos =
                        manager.getProcessMemoryInfo(new int[]{Process.myPid()});
                if (infos != null && infos.length > 0) pssKb = infos[0].getTotalPss();
            } catch (RuntimeException ignored) {
                // Do not display a fabricated memory value if the firmware restricts this API.
            }
        }
        int thermal = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (power != null) thermal = power.getCurrentThermalStatus();
            } catch (RuntimeException ignored) {
                // Some TVs do not expose a usable thermal service.
            }
        }
        return new Snapshot(pssKb, memory.lowMemory, thermal);
    }

    static String thermalLabel(int status) {
        switch (status) {
            case PowerManager.THERMAL_STATUS_NONE: return "Normal";
            case PowerManager.THERMAL_STATUS_LIGHT: return "Léger";
            case PowerManager.THERMAL_STATUS_MODERATE: return "Modéré";
            case PowerManager.THERMAL_STATUS_SEVERE: return "Élevé";
            case PowerManager.THERMAL_STATUS_CRITICAL: return "Critique";
            case PowerManager.THERMAL_STATUS_EMERGENCY: return "Urgence";
            case PowerManager.THERMAL_STATUS_SHUTDOWN: return "Arrêt thermique imminent";
            default: return "Indisponible";
        }
    }
}
