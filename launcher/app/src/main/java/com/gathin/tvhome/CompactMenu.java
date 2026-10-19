package com.gathin.tvhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.function.IntConsumer;

/** Shared compact, opaque D-pad-friendly menu for favourites and launcher settings. */
final class CompactMenu {
    private CompactMenu() { }

    private static int dp(Context context, float value) {
        return Math.round(context.getResources().getDisplayMetrics().density * value);
    }

    private static GradientDrawable bg(Context context, int color, int border, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(context, radius));
        if (border != 0) drawable.setStroke(dp(context, 1), border);
        return drawable;
    }

    static void show(Context context, String title, String[] labels, String[] symbols,
                     int selectedIndex, IntConsumer onSelect, Runnable onBack) {
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(context, 20), dp(context, 18), dp(context, 20), dp(context, 17));
        panel.setBackground(bg(context, 0xFF1D293A, 0xFF526985, 18));

        TextView heading = new TextView(context);
        heading.setText(title);
        heading.setTextColor(Color.WHITE);
        heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        heading.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        heading.setMaxLines(2);
        heading.setPadding(dp(context, 3), 0, dp(context, 3), dp(context, 12));
        panel.addView(heading);

        View rule = new View(context);
        rule.setBackgroundColor(0xFF435A75);
        panel.addView(rule, new LinearLayout.LayoutParams(-1, dp(context, 1)));

        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(false);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setClipToPadding(false);
        LinearLayout list = new LinearLayout(context);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(dp(context, 2), dp(context, 10), dp(context, 2), dp(context, 2));
        scroll.addView(list);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(-1, -2);
        // Important: constrain long app lists so the dialog never grows off-screen.
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        scrollParams.height = Math.min(dp(context, Math.max(1, labels.length) * 49),
                Math.max(dp(context, 110), screenHeight - dp(context, 190)));
        panel.addView(scroll, scrollParams);

        AlertDialog dialog = new AlertDialog.Builder(context).setView(panel).create();
        View initial = null;
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            LinearLayout row = new LinearLayout(context);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(dp(context, 13), 0, dp(context, 12), 0);
            row.setFocusable(true);
            row.setClickable(true);
            boolean selected = index == selectedIndex;
            row.setBackground(bg(context, selected ? 0xFF3E608A : 0xFF304157,
                    selected ? 0xFF84BFFF : 0, 13));
            String symbol = symbols != null && i < symbols.length ? symbols[i] : null;
            if (symbol != null && !symbol.isEmpty()) {
                TextView pictogram = new TextView(context);
                pictogram.setText(symbol);
                pictogram.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                pictogram.setTextColor(0xFFD5E8FF);
                pictogram.setGravity(Gravity.CENTER);
                pictogram.setBackground(bg(context, 0xFF26364C, 0, 8));
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(context, 30), dp(context, 30));
                iconParams.rightMargin = dp(context, 13);
                row.addView(pictogram, iconParams);
            }
            TextView label = new TextView(context);
            label.setText(labels[i]);
            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            label.setTextColor(Color.WHITE);
            label.setGravity(Gravity.CENTER_VERTICAL);
            label.setSingleLine(true);
            label.setEllipsize(android.text.TextUtils.TruncateAt.END);
            row.addView(label, new LinearLayout.LayoutParams(0, -1, 1));
            row.setOnFocusChangeListener((v, focused) -> {
                row.setBackground(bg(context, focused ? 0xFF486F9E :
                        (selected ? 0xFF3E608A : 0xFF304157),
                        focused ? 0xFFCEE8FF : (selected ? 0xFF84BFFF : 0), 13));
            });
            row.setOnClickListener(v -> { dialog.dismiss(); onSelect.accept(index); });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(context, 43));
            lp.bottomMargin = dp(context, 6);
            list.addView(row, lp);
            if (initial == null || selected) initial = row;
        }

        TextView close = new TextView(context);
        close.setText(onBack != null ? R.string.back : R.string.cancel);
        close.setTextColor(0xFFDEEAFF);
        close.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        close.setGravity(Gravity.CENTER);
        close.setFocusable(true);
        close.setClickable(true);
        close.setBackground(bg(context, 0xFF304157, 0, 12));
        close.setOnFocusChangeListener((v, focused) ->
                close.setBackground(bg(context, focused ? 0xFF486F9E : 0xFF304157,
                        focused ? 0xFFCEE8FF : 0, 12)));
        close.setOnClickListener(v -> { dialog.dismiss(); if (onBack != null) onBack.run(); });
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(-1, dp(context, 39));
        closeParams.topMargin = dp(context, 9);
        panel.addView(close, closeParams);

        final View focusTarget = initial;
        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int width = context.getResources().getDisplayMetrics().widthPixels;
                dialog.getWindow().setLayout(Math.min(dp(context, 440), width - dp(context, 56)),
                        WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setDimAmount(0.72f);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
            if (focusTarget != null) focusTarget.requestFocus();
        });
        dialog.show();
    }
}
