package com.example.clientmanagerapp.ui.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.clientmanagerapp.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PainMapAbsView extends View {

    private static class Zone {
        final String key;
        final RectF normOval; // 0..1
        Zone(String key, RectF normOval) {
            this.key = key;
            this.normOval = normOval;
        }
    }

    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap absBitmap;
    private final RectF imageDst = new RectF();
    private final Matrix imageMatrix = new Matrix();

    private final Map<String, Zone> zones = new LinkedHashMap<>();
    private final Map<String, Integer> values = new HashMap<>();

    public PainMapAbsView(Context context) { super(context); init(); }
    public PainMapAbsView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }
    public PainMapAbsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        overlayPaint.setStyle(Paint.Style.FILL);
        absBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.abs_body);

        // ~18 zóna (később finomítjuk optikailag)

        addZone("upper_abs_left",   oval(0.32f, 0.12f, 0.50f, 0.34f));
        addZone("mid_abs_left",     oval(0.36f, 0.34f, 0.50f, 0.48f));
        addZone("lower_abs_left",   oval(0.36f, 0.48f, 0.50f, 0.58f));

        addZone("upper_abs_right",  oval(0.50f, 0.12f, 0.68f, 0.34f));
        addZone("mid_abs_right",    oval(0.50f, 0.34f, 0.64f, 0.48f));
        addZone("lower_abs_right",  oval(0.50f, 0.48f, 0.64f, 0.58f));

        addZone("oblique_left_upper", oval(0.18f, 0.18f, 0.32f, 0.30f));
        addZone("oblique_left_mid",   oval(0.22f, 0.30f, 0.34f, 0.46f));
        addZone("oblique_left_lower", oval(0.22f, 0.48f, 0.38f, 0.86f));

        addZone("oblique_right_upper", oval(0.68f, 0.18f, 0.82f, 0.30f));
        addZone("oblique_right_mid",   oval(0.66f, 0.30f, 0.78f, 0.46f));
        addZone("oblique_right_lower", oval(0.60f, 0.48f, 0.76f, 0.86f));

        addZone("pelvis_left",  oval(0.40f, 0.60f, 0.50f, 0.96f));
        addZone("pelvis_right", oval(0.50f, 0.60f, 0.60f, 0.96f));

        for (String k : zones.keySet()) values.put(k, 0);

        setClickable(true);
        setFocusable(true);
    }

    private void addZone(String key, RectF normOval) {
        zones.put(key, new Zone(key, normOval));
    }

    private RectF oval(float l, float t, float r, float b) {
        return new RectF(l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        computeFitCenterDst(w, h);
    }

    private void computeFitCenterDst(int viewW, int viewH) {
        if (absBitmap == null) return;

        float bw = absBitmap.getWidth();
        float bh = absBitmap.getHeight();

        float scale = Math.min(viewW / bw, viewH / bh);
        float dstW = bw * scale;
        float dstH = bh * scale;

        float left = (viewW - dstW) / 2f;
        float top = (viewH - dstH) / 2f;

        imageDst.set(left, top, left + dstW, top + dstH);

        imageMatrix.reset();
        imageMatrix.postScale(scale, scale);
        imageMatrix.postTranslate(left, top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (absBitmap != null) canvas.drawBitmap(absBitmap, imageMatrix, null);

        for (Zone z : zones.values()) {
            int level = values.containsKey(z.key) ? values.get(z.key) : 0;
            if (level <= 0) continue;

            overlayPaint.setColor(PainColors.colorForLevel(level));
            RectF dstOval = normToDst(z.normOval);
            canvas.drawOval(dstOval, overlayPaint);
        }
    }

    private RectF normToDst(RectF norm) {
        float l = imageDst.left + norm.left * imageDst.width();
        float t = imageDst.top + norm.top * imageDst.height();
        float r = imageDst.left + norm.right * imageDst.width();
        float b = imageDst.top + norm.bottom * imageDst.height();
        return new RectF(l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) return true;

        float x = event.getX();
        float y = event.getY();

        String hitKey = null;
        for (Zone z : zones.values()) {
            RectF dst = normToDst(z.normOval);
            if (dst.contains(x, y)) {
                hitKey = z.key;
                break;
            }
        }

        if (hitKey == null) return true;

        showPicker(hitKey);
        return true;
    }

    private void showPicker(String key) {
        final String[] levels = {"0", "1", "2", "3", "4", "5"};
        new AlertDialog.Builder(getContext())
                .setTitle("Pain level (0–5)")
                .setItems(levels, (d, which) -> {
                    int level = Integer.parseInt(levels[which]);
                    values.put(key, level);
                    invalidate();
                })
                .show();
    }

    public String exportPainMapJson() {
        try {
            JSONObject obj = new JSONObject();
            for (String k : zones.keySet()) {
                int v = values.containsKey(k) ? values.get(k) : 0;
                obj.put(k, v);
            }
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public void importPainMapJson(String json) {
        for (String k : zones.keySet()) values.put(k, 0);

        if (json == null || json.trim().isEmpty()) { invalidate(); return; }

        try {
            JSONObject obj = new JSONObject(json);
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                String k = it.next();
                int v = obj.optInt(k, 0);
                if (zones.containsKey(k)) values.put(k, Math.max(0, Math.min(5, v)));
            }
        } catch (Exception ignored) {}

        invalidate();
    }
}