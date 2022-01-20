package com.company;

public class Main {

    public static void main(String[] args) {
        int darts = 10_000_000, area = 3, totalArea = 10;

        double finalArea = getFinalArea(darts, area, totalArea);

        System.out.println(finalArea + "%");
    }

    public static boolean inFunct(double x, double y) {
        return y <= f(x);
    }

    private static double f(double x) {
        return Math.sin(x) + 2;
    }

    private static double getFinalArea(int darts, int area, int totalArea) {
        double hits = 0;
        for (int i = 0; i < darts; i++) {
            double dartY = (Math.random() * totalArea);
            double dartX = (Math.random() * totalArea);

            if (inFunct(dartX, dartY)) hits++;
        }

        return ((hits/darts) * totalArea) * (100 / totalArea);
    }
}
