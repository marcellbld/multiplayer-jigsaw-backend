package com.mbld.jigslybackend.utils;

public final class MathUtils {
    private MathUtils() {}

    public static float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
}
