package com.algorithmia.algo;

/**
 * Algorithm response metadata
 */
public final class Metadata {
    public Double duration;
    public String stdout;

    public Metadata(Double duration) {
        this(duration, null);
    }

    public Metadata(Double duration, String stdout) {
        this.duration = duration;
        this.stdout = stdout;
    }

}
