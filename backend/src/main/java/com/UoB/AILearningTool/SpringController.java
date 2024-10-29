package com.UoB.AILearningTool;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class SpringController {
    private final Logger log = LoggerFactory.getLogger(SpringController.class);
    private final DatabaseController DBC = new DatabaseController();

    // If user consents to optional cookies, assign a unique user ID for them.
    @GetMapping("/signup")
    public void signup(@CookieValue(value = "optionalConsent", defaultValue = "false") boolean optionalConsent,
                      HttpServletResponse response) {
        if (optionalConsent) {
            Cookie userIDCookie = new Cookie("userID", DBC.addUser());
            userIDCookie.setMaxAge(30 * 24 * 60 * 60); // Cookie will expire in 30 days
            userIDCookie.setSecure(true);
            response.addCookie(userIDCookie);
            log.info("Assigned a new userID.");
        }
    }

    // If user revokes their consent for data storage / optional cookies,
    // remove all data stored about them.
    @GetMapping("/revokeConsent")
    public void revokeConsent(@CookieValue(value = "userID", defaultValue = "") String userID,
                                 HttpServletResponse response) {
        if (userID.isEmpty()) {
            log.info("Cannot withdraw consent of userID {}", userID);
        }
        Cookie cookie = new Cookie("userID", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        DBC.removeUser(userID);
    }
}
