package com.example.demo.websocket;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.demo.model.dto.event.WebSocketTicketDto;
import com.example.demo.repository.sales.SalesRepositoryJdbc;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TicketService {

    private final SessionManager sessionManager;
    private final SalesRepositoryJdbc salesRepositoryJdbc;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;

    public TicketService(SessionManager sessionManager, SalesRepositoryJdbc salesRepositoryJdbc, TaskScheduler taskScheduler) {
        this.sessionManager = sessionManager;
        this.salesRepositoryJdbc = salesRepositoryJdbc;
        this.taskScheduler = taskScheduler;
    }

    public void enableQuery() {
        if (scheduledFuture == null || scheduledFuture.isCancelled()) {
        	  scheduledFuture = taskScheduler.scheduleAtFixedRate(
                      this::updateTicketStatus,
                      Instant.now(), // 從當前時間開始
                      Duration.ofSeconds(1) // 每 1 秒執行一次
                  );           
        	  System.out.println("啟用定時查詢");
        }
    }

    public void disableQuery() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            System.out.println("停止定時查詢");
        }
    }

    private void updateTicketStatus() {
        Map<WebSocketSession, List<WebSocketTicketDto>> sessionEventMap = sessionManager.getSessionEventMap();

        if (sessionEventMap.isEmpty()) {
            System.out.println("無活動的客戶端連接，暫時不發送數據");
            return;
        }

        for (Map.Entry<WebSocketSession, List<WebSocketTicketDto>> entry : sessionEventMap.entrySet()) {
            WebSocketSession session = entry.getKey();
            List<WebSocketTicketDto> subscriptions = entry.getValue();

            if (session == null || !session.isOpen()) {
                sessionManager.removeSession(session);
                continue;
            }

            for (WebSocketTicketDto eventSection : subscriptions) {
                try {
                    Integer remaining = salesRepositoryJdbc.findRemaingByEventIdAndSection(
                            eventSection.getEventId(),
                            eventSection.getSection()
                    );
                    String status = String.format(
                            "{\"eventId\": %d, \"section\": \"%s\", \"remainingTickets\": %d}",
                            eventSection.getEventId(),
                            eventSection.getSection(),
                            remaining
                    );
                    session.sendMessage(new TextMessage(status));
                    System.out.println("發送數據成功: " + status);
                } catch (Exception e) {
                    System.err.println("向客戶端發送數據失敗，會話 ID: " + session.getId());
                    e.printStackTrace();
                }
            }
        }
    }

}
