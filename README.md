演唱會售票網站
專案簡介
這是一個專門為演唱會門票銷售而開發的網站系統。它具有以下核心功能和特點:

即時流量監控和自動異常行為封鎖
後台數據實時監控,掌握活動售票狀況
友善簡潔的前端操作介面,提供順暢的購票體驗
支持高併發處理,可承載大量用戶同時搶票

技術架構
該網站採用了以下主要技術組件:

前端: <span style="color:#0074D9">React</span>
後端: <span style="color:#2ECC40">Spring Boot</span>
緩存: <span style="color:#FF851B">Redis</span>
消息隊列: <span style="color:#B10DC9">RabbitMQ</span>
反向代理: <span style="color:#39CCCC">Nginx</span>
資料庫: <span style="color:#AAAAAA">MySQL</span>

各組件在系統中扮演的角色如下:

Nginx: 提供票務資料快取、驗證碼暫存,並實現動態流量封鎖。
Redis: 用於高併發負載均衡、資源快取加速,以及異常流量防護。
Spring Boot: 負責核心業務邏輯處理、資料庫整合,以及身份驗證授權。
RabbitMQ: 提供訂單請求排隊、非同步資料處理,幫助削峰填谷。
MySQL: 作為主要的關係型數據庫,負責持久化各類業務數據。

安全防護
該系統實現了多層面的安全防護措施:

Nginx + Lua:

多 IP 檢測:同一用戶名超過 3 個 IP 判定為異常。
高頻檢測:1 分鐘超過 100 次請求判定為異常。
動態封鎖:將異常用戶記錄至 <span style="color:#FF851B">Redis</span>,並同步到 <span style="color:#39CCCC">Nginx</span>。


身份驗證:

使用 <span style="color:#2ECC40">JWT</span> 進行用戶認證授權。
透過 <span style="color:#FFDC00">Kaptcha</span> 圖形驗證碼增強安全性。



系統監控
系統提供了以下監控功能:

即時流量監控:

記錄每秒請求數,並用動態圖表顯示。
展示當日總請求量。


API 管理:

紀錄全站所有請求時間、IP、用戶、API 請求及執行時間。
幫助管理者監控伺服器狀況。


異常用戶封鎖:

可透過用戶名和 IP 封鎖異常使用者。
被封鎖用戶在全站任何頁面都會導向黑畫面。
