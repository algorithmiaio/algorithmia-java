package com.algorithmia.algo;

/**
 * Algorithm response metadata
 */
public final class Metadata {
    public ContentType content_type;
    public Double duration;
    public String stdout;

    public Metadata(ContentType content_type, Double duration) {
        this(content_type, duration, null);
    }

    public Metadata(ContentType content_type, Double duration, String stdout) {
        this.content_type = content_type;
        this.duration = duration;
        this.stdout = stdout;
    }

}
