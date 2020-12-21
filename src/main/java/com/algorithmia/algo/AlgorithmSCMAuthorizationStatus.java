package com.algorithmia.algo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public final class AlgorithmSCMAuthorizationStatus {
    @SerializedName("authorization_status")
    private String authorizationStatus;
    @SerializedName("scm_username")
    private String scmUserName;
    @SerializedName("scm_organizations")
    private List<SCMOrganizations> scmOrganizations = new LinkedList<>();

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static final class SCMOrganizations {
        @SerializedName("scm_username")
        private String scmUserName;
        @SerializedName("access_level")
        private String accessLevel;
    }
}
