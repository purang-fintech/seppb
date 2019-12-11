package com.pr.sepp.common.constants;

public final class SeppConstants {
    private SeppConstants() {
        throw new IllegalAccessError();
    }

    public static final String LOGIN_RESULT = "result";
    public static final String LOGIN_USER = "user";

    public static final Integer GLOBAL_ERROR_CODE = 1001;
    public static final String NON_LDAP_ERROR_MESSAGE = "没有正确配置后端LDAP服务：配置项为空!";

    public static final String LDAP_URL = "ldap://%s:%s";
    public static final String SEARCH_FILTER = "(&(|(objectClass=user)(objectClass=organizationalUnit)(objectClass=group))(|(cn=%s)(dn=%s)(sAMAccountName=%s)))";
    public static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
}
