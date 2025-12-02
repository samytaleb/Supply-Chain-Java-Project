package construct;

import core.Evaluator;
import core.Instance;
import core.Schedule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BIBA {

    public Schedule build(Instance I, Evaluator ev) {
        Schedule S = new Schedule(I.m);
        Set<Integer> remaining = new HashSet<>();
        for (int j = 0; j < I.n; j++) remaining.add(j);

        while (!remaining.isEmpty()) {
            int bestJ = -1, bestK = -1, bestPos = -1;
            int bestCost = Integer.MAX_VALUE;

            for (int j : remaining) {
                for (int k = 0; k < I.m; k++) {
                    List<Integer> seq = S.sequence(k);
                    for (int pos = 0; pos <= seq.size(); pos++) {
                        Schedule t = S.deepCopy();
                        t.sequence(k).add(pos, j);
                        int c = ev.cmax(t, I);
                        if (c < bestCost) { bestCost = c; bestJ = j; bestK = k; bestPos = pos; }
                    }
                }
            }

            S.sequence(bestK).add(bestPos, bestJ);
            remaining.remove(bestJ);
        }
        return S;
    }
}
