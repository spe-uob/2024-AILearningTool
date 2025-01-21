package com.UoB.AILearningTool;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// integrates Mockito with JUnit
@ExtendWith(MockitoExtension.class)
class SpringControllerTest {
    @Mock
    private DatabaseController mockDBController;

    @Mock
    private OpenAIAPIController mockWXC;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private PrintWriter mockWriter;

    // This Tells Mockito to inject the @Mock fields into SpringController's constructor
    @InjectMocks
    private SpringController springController;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @BeforeEach
    public void setUp() throws IOException {
        Mockito.lenient().when(mockResponse.getWriter()).thenReturn(mockWriter);
    }

    @Test
    public void testNewUserCreated() {

        // mock addUser to return a specific ID "user123" when called wit optional consent = true
        boolean optionalConsent = true;
        String generatedUserID = "user123";
        when(mockDBController.addUser(optionalConsent)).thenReturn(generatedUserID);

        // call the signup method with the arranged parameters
        springController.signup(optionalConsent, mockResponse);

        // verify that addUser was called with optionalConsent = true
        verify(mockDBController, times(1)).addUser(optionalConsent);

        // capture the cookie added to the response
        verify(mockResponse, times(1)).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();


        // assertions on the captured cookie
        assertEquals("userID", capturedCookie.getName(), "Cookie name should be userID");
        assertEquals(generatedUserID, capturedCookie.getValue(), "Cookie value should match generated userID");
        assertEquals(30 * 24 * 60 * 60, capturedCookie.getMaxAge(), "Cookie max age should be 30 days");
    }

    @Test
    public void testRevokeConsent() {

        // define a userID "user123" to revoke consent for
        String userID = "user123";

        // mock removeUser to return true
        when(mockDBController.removeUser(userID)).thenReturn(true);

        springController.revokeConsent(userID, mockResponse);

        // verify that removeUser was called with the correct userID
        verify(mockDBController, times(1)).removeUser(userID);

        // verify that the status was set to 200 when user was removed
        verify(mockResponse, times(1)).setStatus(HttpServletResponse.SC_OK);

        // capture the cookie added to the response
        verify(mockResponse, times(1)).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        // assertions on captured cookie
        assertEquals("userID", capturedCookie.getName(), "Cookie name should be userID");
        assertEquals("", capturedCookie.getValue(), "Cookie value should be empty");
        assertEquals(0, capturedCookie.getMaxAge(), "Cookie max age should be 0");
    }

    @Test
    void testCreateChat() {
        // Given
        String userID = "user123";
        String initialMessage = "Hello chatbot!";
        String generatedChatID = "abc123";
        User mockUser = new User(true);
        Chat mockChat = mock(Chat.class);

        // Mock out DB calls
        when(mockDBController.getUser(userID)).thenReturn(mockUser);
        when(mockDBController.createChat(mockUser, initialMessage)).thenReturn(generatedChatID);
        when(mockDBController.getChat(mockUser, generatedChatID)).thenReturn(mockChat);

        // Mock chat & AI calls
        String messageHistory = "System prompt\nHello chatbot!";
        when(mockChat.getMessageHistory(mockUser)).thenReturn(messageHistory);

        WatsonxResponse aiResponse = new WatsonxResponse(200, "AI says hi");
        when(mockWXC.sendUserMessage(messageHistory)).thenReturn(aiResponse);

        // When
        springController.createChat(userID, initialMessage, mockResponse);

        // Then
        verify(mockDBController).getUser(userID);
        verify(mockDBController).createChat(mockUser, initialMessage);
        verify(mockDBController).getChat(mockUser, generatedChatID);

        verify(mockChat).getMessageHistory(mockUser);
        verify(mockWXC).sendUserMessage(messageHistory);

        // AI responded with status 200, so we expect addAIMessage to be called:
        verify(mockChat).addAIMessage(userID, "AI says hi");

        // Finally, verify response
        verify(mockResponse).setContentType("text/plain");
        verify(mockResponse).setStatus(200);
        verify(mockWriter).write(generatedChatID);
    }

}



