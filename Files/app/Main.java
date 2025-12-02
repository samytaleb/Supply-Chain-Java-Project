package app;

import construct.BIBA;
import core.Evaluator;
import core.Instance;
import core.Schedule;
import localsearch.VND;
import meta.LAHC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    static class Result {
        final String name; final int n, m;
        final int cBiba, cVnd, cLahcMin, cLahcAvg, cLahcMax;
        final double tBibaMs, tVndMs, tLahcAvgMs;
        final Schedule best;
        Result(String name, int n, int m, int cBiba, int cVnd,
               int cLahcMin, int cLahcAvg, int cLahcMax,
               double tBibaMs, double tVndMs, double tLahcAvgMs,
               Schedule best) {
            this.name=name; this.n=n; this.m=m;
            this.cBiba=cBiba; this.cVnd=cVnd;
            this.cLahcMin=cLahcMin; this.cLahcAvg=cLahcAvg; this.cLahcMax=cLahcMax;
            this.tBibaMs=tBibaMs; this.tVndMs=tVndMs; this.tLahcAvgMs=tLahcAvgMs;
            this.best=best;
        }
    }

    public static void main(String[] args) {
        Evaluator ev = new Evaluator();

        List<String> names = new ArrayList<>();
        List<Instance> insts = new ArrayList<>();
        names.add("I1"); insts.add(buildI1());
        names.add("I2"); insts.add(buildI2());
        names.add("I3"); insts.add(buildI3(12, 3, 42L));

        List<Result> results = new ArrayList<>();
        for (int i = 0; i < insts.size(); i++) {
            Result r = runAll(names.get(i), insts.get(i), ev);
            results.add(r);
        }

        System.out.println();
        System.out.println(String.format(
            "%-4s %-3s %-3s %-6s %-6s %-6s %-6s %-6s %-10s %-10s %-10s",
            "Inst","n","m","BIBA","VND","LAHCmin","LAHCavg","LAHCmax",
            "tBIBA","tVND","tLAHC(avg)"
        ));
        for (Result r : results) {
            System.out.println(String.format(
                "%-4s %-3d %-3d %-6d %-6d %-6d %-6d %-6d %-10.2f %-10.2f %-10.2f",
                r.name, r.n, r.m, r.cBiba, r.cVnd, r.cLahcMin, r.cLahcAvg, r.cLahcMax,
                r.tBibaMs, r.tVndMs, r.tLahcAvgMs
            ));
        }
        // Affichage des séquences finales
        for (int i = 0; i < results.size(); i++) {
            printSequences(names.get(i), results.get(i).best);
        }
    }

    static Result runAll(String name, Instance I, Evaluator ev) {
        BIBA biba = new BIBA();
        VND vnd = new VND();

        long t0 = System.nanoTime();
        Schedule S0 = biba.build(I, ev);
        long t1 = System.nanoTime();

        Schedule S1 = vnd.improve(S0, I, ev);
        long t2 = System.nanoTime();

        int cB = ev.cmax(S0, I);
        int cV = ev.cmax(S1, I);

        long[] seeds = {7L, 42L, 2025L};
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE, sum = 0;
        double tLsum = 0.0;
        Schedule best = null;

        for (long seed : seeds) {
            LAHC lahc = new LAHC(50, 8000, seed, true);
            long a = System.nanoTime();
            Schedule S2 = lahc.solve(I, S1, ev);
            long b = System.nanoTime();

            int c2 = ev.cmax(S2, I);
            tLsum += (b - a) / 1e6;
            sum += c2;
            if (c2 < min) { min = c2; best = S2; }
            if (c2 > max) max = c2;
        }
        int avg = Math.round((float)sum / seeds.length);

        double tB = (t1 - t0) / 1e6;
        double tV = (t2 - t1) / 1e6;
        double tLavg = tLsum / seeds.length;

        assertFeasible(best, I, name);
        System.out.println(name + " : BIBA=" + cB + "  VND=" + cV +
                "  LAHC[min/avg/max]=" + min + "/" + avg + "/" + max);

        return new Result(name, I.n, I.m, cB, cV, min, avg, max, tB, tV, tLavg, best);
    }

    static Instance buildI1() {
        int n=6, m=2;
        int[][] p = {{6,7},{5,5},{7,6},{4,8},{9,3},{6,6}};
        int[] r = {0,2,0,3,1,0};
        return new Instance(n, m, p, r, null, null);
    }

    static Instance buildI2() {
        int n=8, m=3;
        int[][] p = {
            {3,9,6},{4,4,7},{5,6,5},{7,8,4},
            {2,2,9},{9,3,6},{4,5,5},{6,4,7}
        };
        int[] r = {0,0,1,2,0,3,1,2};
        int[][][] sijk = new int[n][n][m];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) if (i!=j)
            for (int k=0;k<m;k++) sijk[i][j][k] = 1; // setup unitaire
        return new Instance(n, m, p, r, sijk, null);
    }

    static Instance buildI3(int n, int m, long seed) {
        Random rng = new Random(seed);
        int[][] p = new int[n][m];
        for (int j=0;j<n;j++) {
            for (int k=0;k<m;k++) {
                int base = 2 + rng.nextInt(11);      // 2..12
                int bias = (k==0?+2:(k==2?-2:0));    // M0 lente, M2 rapide
                p[j][k] = Math.max(1, base + bias);
            }
        }
        int[] r = new int[n];
        for (int j=0;j<n;j++) r[j] = rng.nextInt(6); // 0..5
        int[][][] sijk = new int[n][n][m];
        for (int i=0;i<n;i++) for (int j=0;j<n;j++) if (i!=j)
            for (int k=0;k<m;k++) sijk[i][j][k] = 1;
        return new Instance(n, m, p, r, sijk, null);
    }

    static void printSequences(String name, Schedule S) {
        System.out.println(name + " - séquences finales :");
        for (int k = 0; k < S.m(); k++) {
            System.out.println("M" + k + ": " + S.sequence(k));
        }
        System.out.println();
    }

    static void assertFeasible(Schedule S, Instance I, String name) {
        boolean[] seen = new boolean[I.n];
        for (int k=0;k<I.m;k++) for (int j : S.sequence(k)) {
            if (j<0 || j>=I.n) throw new IllegalStateException(name + " : id job invalide " + j);
            if (seen[j]) throw new IllegalStateException(name + " : job " + j + " en double");
            seen[j] = true;
        }
        for (int j=0;j<I.n;j++) if (!seen[j])
            throw new IllegalStateException(name + " : job " + j + " manquant");
    }
}
