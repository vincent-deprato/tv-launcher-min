package com.gathin.tvhome;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Gravity;
import android.util.TypedValue;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * App tiles. The first {@link MainActivity#COLUMNS} positions are editable favourites,
 * the rest is alphabetical.
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.Holder> {

    interface TileListener {
        void onTileFocused(int position, View card);

        /** Called for D-pad Up/Down on a tile. Return true when handled. */
        boolean onTileVerticalKey(int position, int keyCode);
        void onFavouritesReordered(String packageName, int position);
    }

    static final class Entry {
        final String label;
        final ComponentName component;
        final String pkg;
        final Drawable banner;
        final Drawable icon;
        int ambient;

        Entry(String label, ComponentName component, Drawable banner, Drawable icon) {
            this.label = label;
            this.component = component;
            this.pkg = component.getPackageName();
            this.banner = banner;
            this.icon = icon;
        }
    }

    static final float FOCUS_SCALE = 1.10f;

    private final Context ctx;
    private final TileListener listener;
    private final int dockSize;
    /** Dock order (res/values/config.xml, dock_apps). */
    private final List<String> favourites;
    private static final String PREFS = "dock_preferences";
    private static final String ORDER_KEY = "favourites_order_v1";
    /** Never shown as tiles (res/values/config.xml, hidden_apps). */
    private final Set<String> hidden;
    private final List<String> customOrder = new ArrayList<>();
    private static final String HIDDEN_KEY = "hidden_apps_v4";
    private static final String GRID_KEY = "grid_order_v4";
    private static final String GRID_SAVED_KEY = "grid_state_initialized_v43";
    private final List<Entry> items = new ArrayList<>();

    AppAdapter(Context ctx, TileListener listener, int dockSize) {
        this.ctx = ctx;
        this.listener = listener;
        this.dockSize = dockSize;
        this.favourites = new ArrayList<>(Arrays.asList(ctx.getResources().getStringArray(R.array.dock_apps)));
        String stored = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(ORDER_KEY, "");
        if (!stored.isEmpty()) {
            List<String> saved = Arrays.asList(stored.split("\\|"));
            List<String> merged = new ArrayList<>();
            for (String pkg : saved) if (!pkg.isEmpty() && !merged.contains(pkg)) merged.add(pkg);
            for (String pkg : favourites) if (!merged.contains(pkg)) merged.add(pkg);
            favourites.clear();
            favourites.addAll(merged);
        }
        this.hidden = new HashSet<>(Arrays.asList(ctx.getResources().getStringArray(R.array.hidden_apps)));
        android.content.SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String hiddenSaved = prefs.getString(HIDDEN_KEY, "");
        if (!hiddenSaved.isEmpty()) hidden.addAll(Arrays.asList(hiddenSaved.split("\\|")));
        String gridSaved = prefs.getString(GRID_KEY, "");
        if (!gridSaved.isEmpty()) for (String pkg : gridSaved.split("\\|")) if (!pkg.isEmpty() && !customOrder.contains(pkg)) customOrder.add(pkg);
        // Preserve existing v4 entries, then snapshot even an empty grid state to disk.
        // The migration does not reset favorites or previously saved positions.
        // Tiles keep their identity across reload(), so a focused tile stays on its app instead of
        // its view being rebound to whatever app now sits in that slot.
        setHasStableIds(true);
    }

    void reload() {
        PackageManager pm = ctx.getPackageManager();
        Intent probe = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> found = pm.queryIntentActivities(probe, 0);
        List<Entry> fresh = new ArrayList<>();
        for (ResolveInfo ri : found) {
            if (ri.activityInfo == null) continue;
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(ctx.getPackageName()) || hidden.contains(pkg)) continue;
            Drawable banner = ri.activityInfo.loadBanner(pm);
            if (banner == null) banner = ri.activityInfo.applicationInfo.loadBanner(pm);
            Drawable icon = ri.loadIcon(pm);
            String label = String.valueOf(ri.loadLabel(pm));
            fresh.add(new Entry(label, new ComponentName(pkg, ri.activityInfo.name), banner, icon));
        }
        // Index the saved positions once rather than repeatedly scanning lists during sorting.
        // These maps exist only for this reload and do not retain any application resources.
        Map<String, Integer> favouritePositions = new HashMap<>();
        for (int i = 0; i < favourites.size(); i++) {
            favouritePositions.putIfAbsent(favourites.get(i), i);
        }
        Map<String, Integer> gridPositions = new HashMap<>();
        for (int i = 0; i < customOrder.size(); i++) {
            gridPositions.putIfAbsent(customOrder.get(i), i);
        }
        Collections.sort(fresh, (a, b) -> {
            int fa = favouritePositions.containsKey(a.pkg) ? favouritePositions.get(a.pkg) : -1;
            int fb = favouritePositions.containsKey(b.pkg) ? favouritePositions.get(b.pkg) : -1;
            if (fa >= 0 || fb >= 0) {
                if (fa < 0) return 1;
                if (fb < 0) return -1;
                return Integer.compare(fa, fb);
            }
            int ia = gridPositions.containsKey(a.pkg) ? gridPositions.get(a.pkg) : -1;
            int ib = gridPositions.containsKey(b.pkg) ? gridPositions.get(b.pkg) : -1;
            if (ia >= 0 || ib >= 0) {
                if (ia < 0) return 1;
                if (ib < 0) return -1;
                return Integer.compare(ia, ib);
            }
            return a.label.compareToIgnoreCase(b.label);
        });
        items.clear();
        items.addAll(fresh);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        // Each tile must fit its grid cell, including breathing room for the focus zoom.
        // The root layout has 56dp horizontal padding on either side (activity_main.xml).
        float density = ctx.getResources().getDisplayMetrics().density;
        float screenDp = ctx.getResources().getDisplayMetrics().widthPixels / density;
        float usableDp = Math.max(0f, screenDp - 112f);
        int tileDp = Math.max(64, Math.min(180, (int) (usableDp / dockSize) - 18));
        int tileHeightDp = Math.round(tileDp * 101f / 180f);
        FrameLayout.LayoutParams cardParams = (FrameLayout.LayoutParams) v.findViewById(R.id.card).getLayoutParams();
        cardParams.width = Math.round(tileDp * density);
        cardParams.height = Math.round(tileHeightDp * density);
        v.findViewById(R.id.card).setLayoutParams(cardParams);
        FrameLayout.LayoutParams nameParams = (FrameLayout.LayoutParams) v.findViewById(R.id.name).getLayoutParams();
        nameParams.width = Math.round(tileDp * density);
        nameParams.topMargin = Math.round((tileHeightDp + 7) * density);
        v.findViewById(R.id.name).setLayoutParams(nameParams);
        return new Holder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Entry e = items.get(position);
        if (h.pendingLongPress != null) h.card.removeCallbacks(h.pendingLongPress);
        h.pendingLongPress = null;
        h.keyHeld = false;
        h.longTriggered = false;
        h.name.setText(e.label);
        h.name.setVisibility(position < dockSize ? View.INVISIBLE : View.VISIBLE);   // the dock has no labels
        boolean focused = h.card.isFocused();   // a rebind after reload() can hit the focused tile
        h.name.setAlpha(focused ? 1f : 0f);
        h.card.setScaleX(focused ? FOCUS_SCALE : 1f);
        h.card.setScaleY(focused ? FOCUS_SCALE : 1f);
        if (e.banner != null) {
            h.banner.setImageDrawable(e.banner);
            h.banner.setVisibility(View.VISIBLE);
            h.icon.setVisibility(View.GONE);
            h.label.setVisibility(View.GONE);
        } else {
            h.banner.setImageDrawable(null);
            h.banner.setVisibility(View.GONE);
            h.icon.setImageDrawable(e.icon);
            h.icon.setVisibility(View.VISIBLE);
            h.label.setText(e.label);
            h.label.setVisibility(View.VISIBLE);
        }
        // Some Android TV remotes do not dispatch View long-click for DPAD_CENTER.
        // Handle the actual key-down/key-up sequence, plus MENU as an alternative.
        h.card.setOnKeyListener((view, keyCode, event) -> {
            int current = h.getBindingAdapterPosition();
            if (current == RecyclerView.NO_POSITION) return false;
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                return event.getAction() == KeyEvent.ACTION_DOWN
                        && listener != null && listener.onTileVerticalKey(current, keyCode);
            }
            if (keyCode == KeyEvent.KEYCODE_MENU && current < dockSize) {
                if (event.getAction() == KeyEvent.ACTION_UP) showFavouriteActions(current);
                return true;
            }
            // Let Android TV handle OK/Enter normally so long-press opens app settings again.
            return false;
        });
        h.card.setOnClickListener(v -> launch(e));
        h.card.setOnLongClickListener(v -> {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + e.pkg));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try { ctx.startActivity(i); } catch (ActivityNotFoundException ignored) { }
            return true;
        });
    }

    /** Entry point for the visible Manage favourites button, accessible by remote. */
    void showFavouritesManager() {
        int count = Math.min(dockSize, items.size());
        if (count == 0) {
            Toast.makeText(ctx, R.string.no_favourites, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] choices = new String[count];
        for (int i = 0; i < count; i++) {
            choices[i] = (i + 1) + " - " + items.get(i).label;
        }
        showGlassMenu(ctx.getString(R.string.manage_favourites), choices, -1,
                which -> showFavouriteActions(which), null);
    }

    /** Long OK on a favourite: reorder it or replace it with an installed application. */
    private void showFavouriteActions(int source) {
        if (source < 0 || source >= Math.min(dockSize, items.size())) return;
        String appLabel = items.get(source).label;
        showGlassMenu(ctx.getString(R.string.favourite_title, appLabel, source + 1),
                new String[]{ctx.getString(R.string.favourite_change_position), ctx.getString(R.string.favourite_replace)},
                -1, choice -> {
                    if (choice == 0) showPositionDialog(source);
                    else showReplacementDialog(source);
                }, null);
    }

    /** Show the TV apps already listed by the launcher, excluding current dock apps. */
    private void showReplacementDialog(int source) {
        int count = Math.min(dockSize, items.size());
        if (source < 0 || source >= count) return;
        List<Entry> candidates = new ArrayList<>();
        Set<String> pinned = new HashSet<>();
        for (int i = 0; i < count; i++) pinned.add(items.get(i).pkg);
        Set<String> added = new HashSet<>();
        for (Entry entry : items) {
            if (!pinned.contains(entry.pkg) && added.add(entry.pkg)) candidates.add(entry);
        }
        if (candidates.isEmpty()) {
            Toast.makeText(ctx, R.string.no_other_apps, Toast.LENGTH_LONG).show();
            return;
        }
        Collections.sort(candidates, (a, b) -> a.label.compareToIgnoreCase(b.label));
        String[] choices = new String[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) choices[i] = candidates.get(i).label;
        showGlassMenu(ctx.getString(R.string.replace_title, items.get(source).label),
                choices, -1, selected -> {
                    Entry replacement = candidates.get(selected);
                    List<String> updated = new ArrayList<>();
                    for (int i = 0; i < count; i++) {
                        updated.add(i == source ? replacement.pkg : items.get(i).pkg);
                    }
                    // Do not leave the removed app in the favourites: it returns to the normal grid.
                    // Retain unavailable favourites only if there is still a free dock slot.
                    String oldPkg = items.get(source).pkg;
                    for (String pkg : favourites) {
                        if (updated.size() >= dockSize) break;
                        if (!pkg.equals(oldPkg) && !updated.contains(pkg)) updated.add(pkg);
                    }
                    favourites.clear();
                    favourites.addAll(updated);
                    saveFavourites();
                    reload();
                    if (listener != null) listener.onFavouritesReordered(replacement.pkg, source);
                    Toast.makeText(ctx, ctx.getString(R.string.favourite_added, replacement.label), Toast.LENGTH_SHORT).show();
                }, () -> showFavouriteActions(source));
    }

    /** Manage the normal grid using the same compact remote-friendly menu as favourites. */
    void showGridManager() {
        List<Entry> normal = normalGridEntries();
        String[] labels = new String[normal.size() + 1];
        String[] icons = new String[labels.length];
        labels[0] = "Applications masquées";
        icons[0] = "◉";
        for (int i = 0; i < normal.size(); i++) {
            labels[i + 1] = (i + 1) + " · " + normal.get(i).label;
            icons[i + 1] = "▦";
        }
        CompactMenu.show(ctx, "Organiser les applications", labels, icons, -1,
            chosen -> {
                if (chosen == 0) showHiddenManager();
                else showGridActions(normal.get(chosen - 1).pkg);
            }, null);
    }

    private List<Entry> normalGridEntries() {
        List<Entry> normal = new ArrayList<>();
        for (int i = Math.min(dockSize, items.size()); i < items.size(); i++) normal.add(items.get(i));
        return normal;
    }

    private void showGridActions(String pkg) {
        Entry selected = null;
        for (Entry entry : normalGridEntries()) if (entry.pkg.equals(pkg)) { selected = entry; break; }
        if (selected == null) return;
        final String label = selected.label;
        CompactMenu.show(ctx, label,
            new String[]{"Déplacer dans la grille", "Masquer de l'accueil"},
            new String[]{"↕", "⊘"}, -1, choice -> {
                if (choice == 0) showGridPositions(pkg);
                else CompactMenu.show(ctx, "Masquer " + label + " ?",
                    new String[]{"Confirmer le masquage", "Annuler"},
                    new String[]{"⊘", "←"}, 1, selectedAction -> {
                        if (selectedAction == 0) {
                            hidden.add(pkg);
                            persistGrid();
                            reload();
                            Toast.makeText(ctx, label + " masquée", Toast.LENGTH_SHORT).show();
                        } else showGridActions(pkg);
                    }, () -> showGridActions(pkg));
            }, this::showGridManager);
    }

    private void showGridPositions(String pkg) {
        List<Entry> normal = normalGridEntries();
        int original = -1;
        String name = pkg;
        for (int i = 0; i < normal.size(); i++) {
            if (normal.get(i).pkg.equals(pkg)) { original = i; name = normal.get(i).label; break; }
        }
        if (original < 0) return;
        String[] labels = new String[normal.size()];
        for (int i = 0; i < normal.size(); i++) labels[i] = (i + 1) + " · " + normal.get(i).label;
        final String selectedLabel = name;
        CompactMenu.show(ctx, "Nouvelle position · " + name, labels, null, original,
            target -> {
                List<String> order = new ArrayList<>();
                for (Entry entry : normal) if (!entry.pkg.equals(pkg) && !order.contains(entry.pkg)) order.add(entry.pkg);
                order.add(Math.min(target, order.size()), pkg);
                customOrder.clear();
                customOrder.addAll(order);
                persistGrid();
                reload();
                Toast.makeText(ctx, selectedLabel + " déplacée", Toast.LENGTH_SHORT).show();
            }, () -> showGridActions(pkg));
    }

    private void showHiddenManager() {
        List<Entry> candidates = allLaunchableHidden();
        if (candidates.isEmpty()) {
            Toast.makeText(ctx, "Aucune application masquée", Toast.LENGTH_SHORT).show();
            showGridManager();
            return;
        }
        String[] labels = new String[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) labels[i] = candidates.get(i).label;
        CompactMenu.show(ctx, "Réafficher une application", labels, null, -1,
            index -> {
                Entry entry = candidates.get(index);
                hidden.remove(entry.pkg);
                persistGrid();
                reload();
                Toast.makeText(ctx, entry.label + " réaffichée", Toast.LENGTH_SHORT).show();
            }, this::showGridManager);
    }

    private List<Entry> allLaunchableHidden() {
        List<Entry> result = new ArrayList<>();
        PackageManager pm = ctx.getPackageManager();
        Intent query = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        Set<String> seen = new HashSet<>();
        for (ResolveInfo ri : pm.queryIntentActivities(query,0)) {
            if (ri.activityInfo == null) continue;
            String pkg = ri.activityInfo.packageName;
            if (!hidden.contains(pkg) || !seen.add(pkg)) continue;
            result.add(new Entry(String.valueOf(ri.loadLabel(pm)),
                new ComponentName(pkg,ri.activityInfo.name),null,ri.loadIcon(pm)));
        }
        Collections.sort(result,(a,b) -> a.label.compareToIgnoreCase(b.label));
        return result;
    }

    private void persistGrid() {
        // Only mutable hiding is exported, so permanently hidden configuration entries stay hidden.
        Set<String> base = new HashSet<>(Arrays.asList(ctx.getResources().getStringArray(R.array.hidden_apps)));
        Set<String> extra = new HashSet<>(hidden); extra.removeAll(base);
        ctx.getSharedPreferences(PREFS,Context.MODE_PRIVATE).edit()
            .putString(HIDDEN_KEY,android.text.TextUtils.join("|",extra))
            .putString(GRID_KEY,android.text.TextUtils.join("|",customOrder))
            .putBoolean(GRID_SAVED_KEY,true).commit();
    }

    private void saveFavourites() {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString(ORDER_KEY, android.text.TextUtils.join("|", favourites)).commit();
    }

    /** Long OK on a dock tile: choose a destination. Swaps with that slot. */
    private void showPositionDialog(int source) {
        int count = Math.min(dockSize, items.size());
        if (source < 0 || source >= count) return;
        String selectedPkg = items.get(source).pkg;
        String selectedLabel = items.get(source).label;
        String[] choices = new String[count];
        for (int i = 0; i < count; i++) {
            choices[i] = (i + 1) + " - " + items.get(i).label;
            if (i == source) choices[i] += ctx.getString(R.string.position_current);
        }
        showGlassMenu(ctx.getString(R.string.position_title, selectedLabel), choices, source,
                destination -> {
                    if (destination == source) return;
                    String targetPkg = items.get(destination).pkg;
                    // Reconstruct the visible order before swapping. This also works if a configured
                    // favourite is not installed (such entries are preserved after the visible apps).
                    List<String> updated = new ArrayList<>();
                    for (int i = 0; i < count; i++) updated.add(items.get(i).pkg);
                    Collections.swap(updated, source, destination);
                    for (String pkg : favourites) if (!updated.contains(pkg)) updated.add(pkg);
                    favourites.clear();
                    favourites.addAll(updated);
                    saveFavourites();
                    reload();
                    // Keep focus on the moved app in its new location after the RecyclerView rebind.
                    if (listener != null) listener.onFavouritesReordered(selectedPkg, destination);
                    Toast.makeText(ctx, ctx.getString(R.string.position_changed, selectedLabel, destination + 1), Toast.LENGTH_SHORT).show();
                }, null);
    }

    private int dp(float value) {
        return (int) (value * ctx.getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable rounded(int color, int stroke) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(17));
        if (stroke != 0) shape.setStroke(dp(1), stroke);
        return shape;
    }

    /** Same compact visual component as the integrated launcher settings. */
    private void showGlassMenu(String title, String[] choices, int current,
                               java.util.function.IntConsumer onSelect, Runnable onBack) {
        CompactMenu.show(ctx, title, choices, null, current, onSelect, onBack);
    }

    private void launch(Entry e) {
        Intent i = new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
                .setComponent(e.component)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        try {
            ctx.startActivity(i);
        } catch (Exception ex) {
            Toast.makeText(ctx, ctx.getString(R.string.cant_open, e.label), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).component.flattenToShortString().hashCode();
    }

    /** Stable key for the app at a position (its component), or null. */
    String keyAt(int position) {
        return position >= 0 && position < items.size() ? items.get(position).component.flattenToShortString() : null;
    }

    /** Position of the app with this key, or -1 when it is gone. */
    int positionOf(String key) {
        if (key == null) return -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).component.flattenToShortString().equals(key)) return i;
        }
        return -1;
    }

    String labelAt(int position) {
        return position >= 0 && position < items.size() ? items.get(position).label : "";
    }

    /** A vivid colour sampled from the app's banner (or icon), cached per entry. */
    int ambientColorAt(int position) {
        if (position < 0 || position >= items.size()) return 0xFF444444;
        Entry e = items.get(position);
        if (e.ambient == 0) e.ambient = vividColor(e.banner != null ? e.banner : e.icon);
        return e.ambient;
    }

    private static int vividColor(Drawable live) {
        if (live == null) return 0xFF444444;
        // Draw a private copy: changing the bounds of the drawable an ImageView shows would blank the tile.
        Drawable d = live.getConstantState() != null ? live.getConstantState().newDrawable().mutate() : live;
        Rect saved = new Rect(d.getBounds());
        int n = 12;
        Bitmap bmp = Bitmap.createBitmap(n, n, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        d.setBounds(0, 0, n, n);
        d.draw(c);
        d.setBounds(saved);
        float[] hsv = new float[3];
        int best = 0xFF444444;
        float bestScore = -1f;
        long r = 0, g = 0, b = 0;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                int p = bmp.getPixel(x, y);
                r += Color.red(p);
                g += Color.green(p);
                b += Color.blue(p);
                Color.colorToHSV(p, hsv);
                float score = hsv[1] * Math.min(1f, hsv[2] * 1.4f);   // saturated and not too dark
                if (score > bestScore) {
                    bestScore = score;
                    best = p;
                }
            }
        }
        bmp.recycle();
        if (bestScore < 0.25f) {   // nearly monochrome banner (e.g. white): use its average, lifted
            int avg = Color.rgb((int) (r / (n * n)), (int) (g / (n * n)), (int) (b / (n * n)));
            Color.colorToHSV(avg, hsv);
            hsv[2] = Math.max(hsv[2], 0.55f);
            return Color.HSVToColor(hsv);
        }
        Color.colorToHSV(best, hsv);
        hsv[1] = Math.min(1f, hsv[1] * 1.1f);
        hsv[2] = Math.max(0.5f, hsv[2]);
        return Color.HSVToColor(hsv);
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final View card;
        final ImageView banner;
        final ImageView icon;
        final TextView label;
        final TextView name;
        Runnable pendingLongPress;
        boolean keyHeld;
        boolean longTriggered;

        Holder(View v, TileListener listener) {
            super(v);
            card = v.findViewById(R.id.card);
            banner = v.findViewById(R.id.banner);
            icon = v.findViewById(R.id.icon);
            label = v.findViewById(R.id.label);
            name = v.findViewById(R.id.name);

            final float density = v.getResources().getDisplayMetrics().density;
            final float radius = density * 16f;
            card.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
                }
            });
            card.setClipToOutline(true);

            card.setOnFocusChangeListener((view, focused) -> {
                float s = focused ? FOCUS_SCALE : 1.0f;
                view.animate().scaleX(s).scaleY(s)
                        .setDuration(focused ? 260 : 180)
                        .setInterpolator(new OvershootInterpolator(focused ? 1.6f : 0f))
                        .start();
                name.animate().alpha(focused ? 1f : 0f)
                        .translationY(focused ? density * 4f : -density * 6f)
                        .setDuration(220).start();
                int pos = getBindingAdapterPosition();
                if (focused && listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onTileFocused(pos, view);
                }
            });
        }
    }
}
