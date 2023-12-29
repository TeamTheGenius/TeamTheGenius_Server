package com.genius.todoffin.util.formatter;

public final class CommonPattern {
    public static final String MANAGER_ID_PATTERN = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,16}$";
    public static final String MANAGER_PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$";
    public static final String IP_ADDRESS_PATTERN = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    public static final String Y_OR_N = "^[YN]$";
}
