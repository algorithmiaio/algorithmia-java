package com.algorithmia.algo;

/**
 * Version of an Algorithm object
 */
public class Version {
    private String version;

    /**
     * @param version formatted as x.y.z or x.y
     */
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

    /**
     * Helper to instantiate a fully specified Version
     *
     * @param major the major version number, i.e., 'x' in x.y.z
     * @param minor the minor version number, i.e., 'y' in x.y.z
     * @param revision the revision number, i.e., 'z' in x.y.z
     * @return fully specified Version object
     */
    public static Version Revision(Long major, Long minor, Long revision) {
        return new Version(String.format("%d.%d.%d", major, minor, revision));
    }

    /**
     * Helper to instantiate a minor Version
     * Using a minor version implies the latest revision of a minor version will be used
     *
     * @param major the major version number, i.e., 'x' in x.y
     * @param minor the minor version number, i.e., 'y' in x.y
     * @return Version object that only specifies minor version
     */
    public static Version Minor(Long major, Long minor) {
        return new Version(String.format("%d.%d", major, minor));
    }

    /**
     * Helper to instantiate the latest Version
     * Using latest version implies that breaking changes and price changes could occur between calls
     *
     * @return Version object that does not specify a version
     */
    public static Version Latest() {
        return new Version(null);
    }
}