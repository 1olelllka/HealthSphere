package com._olelllka.HealthSphere_Backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceUnitTest {

    @InjectMocks
    private JwtService jwtService;

    @Test
    public void testThatGenerateJwtWorksAsIntended() {
        // given
        String username = "email@email.com";
        // when
        String jwt = jwtService.generateJwt(username);
        // then
        assertNotNull(jwt);
    }

    @Test
    public void testThatExtractUsernameWorksAsIntended() {
        // given
        String username = "email@email.com";
        String jwt = jwtService.generateJwt(username);
        // when
        String result = jwtService.extractUsername(jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, username)
        );
    }

    @Test
    public void testThatIsTokenValidReturnsFalseIfUsernameIsIncorrect() {
        // given
        String username = "email@email.com";
        String jwt = jwtService.generateJwt(username);
        // when
        boolean result = jwtService.isTokenValid(jwt, "incorrectUser");
        // then
        assertFalse(result);
    }

    @Test
    public void testThatIsTokenValidReturnsTrueIfUsernameIsCorrect() {
        // given
        String username = "email@email.com";
        String jwt = jwtService.generateJwt(username);
        // when
        boolean result = jwtService.isTokenValid(jwt, username);
        // then
        assertTrue(result);
    }

}
