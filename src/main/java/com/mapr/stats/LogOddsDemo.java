package com.mapr.stats;

import com.tdunning.math.stats.MergingDigest;
import com.tdunning.math.stats.TDigest;

import java.util.Random;

public class LogOddsDemo {

    public static final int N = 1_000_000;

    public static void main(String[] args) {
        double[] data = new double[N];

        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            if (i > 200_000) {
                data[i] = rand.nextGaussian() + 3;
            } else {
                data[i] = rand.nextGaussian();
            }
        }

        TDigest td = new MergingDigest(200);

        // train the t-digest with 20% of the data
        int i = 0;
        while (i < N / 10) {
            td.add(data[i]);
            i++;
        }

        while (i < N) {
            td.add(data[i]);
            double p = td.cdf(data[i]);
            p = Math.log10(p / (1 - p));
            // mark anomalies by large log-odds
            if (p > 3) {
                System.out.printf("%d, %.3f, %.6f, %.3f\n", i, p, td.cdf(data[i]), data[i]);
            }
            i++;
        }
    }
}
