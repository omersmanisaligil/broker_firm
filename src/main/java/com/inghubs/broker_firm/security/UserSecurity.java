package com.inghubs.broker_firm.security;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class UserSecurity implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier authenticationSupplier, RequestAuthorizationContext ctx) {
        UUID userId = UUID.fromString(ctx.getVariables().get("userId"));
        Authentication authentication = (Authentication) authenticationSupplier.get();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        if(isAdmin){
            return new AuthorizationDecision(true);
        }

        return new AuthorizationDecision(hasUserId(authentication, userId));
    }
    public boolean hasUserId(Authentication authentication, UUID userId) {
        UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();
        return user.getId() == userId;
    }

}