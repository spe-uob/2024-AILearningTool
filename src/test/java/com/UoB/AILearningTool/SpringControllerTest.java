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

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mockResponse.getWriter()).thenReturn(mockWriter);
    }

    @InjectMocks
    private SpringController springController;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @Test
    public void testNewUserCreated() throws IOException {

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
    public void testRevokeConsent() throws IOException {

        // define a userID "user123" to revoke consent for
        String userID = "user123";

        // mock removeUser to return true
        when(mockDBController.removeUser(userID)).thenReturn(true);

        // verify that removeUser was called with the correct userID
        verify(mockDBController, times(1)).removeUser(userID);

        // verify that the status was set to 200 when user was removed
        verify(mockResponse, times(1)).setStatus(200);

        // capture the cookie added to the response
        verify(mockResponse, times(1)).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        // assertions on captured cookie
        assertEquals("userID", capturedCookie.getName(), "Cookie name should be userID");
        assertEquals(" ", capturedCookie.getValue(), "Cookie value should be empty");
        assertEquals(0, capturedCookie.getMaxAge(), "Cookie max age should be 0");
    }

    @Test
    public void testCreateChat() throws IOException {

        String userID = "user123";
        String initialMessage = "Hello chatbot!";
        String chatID = "chat456";
        User mockUser = new User(true);
        Chat mockChat = mock(Chat.class);
        String messageHistory = "System prompt\n" + initialMessage;
        String chatbotResponseText = "Chatbot Response";

        // mock DatabaseController to return a user and handle chat creation
        when(mockDBController.getUser(userID)).thenReturn(mockUser);
        when(mockDBController.createChat(mockUser, initialMessage)).thenReturn(chatID);
        when(mockDBController.getChat(mockUser, chatID)).thenReturn(mockChat);

        // mock chat to return the message history
        when(mockChat.getMessageHistory(mockUser)).thenReturn(messageHistory);

        // mock OpenAIAPIController to return a successful chatbot response
        when(mockWXC.sendUserMessage(messageHistory)).thenReturn(new WatsonxResponse(200, chatbotResponseText));

        // mock chat's addAIMessage to return true
        when(mockChat.addAIMessage(userID, chatbotResponseText)).thenReturn(true);

        // call the createChat method with the arranged parameters.
        springController.createChat(userID, initialMessage, mockResponse);

        verify(mockDBController, times(1)).getUser(userID);
        verify(mockDBController, times(1)).createChat(mockUser, initialMessage);
        verify(mockDBController, times(1)).getChat(mockUser, chatID);
        verify(mockChat, times(1)).getMessageHistory(mockUser);
        verify(mockChat, times(1)).addAIMessage(userID, chatbotResponseText);
        verify(mockWXC, times(1)).sendUserMessage(messageHistory);

        verify(mockResponse, times(1)).setContentType("text/plain");
        verify(mockResponse, times(1)).setStatus(200);
        verify(mockResponse.getWriter(), times(1)).write(chatID);

    }
}



