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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PainMapFullBodyPreviewView extends View {

    private static class Zone {
        final String key;
        final RectF normOval; // 0..1
        Zone(String key, RectF normOval) {
            this.key = key;
            this.normOval = normOval;
        }
    }

    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Map<String, Integer> painMap = new HashMap<>();
    private final Map<String, Zone> zones = new LinkedHashMap<>();

    private Bitmap bodyBitmap;
    private final RectF imageDst = new RectF();
    private final Matrix imageMatrix = new Matrix();

    public PainMapFullBodyPreviewView(Context context) {
        super(context);
        init();
    }

    public PainMapFullBodyPreviewView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PainMapFullBodyPreviewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        overlayPaint.setStyle(Paint.Style.FILL);
        bodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.human_body);

        // ugyanazok a zónák, mint a PainMapView-ben (15-20)
        addZone("head_front",     oval(0.24f, 0.06f, 0.34f, 0.18f));
        addZone("neck_front",     oval(0.26f, 0.18f, 0.32f, 0.22f));
        addZone("shoulder_l_f",   oval(0.16f, 0.22f, 0.24f, 0.28f));
        addZone("shoulder_r_f",   oval(0.34f, 0.22f, 0.42f, 0.28f));
        addZone("chest_front",    oval(0.20f, 0.22f, 0.38f, 0.36f));
        addZone("abdomen_front",  oval(0.22f, 0.36f, 0.36f, 0.44f));
        addZone("hip_front",      oval(0.23f, 0.44f, 0.35f, 0.50f));
        addZone("arm_l_f",        oval(0.14f, 0.26f, 0.20f, 0.50f));
        addZone("arm_r_f",        oval(0.38f, 0.26f, 0.44f, 0.50f));
        addZone("leg_l_f",        oval(0.22f, 0.50f, 0.28f, 0.88f));
        addZone("leg_r_f",        oval(0.30f, 0.50f, 0.36f, 0.88f));

        // Back (jobb fél ~ x: 0.52..0.95)
        addZone("head_back",      oval(0.66f, 0.06f, 0.76f, 0.18f));
        addZone("neck_back",      oval(0.68f, 0.18f, 0.74f, 0.22f));
        addZone("upper_back",     oval(0.62f, 0.22f, 0.82f, 0.34f));
        addZone("lower_back",     oval(0.64f, 0.34f, 0.78f, 0.44f));
        addZone("hip_back",       oval(0.65f, 0.44f, 0.77f, 0.50f));
        addZone("arm_l_b",        oval(0.56f, 0.26f, 0.62f, 0.50f));
        addZone("arm_r_b",        oval(0.80f, 0.26f, 0.86f, 0.50f));
        addZone("leg_l_b",        oval(0.64f, 0.50f, 0.72f, 0.88f));
        addZone("leg_r_b",        oval(0.72f, 0.50f, 0.80f, 0.88f));

        for (String key : zones.keySet()) painMap.put(key, 0);

        // preview: ne legyen interaktív
        setClickable(false);
        setFocusable(false);
    }

    private void addZone(String key, RectF normOval) {
        zones.put(key, new Zone(key, normOval));
    }

    private RectF oval(float left, float top, float right, float bottom) {
        return new RectF(left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        computeFitCenterDst(w, h);
    }

    private void computeFitCenterDst(int viewW, int viewH) {
        if (bodyBitmap == null) return;

        float bw = bodyBitmap.getWidth();
        float bh = bodyBitmap.getHeight();

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

        if (bodyBitmap != null) {
            canvas.drawBitmap(bodyBitmap, imageMatrix, null);
        }

        for (Zone z : zones.values()) {
            int level = painMap.containsKey(z.key) ? painMap.get(z.key) : 0;
            if (level <= 0) continue;

            overlayPaint.setColor(PainColors.colorForLevel(level));
            RectF oval = normToDst(z.normOval);
            canvas.drawOval(oval, overlayPaint);
        }
    }

    private RectF normToDst(RectF norm) {
        float l = imageDst.left + norm.left * imageDst.width();
        float t = imageDst.top + norm.top * imageDst.height();
        float r = imageDst.left + norm.right * imageDst.width();
        float b = imageDst.top + norm.bottom * imageDst.height();
        return new RectF(l, t, r, b);
    }

    // Adapter fogja hívni bind-nél:
    public void setPainMapJson(String json) {
        // reset
        for (String key : zones.keySet()) painMap.put(key, 0);

        if (json == null || json.trim().isEmpty()) {
            invalidate();
            return;
        }

        try {
            JSONObject obj = new JSONObject(json);
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                String key = it.next();
                int v = obj.optInt(key, 0);
                if (zones.containsKey(key)) {
                    painMap.put(key, Math.max(0, Math.min(5, v)));
                }
            }
        } catch (Exception ignored) { }

        invalidate();
    }
}