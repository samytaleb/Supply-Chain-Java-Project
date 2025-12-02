package core;

public class Instance {
    public final int n, m;  
    public final int[][] pjk;    
    public final int[] rj;   
    public final int[][][] sijk; 
    public final int[][] s0jk;  

    public Instance(int n, int m, int[][] pjk, int[] rj, int[][][] sijk, int[][] s0jk) {
        this.n = n; this.m = m; this.pjk = pjk; this.rj = rj; this.sijk = sijk; this.s0jk = s0jk;
        validate();
    }

    private void validate() {
        if (pjk.length != n) throw new IllegalArgumentException("pjk rows != n");
        for (int j = 0; j < n; j++) if (pjk[j].length != m) throw new IllegalArgumentException("pjk[j].length != m");
        if (rj.length != n) throw new IllegalArgumentException("rj length != n");
        if (sijk != null) {
            if (sijk.length != n) throw new IllegalArgumentException("sijk size0 != n");
            for (int i = 0; i < n; i++) {
                if (sijk[i].length != n) throw new IllegalArgumentException("sijk[i] size1 != n");
                for (int j = 0; j < n; j++) if (sijk[i][j].length != m) throw new IllegalArgumentException("sijk[i][j] size2 != m");
            }
        }
        if (s0jk != null) {
            if (s0jk.length != n) throw new IllegalArgumentException("s0jk size0 != n");
            for (int j = 0; j < n; j++) if (s0jk[j].length != m) throw new IllegalArgumentException("s0jk[j] size1 != m");
        }
    }
}
