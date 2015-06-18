package com.algorithmia.algo;

public class Version {
    private String version;

    public Version(String version) {
        this.version = version;
    }

    public boolean isLatest() {
        return this.version == null;
    }

    @Override
    public String toString() {
        if(this.version == null) {
            return "latest";
        } else {
            return this.version;
        }
    }

    public static Version Revision(Long major, Long minor, Long revision) {
        return new Version(String.format("%d.%d.%d", major, minor, revision));
    }

    public static Version Minor(Long major, Long minor) {
        return new Version(String.format("%d.%d", major, minor));
    }

    public static Version Latest() {
        return new Version(null);
    }
}