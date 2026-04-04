package com.backend.usermanagement.security;

import com.backend.usermanagement.domain.entity.AuditAction;
import com.backend.usermanagement.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect     // Bu sınıfın bir AOP Aspect olduğunu belirtir
@Component  // Spring bean olarak register et
public class AuditAspect {

    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // AuthService.register() metodunu izle
    @Around("execution(* com.backend.usermanagement.service.AuthService.register(..))")
    public Object auditRegister(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = (String) joinPoint.getArgs()[0];
        String ip = getClientIp();
        try {
            Object result = joinPoint.proceed();
            auditLogService.log(email, AuditAction.REGISTER, true, ip, "Registration successful");
            return result;
        } catch (Exception e) {
            auditLogService.log(email, AuditAction.REGISTER, false, ip, e.getMessage());
            throw e;
        }
    }

    // UserService.changePassword() metodunu izle
    @Around("execution(* com.backend.usermanagement.service.UserService.changePassword(..))")
    public Object auditChangePassword(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = (String) joinPoint.getArgs()[0];
        String ip = getClientIp();
        try {
            Object result = joinPoint.proceed();
            auditLogService.log(email, AuditAction.PASSWORD_CHANGE, true, ip, "Password changed");
            return result;
        } catch (Exception e) {
            auditLogService.log(email, AuditAction.PASSWORD_CHANGE, false, ip, e.getMessage());
            throw e;
        }
    }

    // AuthService.createPasswordResetToken() metodunu izle
    @Around("execution(* com.backend.usermanagement.service.AuthService.createPasswordResetToken(..))")
    public Object auditPasswordResetRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = (String) joinPoint.getArgs()[0];
        String ip = getClientIp();
        try {
            Object result = joinPoint.proceed();
            auditLogService.log(email, AuditAction.PASSWORD_RESET_REQUEST, true, ip, "Password reset token created");
            return result;
        } catch (Exception e) {
            auditLogService.log(email, AuditAction.PASSWORD_RESET_REQUEST, false, ip, e.getMessage());
            throw e;
        }
    }

    // UserService.deactivateAccount() metodunu izle
    @Around("execution(* com.backend.usermanagement.service.UserService.deactivateAccount(..))")
    public Object auditDeactivate(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = (String) joinPoint.getArgs()[0];
        String ip = getClientIp();
        try {
            Object result = joinPoint.proceed();
            auditLogService.log(email, AuditAction.ACCOUNT_DEACTIVATE, true, ip, "Account deactivated");
            return result;
        } catch (Exception e) {
            auditLogService.log(email, AuditAction.ACCOUNT_DEACTIVATE, false, ip, e.getMessage());
            throw e;
        }
    }

    // UserService.updateUserRole() metodunu izle
    @Around("execution(* com.backend.usermanagement.service.UserService.updateUserRole(..))")
    public Object auditRoleUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = (Long) joinPoint.getArgs()[0];
        String roleName = (String) joinPoint.getArgs()[1];
        String ip = getClientIp();
        try {
            Object result = joinPoint.proceed();
            auditLogService.log("admin", AuditAction.ROLE_UPDATE, true, ip,
                    "Role " + roleName + " added to user id: " + userId);
            return result;
        } catch (Exception e) {
            auditLogService.log("admin", AuditAction.ROLE_UPDATE, false, ip, e.getMessage());
            throw e;
        }
    }

    // HTTP request'ten client IP adresini al
    private String getClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "unknown";
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            return (forwarded != null) ? forwarded.split(",")[0] : request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
