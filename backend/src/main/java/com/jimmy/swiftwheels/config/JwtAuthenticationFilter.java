package com.jimmy.swiftwheels.config;

import com.jimmy.swiftwheels.token.TokenRepository;
import com.jimmy.swiftwheels.util.JwtUtil;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull javax.servlet.http.HttpServletRequest request,
                                    @NonNull javax.servlet.http.HttpServletResponse response,
                                    @NonNull javax.servlet.FilterChain filterChain) throws javax.servlet.ServletException, IOException {
        if(request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtUtil.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() ) // && !t.isRevoked()
                    .orElse(false);
            if (jwtUtil.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails((javax.servlet.http.HttpServletRequest) request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
