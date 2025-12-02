package localsearch;

import core.Evaluator;
import core.Instance;
import core.Schedule;

import java.util.List;

public class VND {

    public Schedule improve(Schedule S, Instance I, Evaluator ev) {
        boolean improved = true;
        while (improved) {
            improved = false;
            int best = ev.cmax(S, I);
            Schedule bestS = S;

            //Swap adjacent
            for (int k = 0; k < I.m; k++) {
                List<Integer> seq = S.sequence(k);
                for (int a = 0; a + 1 < seq.size(); a++) {
                    Schedule t = S.deepCopy();
                    int ja = t.sequence(k).get(a);
                    int jb = t.sequence(k).get(a + 1);
                    t.sequence(k).set(a, jb);
                    t.sequence(k).set(a + 1, ja);
                    int c = ev.cmax(t, I);
                    if (c < best) { best = c; bestS = t; }
                }
            }
            //Insertion intra-machine
            for (int k = 0; k < I.m; k++) {
                List<Integer> seq = S.sequence(k);
                for (int a = 0; a < seq.size(); a++) {
                    for (int b = 0; b <= seq.size(); b++) {
                        if (a == b || a + 1 == b) continue;
                        Schedule t = S.deepCopy();
                        int j = t.sequence(k).remove(a);
                        t.sequence(k).add(b > a ? b - 1 : b, j);
                        int c = ev.cmax(t, I);
                        if (c < best) { best = c; bestS = t; }
                    }
                }
            }
            //Insertion inter-machines
            for (int k = 0; k < I.m; k++) {
                for (int pos = 0; pos < S.sequence(k).size(); pos++) {
                    for (int k2 = 0; k2 < I.m; k2++) if (k2 != k) {
                        for (int b = 0; b <= S.sequence(k2).size(); b++) {
                            Schedule t = S.deepCopy();
                            int j = t.sequence(k).remove(pos);
                            t.sequence(k2).add(b, j);
                            int c = ev.cmax(t, I);
                            if (c < best) { best = c; bestS = t; }
                        }
                    }
                }
            }

            if (bestS != S) { S = bestS; improved = true; }
        }
        return S;
    }
}
