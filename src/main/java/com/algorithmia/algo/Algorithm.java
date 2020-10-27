package com.algorithmia.algo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Changing build name for builder annotation due to build map
@Builder(buildMethodName = "buildDTO")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public final class Algorithm {
    private String id;
    private String name;
    private Details details;
    private Settings settings;
    @SerializedName("version_info")
    private VersionInfo versionInfo;
    private Source source;
    private Compilation compilation;
    private Build build = new Build();
    @SerializedName("self_link")
    private String selfLink;
    @SerializedName("resource_type")
    private String resourceType;

    @Override
    public String toString() {
        return "Algorithm{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", details=" + details +
                ", settings=" + settings +
                ", versionInfo=" + versionInfo +
                ", source=" + source +
                ", compilation=" + compilation +
                ", build=" + build +
                ", selfLink='" + selfLink + '\'' +
                ", resourceType='" + resourceType + '\'' +
                '}';
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Details {
        private String label;
        private String summary;
        private String tagline;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Settings {
        @SerializedName("algorithm_callability")
        private String algorithmCallability;
        private String environment;
        private String language;
        @SerializedName("licence") // documentation says "license" but server only accepts "licence"!!!!!
        private String license;
        @SerializedName("network_access")
        private String networkAccess;
        @SerializedName("package_set")
        private String packageSet;
        @SerializedName("pipeline_enabled")
        private Boolean pipelineEnabled;
        @SerializedName("source_visibility")
        private String sourceVisibility;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class VersionInfo {
        @SerializedName("git_hash")
        private String gitHash;
        @SerializedName("release_notes")
        private String releaseNotes;
        @SerializedName("sample_input")
        private String sampleInput;
        @SerializedName("sample_output")
        private String sampleOutput;
        @SerializedName("semantic_version")
        private String semanticVersion;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Source {
        @SerializedName("repository_https_url")
        private String repositoryHttpsUrl;
        @SerializedName("repository_name")
        private String repositoryName;
        @SerializedName("repository_owner")
        private String repositoryOwner;
        @SerializedName("repository_ssh_url")
        private String repositorySshUrl;
        private SCM scm;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class SCM {
        @SerializedName("default")
        private Boolean zDefault;
        private Boolean enabled;
        private String id;
        private Oauth oauth;
        private String provider;
        private Urls urls;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Oauth {
        @SerializedName("client_id")
        private String clientId;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Urls {
        private String api;
        private String ssh;
        private String web;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Compilation {
        private String output;
        private Boolean successful;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Build {
        @SerializedName("build_id")
        private String buildId;
        @SerializedName("commit_sha")
        private String commitSha;
        @SerializedName("finished_at")
        private String finishedAt;
        @SerializedName("resource_type")
        private String resourceType;
        @SerializedName("started_at")
        private String startedAt;
        private String status;
        @SerializedName("version_info")
        private VersionInfo versionInfo;
    }
}
