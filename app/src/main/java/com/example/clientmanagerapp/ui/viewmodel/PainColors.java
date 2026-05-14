package com.example.clientmanagerapp.ui.viewmodel;

import android.graphics.Color;

public class PainColors {

    // áttetsző (alpha ~ 0xAA) színek: 1 zöld -> 5 piros
    public static int colorForLevel(int level) {
        switch (level) {
            case 1: return Color.parseColor("#AA00FF00");
            case 2: return Color.parseColor("#AA99FF00");
            case 3: return Color.parseColor("#AAFFFF00");
            case 4: return Color.parseColor("#AAFF9900");
            case 5: return Color.parseColor("#AAFF0000");
            default: return Color.TRANSPARENT;
        }
    }
}