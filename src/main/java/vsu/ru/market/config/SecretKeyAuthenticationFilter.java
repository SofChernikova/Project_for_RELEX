package vsu.ru.market.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vsu.ru.market.models.User;
import vsu.ru.market.services.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecretKeyAuthenticationFilter extends OncePerRequestFilter {

    private final UserService keyService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final User user;
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }
        user = keyService.extractUser(authHeader);
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        authHeader, user, user.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}

