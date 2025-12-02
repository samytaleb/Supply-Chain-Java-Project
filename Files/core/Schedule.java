package core;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private final List<List<Integer>> machines;

    public Schedule(int m) {
        machines = new ArrayList<>(m);
        for (int k = 0; k < m; k++) machines.add(new ArrayList<>());
    }

    public List<Integer> sequence(int k) { return machines.get(k); }

    public int m() { return machines.size(); }

    public Schedule deepCopy() {
        Schedule S = new Schedule(machines.size());
        for (int k = 0; k < machines.size(); k++) S.sequence(k).addAll(machines.get(k));
        return S;
    }

    @Override public String toString() { return machines.toString(); }
}
