package com.gathin.tvhome;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

/** On-demand app memory and system-pressure reading, without resident observers. */
final class DiagnosticSampler {
    private DiagnosticSampler() { }

    static final class Snapshot {
        final int pssKb;
        final boolean lowMemory;
        Snapshot(int pssKb, boolean lowMemory) {
            this.pssKb = pssKb;
            this.lowMemory = lowMemory;
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
                // Firmware may restrict process memory APIs; never invent a value.
            }
        }
        return new Snapshot(pssKb, memory.lowMemory);
    }
}
