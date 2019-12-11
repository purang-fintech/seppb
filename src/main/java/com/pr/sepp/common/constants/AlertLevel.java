package com.pr.sepp.common.constants;

public enum AlertLevel {
    FATAL(4,"致命"),
    SERIOUS(3,"严重"),
    IMPORTANT(2,"重要"),
    GENERAL(1,"一般"),
    UNKNOWN(0,"未知");
    private Integer key;
    private String name;

    AlertLevel(Integer key, String name) {
        this.key = key;
        this.name = name;
    }

    public Integer getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

   public static AlertLevel enumByKey(Integer key) {
       for (AlertLevel value : values()) {
           if (value.getKey().equals(key)) {
               return value;
           }
       }
       return UNKNOWN;
   }

}
