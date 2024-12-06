package com.example.demo.model.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data  // Lombok 自動生成 getter、setter、toString 等方法
@AllArgsConstructor  // Lombok 自動生成包含所有參數的構造器
public  class TicketUpdate {
   // 活動ID
   private Integer eventId;
   
   // 票區名稱
   private String section;
   
   // 剩餘票數
   private Integer remainingTickets;

   // 將對象轉換為 JSON 格式的字符串
   public String toJson() {
       return String.format(
           "{\"eventId\": %d, \"section\": \"%s\", \"remainingTickets\": %d}",
           eventId, section, remainingTickets
       );
   }
}