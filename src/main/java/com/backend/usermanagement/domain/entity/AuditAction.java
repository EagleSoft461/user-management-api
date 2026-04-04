package com.backend.usermanagement.domain.entity;

public enum AuditAction {
    LOGIN,
    REGISTER,
    PASSWORD_CHANGE,
    PASSWORD_RESET_REQUEST,
    PASSWORD_RESET,
    ACCOUNT_DEACTIVATE,
    ROLE_UPDATE,
    TOKEN_REFRESH
}
