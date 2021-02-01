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
public class Organization {
    @SerializedName("org_contact_name")
    private String orgContactName;
    @SerializedName("org_email")
    private String orgEmail;
    @SerializedName("org_label")
    private String orgLabel;
    @SerializedName("org_name")
    private String orgName;
    @SerializedName("org_url")
    private String orgUrl;
    @SerializedName("external_id")
    private String externalId;
    @SerializedName("external_admin_group_id")
    private String externalAdminGroupId;
    @SerializedName("external_member_group_id")
    private String externalMemberGroupId;
    @SerializedName("type_id")
    private String typeId;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("self_link")
    private String selfLink;
}
