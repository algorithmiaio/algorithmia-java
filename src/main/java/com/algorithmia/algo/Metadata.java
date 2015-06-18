package com.algorithmia.algo;

/**
 * A result representing success
 */
public final class Metadata {
    public Double duration;
    public String stdout;
    // public List<String> alerts;

    public Metadata(Double duration) {
        this(duration, null);
    }

    public Metadata(Double duration, String stdout) {
        this.duration = duration;
        this.stdout = stdout;
    }

}
