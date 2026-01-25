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

    private static final Set<WebSocketSession> sessions =
            Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private TranslationService translationService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("User connected: " + session.getId());
        System.out.println("Total users: " + sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String msg = message.getPayload();

        boolean isTamil = containsTamil(msg);

        String sourceLang = isTamil ? "ta" : "en";
        String targetLang = isTamil ? "en" : "ta";

        String translated =
                translationService.translate(msg, sourceLang, targetLang);

        System.out.println("From " + session.getId() + ": " + msg);
        System.out.println("Translated: " + translated);

        String json = """
        {
          "from": "%s",
          "original": "%s",
          "translated": "%s"
        }
        """.formatted(
                session.getId(),
                escape(msg),
                escape(translated)
        );

        synchronized (sessions) {
            for (WebSocketSession s : sessions) {
                if (s.isOpen() && !s.getId().equals(session.getId())) {
                    s.sendMessage(new TextMessage(json));
                }
            }
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        System.out.println("User disconnected: " + session.getId());
        System.out.println("Total users: " + sessions.size());
    }


    private boolean containsTamil(String text) {
        for (char c : text.toCharArray()) {
            if (c >= 0x0B80 && c <= 0x0BFF) {
                return true;
            }
        }
        return false;
    }

    private String escape(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
