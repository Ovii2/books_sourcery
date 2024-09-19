package org.example.company;

import org.example.company.repository.TokenRepository;
import org.example.company.model.Token;
import org.example.company.service.LogoutService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class LogoutUserTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private LogoutService logoutService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    @Test
    void testLogout_Success() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        Authentication authentication = mock(Authentication.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(response.getWriter()).thenReturn(writer);

        Token token = new Token();
        token.setToken("validToken");

        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(token));

        SecurityContext securityContext = mock(SecurityContext.class);
        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);

        logoutService.logout(request, response, authentication);

        verify(tokenRepository, times(1)).findByToken("validToken");
        verify(tokenRepository, times(1)).delete(token);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(writer, times(1)).write("Logout successful");

        securityContextHolderMock.verify(() -> SecurityContextHolder.clearContext(), times(1));
    }

    @Test
    void testLogout_NoToken() throws IOException {
        // Setup mocks
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        Authentication authentication = mock(Authentication.class);

        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        logoutService.logout(request, response, authentication);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer, times(1)).write("No JWT token found in the request headers");
    }

    @Test
    void testLogout_InvalidToken() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        Authentication authentication = mock(Authentication.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(response.getWriter()).thenReturn(writer);

        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        logoutService.logout(request, response, authentication);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer, times(1)).write("Invalid JWT token");
    }
}
