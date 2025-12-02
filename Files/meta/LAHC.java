package meta;

import core.Evaluator;
import core.Instance;
import core.Schedule;
import localsearch.VND;

import java.util.Random;

public class LAHC {
    private final int L, iters;
    private final long seed;
    private final boolean polish;

    public LAHC(int L, int iters, long seed, boolean polish) {
        this.L = L; this.iters = iters; this.seed = seed; this.polish = polish;
    }

    public Schedule solve(Instance I, Schedule start, Evaluator ev) {
        Random rng = new Random(seed);
        Schedule S = start.deepCopy();
        int f = ev.cmax(S, I);

        int[] hist = new int[Math.max(1, L)];
        for (int i = 0; i < hist.length; i++) hist[i] = f;

        Schedule best = S.deepCopy();
        int bestF = f;

        for (int it = 0; it < iters; it++) {
            Schedule N = neighbor(S, I, rng);
            int fn = ev.cmax(N, I);
            int h = hist[it % hist.length];

            if (fn <= f || fn <= h) { // acceptation LAHC
                S = N; f = fn;
                if (f < bestF) { bestF = f; best = S.deepCopy(); }
            }
            hist[it % hist.length] = f;
        }

        if (polish) best = new VND().improve(best, I, ev);
        return best;
    }

    private Schedule neighbor(Schedule S, Instance I, Random rng) {
        Schedule N = S.deepCopy();
        int move = rng.nextInt(3);
        switch (move) {
            case 0: { 
                int k = rng.nextInt(I.m);
                if (N.sequence(k).size() >= 2) {
                    int a = rng.nextInt(N.sequence(k).size() - 1);
                    int ja = N.sequence(k).get(a);
                    int jb = N.sequence(k).get(a + 1);
                    N.sequence(k).set(a, jb);
                    N.sequence(k).set(a + 1, ja);
                    return N;
                }

            }
            case 1: { 
                int k = rng.nextInt(I.m);
                if (N.sequence(k).size() >= 1) {
                    int a = rng.nextInt(N.sequence(k).size());
                    int b = rng.nextInt(N.sequence(k).size() + 1);
                    int j = N.sequence(k).remove(a);
                    N.sequence(k).add(b > a ? b - 1 : b, j);
                    return N;
                }
            }
            default: { 
                if (I.m >= 2) {
                    int k = rng.nextInt(I.m);
                    if (N.sequence(k).size() >= 1) {
                        int pos = rng.nextInt(N.sequence(k).size());
                        int k2 = (k + 1 + rng.nextInt(I.m - 1)) % I.m;
                        int b = rng.nextInt(N.sequence(k2).size() + 1);
                        int j = N.sequence(k).remove(pos);
                        N.sequence(k2).add(b, j);
                        return N;
                    }
                }
            }
        }
        return N; // si aucun mouvement possible
    }
}
