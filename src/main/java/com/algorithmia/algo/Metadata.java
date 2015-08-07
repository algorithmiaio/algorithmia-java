package com.algorithmia.algo;

/**
 * Algorithm response metadata
 */
public final class Metadata {

    private ContentType content_type;
    private Double duration;
    private String stdout;

    public Metadata(ContentType content_type, Double duration) {
        this(content_type, duration, null);
    }

    public Metadata(ContentType content_type, Double duration, String stdout) {
        this.content_type = content_type;
        this.duration = duration;
        this.stdout = stdout;
    }

    public ContentType getContentType() {
      return content_type;
    }
    public Double getDuration() {
      return duration;
    }
    public String getStdout() {
      return stdout;
    }

}
