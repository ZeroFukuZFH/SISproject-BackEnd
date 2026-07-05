package com.example.SISproject.filter;

import com.example.SISproject.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// THIS CLASS ONLY RUNS ONCE PER REQUEST TO VALIDATE REQUESTS THAT NEED AUTHENTICATION

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        Cookie[] httpCookies = request.getCookies();
        String token = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
        } else if(httpCookies != null) {
            String jwtCookie = "jwt";
            for (Cookie cookie : httpCookies) {
                if (jwtCookie.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        String email = null;
        if(token != null && SecurityContextHolder.getContext().getAuthentication() == null){
             try {
                 email = jwtService.getUserEmail(token);
             } catch (Exception e) {
                 logger.error("JWT validation failed: " + e.getMessage());
             }
        }

        if (email != null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}
