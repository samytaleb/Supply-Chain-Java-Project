package core;

import java.util.List;

public class Evaluator {

    public int cmax(Schedule S, Instance I) {
        int Cmax = 0;
        for (int k = 0; k < I.m; k++) {
            List<Integer> seq = S.sequence(k);
            int t = 0;
            int prev = -1; // -1 = début de machine
            for (int idx = 0; idx < seq.size(); idx++) {
                int j = seq.get(idx);

                int setup;
                if (prev == -1) setup = (I.s0jk == null ? 0 : I.s0jk[j][k]);
                else            setup = (I.sijk == null ? 0 : I.sijk[prev][j][k]);

                t += setup;
                t = Math.max(t, I.rj[j]);
                t += I.pjk[j][k];
                prev = j;
            }
            Cmax = Math.max(Cmax, t);
        }
        return Cmax;
    }
}
