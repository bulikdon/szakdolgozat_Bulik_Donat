package com.example.clientmanagerapp.ui.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.clientmanagerapp.R;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PainMapFootPreviewView extends View {

    private static class Zone {
        final String key;
        final RectF normOval; // 0..1 in image bounds
        Zone(String key, RectF normOval) {
            this.key = key;
            this.normOval = normOval;
        }
    }

    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap footBitmap;
    private final RectF imageDst = new RectF();
    private final Matrix imageMatrix = new Matrix();

    private final Map<String, Zone> zones = new LinkedHashMap<>();
    private final Map<String, Integer> values = new LinkedHashMap<>();

    public PainMapFootPreviewView(Context context) { super(context); init(); }
    public PainMapFootPreviewView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); init(); }
    public PainMapFootPreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); init(); }

    private void init() {
        overlayPaint.setStyle(Paint.Style.FILL);

        // IMPORTANT: legyen ugyanaz a drawable, mint a PainMapFootView-ben
        // Ha nálad más a név, ezt az egy sort írd át.
        footBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.foot_soles);

        // SAME zones as the editor view (PainMapFootView)
        // Bal talp (x ~ 0.10..0.46)
        addZone("left_toes",      oval(0.22f, 0.12f, 0.46f, 0.24f));
        addZone("left_ball",      oval(0.25f, 0.24f, 0.44f, 0.42f));
        addZone("left_arch",      oval(0.27f, 0.42f, 0.40f, 0.72f));
        addZone("left_heel",      oval(0.30f, 0.70f, 0.43f, 0.90f));

        // JOBB TALP (x ~ 0.54..0.90)
        addZone("right_toes",     oval(0.54f, 0.12f, 0.78f, 0.24f));
        addZone("right_ball",     oval(0.55f, 0.24f, 0.75f, 0.42f));
        addZone("right_arch",     oval(0.60f, 0.42f, 0.73f, 0.72f));
        addZone("right_heel",     oval(0.57f, 0.70f, 0.70f, 0.90f));

        for (String k : zones.keySet()) values.put(k, 0);

        // Preview: never clickable
        setClickable(false);
        setFocusable(false);
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
        if (footBitmap == null) return;

        float bw = footBitmap.getWidth();
        float bh = footBitmap.getHeight();

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

        if (footBitmap != null) canvas.drawBitmap(footBitmap, imageMatrix, null);

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

    public void setPainMapJson(@Nullable String json) {
        for (String k : zones.keySet()) values.put(k, 0);

        if (json == null || json.trim().isEmpty()) {
            invalidate();
            return;
        }

        try {
            JSONObject obj = new JSONObject(json);
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                String k = it.next();
                int v = obj.optInt(k, 0);
                if (zones.containsKey(k)) values.put(k, clamp0to5(v));
            }
        } catch (Exception ignored) {}

        invalidate();
    }

    private int clamp0to5(int v) {
        if (v < 0) return 0;
        if (v > 5) return 5;
        return v;
    }
}