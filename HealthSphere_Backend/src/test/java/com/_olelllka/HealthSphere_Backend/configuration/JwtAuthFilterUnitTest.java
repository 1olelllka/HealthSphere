package com._olelllka.HealthSphere_Backend.configuration;

import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterUnitTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @Test
    public void testThatFilterDoesNotExecutesWhenThereAreNoCookies() throws Exception{
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    public void testThatFilterDoesNotExecutesWhenThereIsNoCorrectCookie() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie cookie = new Cookie("someCookie", "someValue");
        request.setCookies(cookie);
        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    public void testThatFilterDoesNotExecutesWhenTokenIsInvalid() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie cookie = new Cookie("accessToken", "someValue");
        UserEntity user = UserEntity.builder().email("email@email.com").password("password").role(Role.ROLE_PATIENT).build();
        request.setCookies(cookie);
        // when
        when(jwtService.extractUsername(cookie.getValue())).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid("someValue", user.getUsername())).thenReturn(false);
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testThatFilterAuthenticatesWhenEverythingIsCorrect() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Cookie cookie = new Cookie("accessToken", "someValue");
        UserEntity user = UserEntity.builder().email("email@email.com").password("password").role(Role.ROLE_PATIENT).build();
        request.setCookies(cookie);
        // when
        when(jwtService.extractUsername(cookie.getValue())).thenReturn(user.getUsername());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);
        when(jwtService.isTokenValid("someValue", user.getUsername())).thenReturn(true);
        jwtAuthFilter.doFilterInternal(request, response, filterChain);
        // then
        verify(filterChain, times(1)).doFilter(request, response);
        assertAll(
                () -> assertNotNull(SecurityContextHolder.getContext().getAuthentication()),
                () -> assertInstanceOf(UsernamePasswordAuthenticationToken.class, SecurityContextHolder.getContext().getAuthentication())
        );
    }

}
