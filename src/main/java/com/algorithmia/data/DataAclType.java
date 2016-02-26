package com.algorithmia.data;

import java.util.LinkedList;
import java.util.List;

public class DataAclType {
    private final List<String> aclStrings;
    public DataAclType(List<String> aclStrings) {
        this.aclStrings = aclStrings;
    }

    public static DataAclType fromAclStrings(List<String> aclStrings) {
        if (aclStrings == null) {
            return null;
        } else if (aclStrings.size() == 0) {
            return PRIVATE;
        } else {
            String aclString = aclStrings.get(0);
            if (aclString.equals(PUBLIC_PERMISSIONS)) {
                return PUBLIC;
            } else if (aclString.equals(MY_ALGOS_PERMISSIONS)) {
                return MY_ALGOS;
            }
        }
        return null;
    }

    private static List<String> createList(String aclString) {
        List<String> aclList = new LinkedList<String>();
        aclList.add(aclString);
        return aclList;
    }

    public List<String> getAclStrings() {
        return aclStrings;
    }

    public static final String PUBLIC_PERMISSIONS = "user://*";
    public static final String MY_ALGOS_PERMISSIONS = "algo://.my/*";

    public static DataAclType PRIVATE = new DataAclType(new LinkedList<String>());
    public static DataAclType PUBLIC = new DataAclType(createList(PUBLIC_PERMISSIONS));
    public static DataAclType MY_ALGOS = new DataAclType(createList(MY_ALGOS_PERMISSIONS));

}
