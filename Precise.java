package Sydn;

public enum Precise {
        PRECISE(100),
        NORMAL(50);
        final int ps;

        Precise(int n) {
            ps = n;
        }
    }
