package com.algorithmia.data;

import java.util.List;
import java.util.Map;

public class DataAcl {
    protected List<String> read;   // This is needed for gson to work correctly

    public DataAcl(DataAclType readAcl) {
        this.read = readAcl.getAclStrings();
    }

    public DataAclType getReadPermissions() {
        return DataAclType.fromAclStrings(read);
    }

    public static DataAcl fromAclResponse(Map<String, List<String>> aclResponse) {
        if (aclResponse == null) {
            return null;
        }
        return new DataAcl(DataAclType.fromAclStrings(aclResponse.get("read")));
    }

    public static final DataAcl PUBLIC = new DataAcl(DataAclType.PUBLIC);
    public static final DataAcl PRIVATE = new DataAcl(DataAclType.PRIVATE);
    public static final DataAcl MY_ALGOS = new DataAcl(DataAclType.MY_ALGOS);
}
