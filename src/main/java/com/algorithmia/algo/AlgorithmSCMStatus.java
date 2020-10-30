package com.algorithmia.algo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public final class AlgorithmSCMStatus {
    @SerializedName("scm_connection_status")
    private String scmConnectionStatus;
    @SerializedName("repository_public_deploy_key")
    private String repositoryPublicDeployKey;
    @SerializedName("repository_webhook_secret")
    private String repositoryWebhookSecret;
    @SerializedName("repository_webhook_url")
    private String repositoryWebhookUrl;
}
