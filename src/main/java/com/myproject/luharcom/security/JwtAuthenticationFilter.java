package com.myproject.luharcom.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.myproject.luharcom.models.PrincipalUser;
import com.myproject.luharcom.service.CustomUserDetailsService;
import com.myproject.luharcom.utils.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Optional<String> jwt = getTokenFromRequest(request);
            if (jwt.isPresent()) {
                PrincipalUser principalUser = jwtHelper.getUserFromToken(jwt.get());
                UserDetails userDetails = userDetailsService.loadUserByUsername(principalUser.getUsername());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request){
         String token = request.getHeader("Authorization");
         if(StringUtils.hasText(token) && token.startsWith("bearer ")){
             return Optional.of(token.substring(7));
         }
         return Optional.empty();
    }

}
