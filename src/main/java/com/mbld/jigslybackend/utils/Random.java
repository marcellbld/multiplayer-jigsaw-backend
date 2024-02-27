package com.mbld.jigslybackend.utils;

public final class Random {
    private Random() {}
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
