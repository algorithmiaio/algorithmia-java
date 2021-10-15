package com.algorithmia.algo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder(buildMethodName = "buildDTO")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Environment {
    private String id;
    @SerializedName("environment_specification_id")
    private String environmentSpecificationId;
    @SerializedName("display_name")
    private String displayName;
    private String description;
    @SerializedName("created_at")
    private String createdAt;
    private Language language;
    @SerializedName("machine_type")
    private String machineType;


    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class Language {
        private String name;
        @SerializedName("display_name")
        private String displayName;
        private String configuration;
    }
}
