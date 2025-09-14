# 🎮 Game Arena – Board-Verse Edition

A modern, beautifully crafted Java desktop hub for classic board games—Tic-Tac-Toe, Othello, and Sudoku—reimagined with advanced features, statistics, sound, and a seamless user experience.

---

## 🌟 Why Game Arena?

Game Arena is more than just a collection of games. It’s a showcase of how classic board games can be elevated with modern UI, rich interactivity, and thoughtful design. Whether you’re a casual player or a board game enthusiast, Game Arena offers a polished, all-in-one experience.

---

## 🚀 Features at a Glance

| Game           | Features & Variations                                                                                 |
|----------------|------------------------------------------------------------------------------------------------------|
| **Tic-Tac-Toe**| - Play on boards from **2×2 up to 10×10**<br>- **2×2 mode** with a unique, secure **toss** to decide who starts<br>- Undo/Redo moves<br>- Player name & symbol customization<br>- Score tracking<br>- Modern, animated board UI<br>- Change board size anytime<br>- Visual highlights and sound effects |
| **Othello**    | - Classic 8×8 board<br>- Beautiful wooden board and glossy pieces<br>- Valid move highlighting<br>- Real-time score display<br>- Player name customization<br>- Timer and new game controls<br>- Piece flipping animation and sound<br>- Skips turn if no valid moves<br>- Endgame dialog and replay option |
| **Sudoku**     | - Four difficulty levels (Easy to Super Hard)<br>- **Lifeline system** (5 hearts)<br>- Hints and solution reveal<br>- Timer and clear controls<br>- Custom number pad for input<br>- Colorful, modern board design<br>- Real-time input validation<br>- Unique puzzle generation with solution check<br>- Visual and sound feedback for errors |

---

## 🖼️ Screenshots

### Main Menu
![Main Menu](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/blob/main/java-gui/images/main-menu.PNG)

---

### Tic-Tac-Toe (3x3)
![Tic-Tac-Toe 3x3](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/blob/main/java-gui/images/tictactoe-3x3.PNG)

---

### Tic-Tac-Toe (2x2 Toss Mode)
![Tic-Tac-Toe 2x2 Toss](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/blob/main/java-gui/images/tictactoe-2x2-toss.PNG)

---

### Othello
![Othello Gameplay](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/blob/main/java-gui/images/othello-gameplay.PNG)

---

### Sudoku
![Sudoku Gameplay](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/blob/main/java-gui/images/sudoku-gameplay.PNG)

---

### Sudoku (Lifeline Finished)
![Sudoku Lifeline Finished](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/blob/main/java-gui/images/sudoku-lifeline-finished.PNG)

---

## 🖥️ How to Run Game Arena (Using JAR File)

### 1. Download the JAR
- [Click here to download GameHubApp.jar](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/raw/main/application/Game-Hub.jar)

### 2. Install Java (if not already installed)
- You need Java 8 or higher (JRE or JDK).
- Download from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/).

### 3. Run the Game
- **Option 1: Double-click** the `Game-Hub.jar` file.
- **Option 2: Use the command line:**
  ```sh
  java -jar GameHubApp.jar
  ```

### 4. Enjoy!
- No need to download any other files or source code. The JAR contains everything needed to play.
  
---

## 🖥️ How to Run Game Arena (Using Exe File)

### 1. Download the EXE
- [Click here to download GameHubApp.exe](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/raw/main/application/Game-Hub.exe)

### 2. Run the Game
- **Double-click** the `Game-Hub.exe` file.

### 3. Enjoy!
- No need to download any other files or source code. The EXE contains everything needed to play.

---

## 🛠️ Build from Source (For Developers)

Want to explore or modify the code?

1. Make sure you have Java 8+ installed.
2. Compile:
   ```sh
   javac GamePlatform.java
   ```
3. Run:
   ```sh
   java GamePlatform
   ```

---

## 📝 Troubleshooting

- If double-clicking the JAR doesn't work, use the command line method above.
- If you get an error like "Java not recognized," install Java and try again.
- If the game doesn’t launch, make sure you downloaded the full JAR and not a partial file.
- If Windows Defender or any other Defender shows a warning, click "More info" then "Run anyway".
- This is normal for unsigned executables from GitHub. (Rest-Assured the file is 100% safe).

---

## 📁 Repository Structure

```
Game-Arena-Board-Verse-GameHub/
├── application/
│   │── Game-Hub.jar
│   └── Game-Hub.exe
├── java-gui/
│   ├── GamePlatform.java
│   └── images/
│       ├── lifeline.png
│       ├── main-menu.PNG
│       ├── othello-gameplay.PNG
│       ├── othello.PNG
│       ├── sudoku-gameplay.PNG
│       ├── sudoku-lifeline-finished.PNG
│       ├── sudoku.PNG
│       ├── tictactoe-2x2-toss.PNG
│       ├── tictactoe-3x3.PNG
│       └── tictactoe.PNG
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🧩 Project Architecture & Highlights

### Main Platform
- **GamePlatform.java**: The heart of the app. Manages navigation, sound, statistics, and game switching with a modern CardLayout-based UI.
- **GameHub**: The main menu, featuring animated game cards, sound toggle, statistics, and quick access to all games.

### Game Modules
- **TicTacToeGame**: 
  - Supports 2×2 to 10×10 boards.
  - Unique 2×2 “toss” system for fairness.
  - Undo/redo, player customization, and dynamic board resizing.
  - Modern, animated board with color-coded symbols and sound feedback.
- **OthelloGame**: 
  - Classic 8×8 gameplay with beautiful wooden board and glossy pieces.
  - Valid move highlighting, real-time scoring, and player name customization.
  - Timer, new game controls, and endgame dialog.
  - Piece flipping with sound and animation.
- **SudokuGame**: 
  - Four difficulty levels, lifeline (heart) system, and real-time input validation.
  - Custom number pad, hints, and solution reveal.
  - Unique puzzle generation with solution uniqueness check.
  - Visual and sound feedback for correct/incorrect moves.

### UI/UX
- **Custom Buttons**: All buttons are custom-painted for a modern, tactile feel.
- **Sound Effects**: Soothing, musical tones for actions, moves, wins, errors, and more.
- **Statistics**: Tracks games played, wins, and playtime for each game, with reset options.
- **Accessibility**: Large fonts, color contrast, and responsive layouts for all ages.

### Code Quality
- **Modular OOP Design**: Each game is a self-contained class, making the codebase easy to extend.
- **Resource Management**: Images and sounds are bundled for cross-platform compatibility.
- **No External Dependencies**: 100% Java Standard Library.

---

## 🤝 Contributing

Contributions are welcome! If you have ideas, improvements, or bug fixes, feel free to submit a pull request for review.

---

## 🏆 About

Created by **Madhav Agarwal**.  
Inspired by classic games, built for modern fun and learning.

If you have any questions or need further assistance, feel free to reach out:

- GitHub: [madhavagarwal3012](https://github.com/madhavagarwal3012)
- Linktree: [madhavagarwal3012](https://linktr.ee/madhavagarwal3012)

---

## 📜 License

This project is licensed under the MIT License. See [LICENSE](https://github.com/madhavagarwal3012/Game-Arena-Board-Verse-GameHub/blob/main/LICENSE) for details.

---

*Enjoy playing! If you like it, give a ⭐ on GitHub and share with friends!* 
[![Star History Chart](https://api.star-history.com/svg?repos=madhavagarwal3012/Game-Arena-Board-Verse-GameHub&type=Date)](https://www.star-history.com/#madhavagarwal3012/Game-Arena-Board-Verse-GameHub&Date)
