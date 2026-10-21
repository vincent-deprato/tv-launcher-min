package com.gathin.tvhome;

import android.os.Process;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Short CPU sample requested from diagnostics only; never polls in the background. */
final class CpuSampler {
    private CpuSampler() { }

    static final class Result {
        final int percent;
        final boolean systemWide;
        Result(int percent, boolean systemWide) {
            this.percent = Math.max(0, Math.min(100, percent));
            this.systemWide = systemWide;
        }
    }

    static Result measure() throws InterruptedException {
        long[] first = readSystem();
        long processStart = Process.getElapsedCpuTime();
        long start = SystemClock.elapsedRealtime();
        Thread.sleep(650);
        long[] second = first == null ? null : readSystem();
        if (first != null && second != null) {
            long total = second[0] - first[0];
            long idle = second[1] - first[1];
            if (total > 0 && idle >= 0 && idle <= total)
                return new Result((int) Math.round((total - idle) * 100.0 / total), true);
        }
        // /proc/stat may be blocked by Android SELinux. Show *this application's*
        // normalized CPU usage instead of incorrectly labeling it system CPU.
        long elapsed = SystemClock.elapsedRealtime() - start;
        long cpu = Process.getElapsedCpuTime() - processStart;
        int cores = Math.max(1, Runtime.getRuntime().availableProcessors());
        double fraction = elapsed > 0 ? cpu * 100.0 / (elapsed * cores) : 0;
        return new Result((int) Math.round(fraction), false);
    }

    /** total, idle (idle plus iowait) from aggregate cpu line in /proc/stat. */
    private static long[] readSystem() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"))) {
            String line = reader.readLine();
            if (line == null || !line.startsWith("cpu ")) return null;
            String[] v = line.trim().split("\\s+");
            if (v.length < 5) return null;
            long total = 0;
            for (int i = 1; i < v.length; i++) total += Long.parseLong(v[i]);
            long idle = Long.parseLong(v[4]);
            if (v.length > 5) idle += Long.parseLong(v[5]);
            return new long[]{total, idle};
        } catch (IOException | NumberFormatException | SecurityException ignored) {
            return null;
        }
    }
}
