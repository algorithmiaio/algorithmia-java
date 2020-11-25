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
public class User {
    private String id;
    @SerializedName("company_name")
    private String companyName;
    @SerializedName("company_role")
    private String companyRole;
    private String email;
    @SerializedName("fullname")
    private String fullName;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("self_link")
    private String selfLink;
    @SerializedName("username")
    private String userName;
}
