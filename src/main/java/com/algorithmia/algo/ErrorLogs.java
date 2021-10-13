package com.algorithmia.algo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ErrorLogs {
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("request_id")
    private String requestId;
    private String username;
    private String algoname;
    private String algoversion;
    private String input;
    private String error;
    @SerializedName("error_type")
    private String errorType;
    @SerializedName("billable_to")
    private String billableTo;
    private String worker;
}
