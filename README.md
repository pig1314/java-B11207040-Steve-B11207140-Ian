# 題目：俄羅斯方塊(Tetris)

---

## 組員：

| 學號      | 姓名   |
| ------- | ------- |
| B11207040 | 林信儒 |
| B11207140 | 劉胤言 |

---

## 分工說明

| 項目類別    | 主要負責人   | 內容摘要 |
| ------- | ------- | --------------- |
| 主要功能 | 劉胤言 | 方塊相關、遊戲基本功能 |
| 主要功能 | 林信儒 | 模式擴展、頁面安排 |
|  UI  | 劉胤言 | 遊戲顯示調整 |
|  UI  | 林信儒 | 按鈕行為改善 |
| BGM | 劉胤言 | 設計初版 |
| BGM | 林信儒 | 改善播放問題 |
| 測試與改善 | 全體 | Bug排除 |

---

## 報告影片連結

* [點我看遊戲演示影片]()

---

## 遊戲說明：
### 一、 遊戲流程
• 選擇模式

• 放置方塊

• 填滿一排消除

• 無法放置時結束
### 二、 操作方式
 
1. 街機模式(Arcade)、挑戰模式(Challenge)

| 按鍵 | 功能 |
| ------- | ------- |
| ⭠⭣⭢| 移動 |
| ⭡ | 旋轉(CW) |
| Z | 旋轉(CCW) |
| SPACE | 放置 |
| C | 儲存 |
| P | 暫停 |
   
2. 對戰模式(Battle)

| 玩家一 | 玩家二 | 功能 |
| ------- | ------- | ------- |
| ASD | ⭠⭣⭢ | 移動 |
| W | ⭡ | 旋轉(CW) |
| Q | K | 旋轉(CCW) |
| SPACE | M | 放置 |
| C | L | 儲存 |
| O | P | 暫停 |

---
## 下載與執行
### 系統需求
•  **作業系統**：Windows (Windows 8 or Higher) / macOS ARM 64 or X64 (Mavericks 10.9 or Higher) / Linux / UNIX

•  **JAVA**：JDK11 以上
1. 下載 Tetris.jar
2. 開啟終端機執行：
   
   ```bash
   java -jar Tetris.jar
   ```
   
## 功能列表：
### 1. 畫面呈現
• 遊戲畫面顯示、刷新

• 根據模式改變畫面排佈

---

### 2. 玩家輸入
• 讀取鍵盤輸入

• 讀取滑鼠輸入

---

### 3. 背景音樂
• 自動撥放內建音樂

---

### 4. 模式選擇
• 首頁按鈕供選擇模式

• 不同模式不同玩法

---

### 5. 計分系統
• 依據消除行數加分

---

### 6. 等級系統
• 依據分數區分等級

• 等級影響下落速度

---

### 7.暫停、重新開始
• 遊戲可隨時暫停

• 暫停後可重新開始或回到主頁

---

### 8. 結束頁面
• 遊戲可隨時暫停

• 暫停後可重新開始或回到主頁

---

### 9. 障礙生成
• 挑戰模式會隨時間生成障礙

• 對戰模式根據對手消除生成障礙

---

## AI對談摘要：
| 指令 | 成果 |
| ------- | ------- |
| 幫我用java寫一個俄羅斯方塊的程式 | 初版俄羅斯方塊 |
| 請依據tetris.wiki增加等級，在畫面上顯示，並調整遊戲速度 | 新增功能 |
| 我想將每種方塊各自做成一個class，並把移動與轉向依方塊獨立編寫 | 將方塊動作從主程式中分開 |
| implement the srs system | SRS system解釋 |
| I want a new UI that has game board in the middle with score and level on the top with their own space, a space for preview of the next block(don't implement the function yet), and a hold space(also don't implement the function yet) | UI更新 |
| now we can implement the preview and hold feature | 引導Grok方向 |
| A main menu for player to hit start | Main Menu製作 |
| I want to make different gamemode, but first let's settle with the UI, when click start, shows 3 button, Arcade, Challenge and Battle | Main Menu更新 |
| I think the split is a better way consider the future, but in order to prevent same code appearing twice, can we put key game element such as moving, pause and placing pieces into a something? and all the game mode  including Arcade takes it and add what it need or modify it. Analyze this option and show if it can be done |         Abstract Panel製作 |
| 將障礙調整至每次只有一個空格 | 挑戰模式更新 |
| The battle mode had also been implemented, but there is quite an issue regarding the conflict between two players' board, see if you can spot the issue and provide solution | 對戰模式Bug修復 |
| I want to make that you can choose to battle a bot, how do we implement it into the game, please first analyze different tetris bot algorithm | 人機對戰諮詢(未實現) |

---

## 參考資料：
• Grok 3 提供初版、協助修改

---

## UML 類別圖 (Class Diagram)

![Tetris class diagram](https://github.com/pig1314/java-B11207040-Steve-B11207140-Ian/blob/main/Images/Tetris%20class%20diagram.png)

---

## 流程圖 (Flow Chart)
### • Arcade Mode
![Tetris Arcade mode flow chart](https://github.com/pig1314/java-B11207040-Steve-B11207140-Ian/blob/main/Images/Tetris%20Arcademode%20flow%20chart.png)
### • Challenge Mode
![Tetris challenge mode flow chart](https://github.com/pig1314/java-B11207040-Steve-B11207140-Ian/blob/main/Images/Tetris%20challenge%20mode%20flow%20chart.png)
### • Battle Mode
![Tetris battle mode flow chart](https://github.com/pig1314/java-B11207040-Steve-B11207140-Ian/blob/main/Images/Tetris%20battle%20mode%20flow%20chart.png)

---

## 序列圖 (Sequence Diagram)
### • Arcade Mode
![Tetris Arcade mode sequential diagram](https://github.com/pig1314/java-B11207040-Steve-B11207140-Ian/blob/main/Images/Tetris%20Arcade%20mode%20sequential%20diagram.png)
### • Challenge Mode
![Tetris challenge mode sequential diagrma](https://github.com/pig1314/java-B11207040-Steve-B11207140-Ian/blob/main/Images/Tetris%20challenge%20mode%20sequential%20diagrma.png)
### • Battle Mode
![Tetris battle mode sequential diagram](https://github.com/pig1314/java-B11207040-Steve-B11207140-Ian/blob/main/Images/Tetris%20battle%20mode%20sequential%20diagram.png)

---

## To be implement
clear line effect flashing popup word sound effect SRS exploit fix game over menu bug fix hold I block visual bug score went over space
