package tech.andrefsramos.msAuth.enums;

public enum UserRoleEnum {
    COMMON, MANAGER, ADMIN;

    public static UserRoleEnum fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
