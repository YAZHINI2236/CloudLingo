package com.chat.chattranslator.websocket;

import com.chat.chattranslator.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static Set<WebSocketSession> sessions =
            Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private TranslationService translationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("User connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 1️⃣ Get raw message
        String msg = message.getPayload();

        // 2️⃣ Simple rule to decide language direction
        // (You can improve this later with user preferences)
        boolean isTamil = containsTamil(msg);

        String sourceLang;
        String targetLang;

        if (isTamil) {
            sourceLang = "ta";
            targetLang = "en";
        } else {
            sourceLang = "en";
            targetLang = "ta";
        }

        // 3️⃣ Call optimized TranslationService
        String translated =
                translationService.translate(msg, sourceLang, targetLang);

        System.out.println("Original: " + msg);
        System.out.println("Translated: " + translated);

        // 4️⃣ Send translated message to all OTHER users
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                if (!s.getId().equals(session.getId())) {
                    s.sendMessage(new TextMessage(translated));
                }
            }
        }

        // 5️⃣ (Optional) Send original back to sender (or skip)
        // session.sendMessage(new TextMessage(msg));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        System.out.println("User disconnected: " + session.getId());
    }

    // ---------------- HELPER ----------------

    // Simple Tamil Unicode check
    private boolean containsTamil(String text) {
        for (char c : text.toCharArray()) {
            if (c >= 0x0B80 && c <= 0x0BFF) {
                return true;
            }
        }
        return false;
    }
}
