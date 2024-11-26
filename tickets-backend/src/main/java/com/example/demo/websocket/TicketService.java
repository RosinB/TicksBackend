package com.example.demo.websocket;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.model.dto.event.WebSocketTicketDto;
import com.example.demo.repository.sales.SalesRepositoryJdbc;

import java.util.Map;

@Service
public class TicketService {

    private final TicketWebSocketHandler webSocketHandler;
    private final SalesRepositoryJdbc salesRepositoryJdbc;

    public TicketService(TicketWebSocketHandler webSocketHandler, SalesRepositoryJdbc salesRepositoryJdbc) {
        this.webSocketHandler = webSocketHandler;
        this.salesRepositoryJdbc = salesRepositoryJdbc;
    }

    @Scheduled(fixedRate = 5000) // 每 5 秒執行一次
    public void updateTicketStatus() {
        Map<WebSocketSession, WebSocketTicketDto> sessionEventMap = webSocketHandler.getSessionEventMap();

        if (sessionEventMap.isEmpty()) {
            System.out.println("無活動的客戶端連接，暫停查詢");
            return;
        }

        for (Map.Entry<WebSocketSession, WebSocketTicketDto> entry : sessionEventMap.entrySet()) {
            WebSocketSession session = entry.getKey();
            WebSocketTicketDto eventSection = entry.getValue();

            if (session == null || !session.isOpen()) {
                System.out.println("會話已關閉，移除該連接: " + session.getId());
                sessionEventMap.remove(session);
                continue;
            }

            try {
                // 查詢資料庫中的剩餘票數
                Integer remaining = salesRepositoryJdbc.findRemaingByEventIdAndSection(
                        eventSection.getEventId(),
                        eventSection.getSection()
                );
                // 構造 JSON 消息
                String status = String.format(
                        "{\"eventId\": %d, \"section\": \"%s\", \"remainingTickets\": %d}",
                        eventSection.getEventId(),
                        eventSection.getSection(),
                        remaining
                );

                // 發送消息給客戶端
                session.sendMessage(new TextMessage(status));
                System.out.println("發送數據成功: " + status);
            } catch (Exception e) {
                System.err.println("向客戶端發送數據失敗，會話 ID: " + session.getId());
                e.printStackTrace();
            }
        }
    }
}
