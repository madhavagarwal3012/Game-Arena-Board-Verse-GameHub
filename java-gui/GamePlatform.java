import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;
import javax.imageio.ImageIO;

public class GamePlatform extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameHub gameHub;
    private TicTacToeGame ticTacToeGame;
    private OthelloGame othelloGame;
    private SudokuGame sudokuGame;
    
    // Game Statistics
    private Map<String, GameStats> gameStatistics;
    
    // Sound Effects (simple beep sounds)
    private boolean soundEnabled = true;
    
    public GamePlatform() {
        gameStatistics = new HashMap<>();
        gameStatistics.put("TicTacToe", new GameStats());
        gameStatistics.put("Othello", new GameStats());
        gameStatistics.put("Sudoku", new GameStats());
        
        setTitle("Game Arena - Ultimate Gaming Hub");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        gameHub = new GameHub(this);
        ticTacToeGame = new TicTacToeGame(this);
        othelloGame = new OthelloGame(this);
        sudokuGame = new SudokuGame(this);
        
        mainPanel.add(gameHub, "HUB");
        mainPanel.add(ticTacToeGame, "TICTACTOE");
        mainPanel.add(othelloGame, "OTHELLO");
        mainPanel.add(sudokuGame, "SUDOKU");
        
        add(mainPanel);
        showHub();
    }
    
    public void showGame(String gameName) {
        cardLayout.show(mainPanel, gameName.toUpperCase());
        playSound("menu");
    }
    
    public void showHub() {
        cardLayout.show(mainPanel, "HUB");
        gameHub.updateStats();
    }
    
    // Sound Effects
    public void playSound(String soundType) {
        if (!soundEnabled) return;
        
        try {
            // Generate soothing musical tones instead of harsh beeps
            int frequency = 440; // Default A4 note
            int duration = 150;
            double volume = 0.25; // Even softer volume for more soothing experience
            
            switch (soundType) {
                case "click":
                    frequency = 523; // C5 note - pleasant click
                    duration = 60;
                    volume = 0.2;
                    break;
                case "win":
                    // Play a beautiful victory chord (C-E-G-C)
                    playChord(new int[]{523, 659, 784, 1047}, 400);
                    return;
                case "move":
                    frequency = 587; // D5 note - smooth move sound
                    duration = 80;
                    volume = 0.15;
                    break;
                case "othello_move":
                    // Special soothing sound for Othello piece placement
                    frequency = 659; // E5 note - gentle piece sound
                    duration = 120;
                    volume = 0.18;
                    break;
                case "othello_flip":
                    // Soft sound for piece flipping
                    frequency = 698; // F5 note - gentle flip sound
                    duration = 100;
                    volume = 0.12;
                    break;
                case "menu":
                    frequency = 440; // A4 note - gentle menu sound
                    duration = 100;
                    volume = 0.15;
                    break;
                case "error":
                    frequency = 349; // F4 note - gentle error sound
                    duration = 150;
                    volume = 0.1;
                    break;
                case "reset":
                    // Soft reset sound
                    frequency = 392; // G4 note - gentle reset
                    duration = 80;
                    volume = 0.12;
                    break;
                case "game_start":
                    // Pleasant game start sound
                    playChord(new int[]{440, 554, 659}, 200);
                    return;
            }
            
            // Create a soothing sine wave tone with enhanced smoothness
            byte[] audioData = generateSmoothTone(frequency, duration, volume);
            AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(audioData),
                new AudioFormat(44100, 16, 1, true, false),
                audioData.length / 2
            );
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            
        } catch (Exception e) {
            // Silently fail if sound can't be played
        }
    }
    
    private void playChord(int[] frequencies, int duration) {
        try {
            // Mix multiple frequencies for a chord
            byte[] mixedAudio = new byte[duration * 44]; // 44.1kHz sample rate
            
            for (int i = 0; i < mixedAudio.length; i += 2) {
                double sample = 0;
                for (int freq : frequencies) {
                    sample += Math.sin(2.0 * Math.PI * freq * i / 44100.0);
                }
                sample = sample / frequencies.length * 0.2; // Normalize and reduce volume
                
                short sampleValue = (short) (sample * 16383); // Convert to 16-bit
                mixedAudio[i] = (byte) (sampleValue & 0xFF);
                mixedAudio[i + 1] = (byte) ((sampleValue >> 8) & 0xFF);
            }
            
            AudioInputStream audioStream = new AudioInputStream(
                new ByteArrayInputStream(mixedAudio),
                new AudioFormat(44100, 16, 1, true, false),
                mixedAudio.length / 2
            );
            
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            
        } catch (Exception e) {
            // Silently fail if sound can't be played
        }
    }
    
    private byte[] generateSmoothTone(int frequency, int duration, double volume) {
        int sampleRate = 44100;
        int numSamples = duration * sampleRate / 1000;
        byte[] audioData = new byte[numSamples * 2]; // 16-bit audio
        
        for (int i = 0; i < numSamples; i++) {
            // Create a smooth sine wave with enhanced fade in/out
            double fadeIn = Math.min(1.0, i / (sampleRate * 0.015)); // 15ms fade in
            double fadeOut = Math.min(1.0, (numSamples - i) / (sampleRate * 0.015)); // 15ms fade out
            double fade = Math.min(fadeIn, fadeOut);
            
            // Add subtle harmonics for richer, more soothing sound
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            double fundamental = Math.sin(angle);
            double harmonic1 = Math.sin(2.0 * angle) * 0.1; // Subtle second harmonic
            double harmonic2 = Math.sin(3.0 * angle) * 0.05; // Very subtle third harmonic
            
            double sample = (fundamental + harmonic1 + harmonic2) * volume * fade;
            
            // Apply gentle compression for smoother sound
            sample = Math.tanh(sample * 0.8) * 1.2;
            
            short sampleValue = (short) (sample * 16383); // Convert to 16-bit
            audioData[i * 2] = (byte) (sampleValue & 0xFF);
            audioData[i * 2 + 1] = (byte) ((sampleValue >> 8) & 0xFF);
        }
        
        return audioData;
    }
    
    private byte[] generateBeep(int frequency, int duration) {
        // Keep this for backward compatibility, but it's not used anymore
        int sampleRate = 44100;
        int numSamples = duration * sampleRate / 1000;
        byte[] audioData = new byte[numSamples];
        
        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * frequency * i / sampleRate;
            audioData[i] = (byte) (Math.sin(angle) * 127);
        }
        
        return audioData;
    }
    
    public void toggleSound() {
        soundEnabled = !soundEnabled;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    // Statistics Management
    public void recordGameResult(String gameName, boolean won, long duration) {
        GameStats stats = gameStatistics.get(gameName);
        if (stats != null) {
            stats.gamesPlayed++;
            if (won) stats.gamesWon++;
            stats.totalPlayTime += duration;
            stats.updateWinRate();
        }
    }
    
    public GameStats getGameStats(String gameName) {
        return gameStatistics.get(gameName);
    }
    
    public Map<String, GameStats> getAllStats() {
        return gameStatistics;
    }
    
    // Reset all game statistics
    public void resetAllStatistics() {
        for (GameStats stats : gameStatistics.values()) {
            stats.gamesPlayed = 0;
            stats.gamesWon = 0;
            stats.totalPlayTime = 0;
            stats.updateWinRate();
        }
        playSound("reset");
    }
    
    // Reset statistics for a specific game
    public void resetGameStatistics(String gameName) {
        GameStats stats = gameStatistics.get(gameName);
        if (stats != null) {
            stats.gamesPlayed = 0;
            stats.gamesWon = 0;
            stats.totalPlayTime = 0;
            stats.updateWinRate();
            playSound("reset");
        }
    }
    
    // Shared styled button creation method
    public JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color baseColor = color;
                Color hoverColor = color;
                // Use a lighter or darker shade for the sound button on hover
                if (text.contains("Sound")) {
                    hoverColor = new Color(34, 211, 238); // Lighter teal for hover
                } else {
                    hoverColor = color.brighter();
                }
                if (getModel().isPressed()) {
                    g2d.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(hoverColor);
                } else {
                    g2d.setColor(baseColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GamePlatform().setVisible(true);
        });
    }
}

class GameHub extends JPanel {
    private GamePlatform parent;
    private JPanel statsPanel;
    private JButton soundToggleButton;
    
    public GameHub(GamePlatform parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Header with title and controls
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("GAME ARENA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Sound toggle button
        // Use a distinct, visually appealing color for the sound button (e.g., soft teal)
        Color soundButtonColor = new Color(56, 189, 248); // Soft blue/teal
        soundToggleButton = parent.createStyledButton(parent.isSoundEnabled() ? "Sound On" : "Sound Off", soundButtonColor);
        soundToggleButton.setForeground(Color.WHITE);
        soundToggleButton.setPreferredSize(new Dimension(120, 40));
        soundToggleButton.addActionListener(e -> {
            parent.toggleSound();
            soundToggleButton.setText(parent.isSoundEnabled() ? "Sound On" : "Sound Off");
            parent.playSound("click");
        });
        
        // Reset statistics button
        JButton resetStatsButton = parent.createStyledButton("Reset Stats", new Color(100, 116, 139));
        resetStatsButton.setPreferredSize(new Dimension(120, 40));
        resetStatsButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset all game statistics?\nThis action cannot be undone.",
                "Reset Statistics",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                parent.resetAllStatistics();
                updateStats();
                parent.playSound("click");
            }
        });
        resetStatsButton.setForeground(Color.WHITE);
        // Restart Game button
        JButton restartButton = parent.createStyledButton("Restart Game", new Color(59, 130, 246));
        restartButton.setPreferredSize(new Dimension(140, 40));
        restartButton.addActionListener(e -> {
            try {
                String javaBin = System.getProperty("java.home") + "/bin/java";
                String mainClass = GamePlatform.class.getName();
                String classPath = System.getProperty("java.class.path");
                String jarPath = new java.io.File(GamePlatform.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
                java.util.List<String> command = new java.util.ArrayList<>();
                if (jarPath.endsWith(".jar")) {
                    command.add(javaBin);
                    command.add("-jar");
                    command.add(jarPath);
                } else {
                    command.add(javaBin);
                    command.add("-cp");
                    command.add(classPath);
                    command.add(mainClass);
                }
                new ProcessBuilder(command).start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });
        restartButton.setForeground(Color.WHITE);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(restartButton);
        rightPanel.add(resetStatsButton);
        rightPanel.add(soundToggleButton);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Main content area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(248, 250, 252));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Games grid
        JPanel gamesPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        gamesPanel.setOpaque(false);
        
        gamesPanel.add(createGameCard(
            "Tic-Tac-Toe", 
            "Classic strategy game with 2×2 to 10×10 board sizes", 
            "• Multiple board sizes (2×2 to 10×10)<br>• Unique toss feature for 2×2 games<br>• Player customization<br>• Score tracking<br>• Strategic gameplay",
            new Color(239, 68, 68),
            () -> {
                parent.playSound("click");
                parent.showGame("TicTacToe");
            },
            "images/tictactoe.PNG"
        ));
        
        gamesPanel.add(createGameCard(
            "Othello", 
            "Strategic board game with beautiful wooden pieces", 
            "• Classic Othello rules<br>• Vibrant glossy pieces<br>• Wooden border design<br>• Player vs Player",
            new Color(16, 185, 129),
            () -> {
                parent.playSound("click");
                parent.showGame("Othello");
            },
            "images/othello.PNG"
        ));
        
        gamesPanel.add(createGameCard(
            "Sudoku", 
            "Number puzzle with 4 difficulty levels", 
            "• 4 difficulty levels<br>• Valid puzzle generation<br>• Timer and hints<br>• Solution checking",
            new Color(168, 85, 247),
            () -> {
                parent.playSound("click");
                parent.showGame("Sudoku");
            },
            "images/sudoku.PNG"
        ));
        
        // Statistics Panel
        statsPanel = createStatsPanel();
        
        contentPanel.add(gamesPanel, BorderLayout.CENTER);
        contentPanel.add(statsPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        // Add copyright label at the bottom of the main menu
        JLabel copyrightLabel = new JLabel("© 2025 Madhav Agarwal. All rights reserved.");
        copyrightLabel.setFont(new Font("Arial", Font.BOLD, 15));
        copyrightLabel.setForeground(new Color(100, 116, 139));
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(copyrightLabel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("GAME STATISTICS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(30, 41, 59));
        
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setOpaque(false);
        
        statsGrid.add(createStatCard("Tic-Tac-Toe", parent.getGameStats("TicTacToe")));
        statsGrid.add(createStatCard("Othello", parent.getGameStats("Othello")));
        statsGrid.add(createStatCard("Sudoku", parent.getGameStats("Sudoku")));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(statsGrid, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatCard(String gameName, GameStats unusedStats) {
        // Always fetch the latest stats
        GameStats stats = parent.getGameStats(gameName.equals("Tic-Tac-Toe") ? "TicTacToe" : gameName);
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(248, 250, 252));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.setColor(new Color(226, 232, 240));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        // Game name
        JLabel nameLabel = new JLabel(gameName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(new Color(30, 41, 59));
        // Statistics
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        JLabel gamesLabel = new JLabel("Games: " + stats.gamesPlayed);
        gamesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gamesLabel.setForeground(new Color(100, 116, 139));
        JLabel winsLabel = new JLabel("Wins: " + stats.gamesWon);
        winsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        winsLabel.setForeground(new Color(100, 116, 139));
        JLabel timeLabel = new JLabel("Time: " + stats.getFormattedPlayTime());
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(100, 116, 139));
        statsPanel.add(gamesLabel);
        statsPanel.add(winsLabel);
        statsPanel.add(timeLabel);
        // Individual reset button for this game
        JButton resetButton = parent.createStyledButton("Reset", new Color(100, 116, 139));
        resetButton.setPreferredSize(new Dimension(110, 38));
        resetButton.setFont(new Font("Arial", Font.BOLD, 15));
        resetButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Reset statistics for " + gameName + "?\nThis action cannot be undone.",
                "Reset " + gameName + " Statistics",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                parent.resetGameStatistics(gameName.equals("Tic-Tac-Toe") ? "TicTacToe" : gameName);
                updateStats();
                parent.playSound("click");
            }
        });
        resetButton.setForeground(Color.WHITE);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(resetButton);
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(statsPanel, BorderLayout.CENTER);
        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }
    
    public void updateStats() {
        Container parentContainer = statsPanel.getParent();
        if (parentContainer != null) {
            parentContainer.remove(statsPanel);
        }
        statsPanel = createStatsPanel();
        if (parentContainer != null) {
            parentContainer.add(statsPanel, BorderLayout.SOUTH);
        }
        revalidate();
        repaint();
    }
    
    private JPanel createGameCard(String title, String description, String features, Color accentColor, Runnable onClick, String imagePath) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.WHITE,
                    0, getHeight(), new Color(248, 250, 252)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Border
                g2d.setColor(new Color(226, 232, 240));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                // Accent line at top
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawLine(0, 0, getWidth(), 0);
                g2d.dispose();
            }
        };
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
                card.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                card.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
        // Image
        JLabel imageLabel = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                // Always use the exact case for resource path
                String resourcePath = "/" + imagePath.replace("\\", "/");
                java.net.URL imgUrl = getClass().getResource(resourcePath);
                if (imgUrl != null) {
                    ImageIcon icon = new ImageIcon(imgUrl);
                    if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
                        Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                        imageLabel = new JLabel(new ImageIcon(img));
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                    }
                }
                // No fallback to direct file path; always use resources in JAR
            } catch (Exception ex) {
                // Do not add imageLabel if image fails to load
            }
        }
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Description
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(new Color(100, 116, 139));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Features
        JLabel featuresLabel = new JLabel("<html><div style='text-align: left;'>" + features + "</div></html>");
        featuresLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        featuresLabel.setForeground(new Color(148, 163, 184));
        // Play button
        JButton playButton = new JButton("PLAY NOW") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(accentColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(accentColor.brighter());
                } else {
                    g2d.setColor(accentColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
                g2d.dispose();
            }
        };
        playButton.setFont(new Font("Arial", Font.BOLD, 16));
        playButton.setPreferredSize(new Dimension(200, 45));
        playButton.setBorderPainted(false);
        playButton.setContentAreaFilled(false);
        playButton.setFocusPainted(false);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.addActionListener(e -> onClick.run());
        // Layout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (imageLabel != null) topPanel.add(imageLabel);
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(descLabel);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(featuresLabel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(playButton);
        card.add(topPanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);
        return card;
    }
}

class TicTacToeGame extends JPanel {
    private GamePlatform parent;
    private String[][] board;
    private String currentPlayer = "X";
    private boolean gameOver = false;
    private int boardSize = 3;
    private JLabel statusLabel;
    private JLabel player1Label, player2Label;
    private int player1Score = 0, player2Score = 0;
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private String player1Symbol = "X";
    private String player2Symbol = "O";
    private JTextField name1Field, name2Field, symbol1Field, symbol2Field;
    private JSpinner boardSizeSpinner;
    private JPanel setupPanel;
    private JPanel gamePanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    // Undo/Redo functionality
    private Stack<Move> moveHistory = new Stack<>();
    private Stack<Move> redoStack = new Stack<>();
    private long gameStartTime;
    // Reference to the custom board panel
    private ModernTicTacToeBoard boardPanel;
    
    // Move class for undo/redo
    private static class Move {
        int row, col;
        String player;
        String previousValue;
        
        Move(int row, int col, String player, String previousValue) {
            this.row = row;
            this.col = col;
            this.player = player;
            this.previousValue = previousValue;
        }
    }
    
    // At the top of TicTacToeGame class, add:
    private boolean tossReadyForMove = false;
    // Add this as a field in TicTacToeGame:
    private JButton[] tossButton = new JButton[1];
    // Add this field to track previous starting player for 2x2
    private String previousStartingPlayer2x2 = null;
    
    public TicTacToeGame(GamePlatform parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        setupPanel = createPlayerSetupPanel();
        gamePanel = createGamePanel();
        contentPanel.add(setupPanel, "SETUP");
        contentPanel.add(gamePanel, "GAME");
        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "SETUP");
    }
    
    private JPanel createPlayerSetupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Header with title and back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Back to Games button
        JButton backButton = parent.createStyledButton("← Back to Games", new Color(100, 116, 139));
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(e -> {
            parent.playSound("click");
            parent.showHub();
        });
        backButton.setForeground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("PLAYER SETUP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Setup form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 30, 20));
        formPanel.setBackground(new Color(248, 250, 252));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Player 1 setup
        JPanel player1Panel = new JPanel(new BorderLayout());
        player1Panel.setBackground(Color.WHITE);
        player1Panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(239, 68, 68), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel player1Title = new JLabel("Player 1 (X)");
        player1Title.setFont(new Font("Arial", Font.BOLD, 18));
        player1Title.setForeground(new Color(30, 41, 59));
        player1Title.setHorizontalAlignment(SwingConstants.CENTER);
        
        name1Field = new JTextField(player1Name);
        name1Field.setFont(new Font("Arial", Font.PLAIN, 16));
        name1Field.setBorder(BorderFactory.createTitledBorder("Name"));
        
        symbol1Field = new JTextField(player1Symbol);
        symbol1Field.setFont(new Font("Arial", Font.PLAIN, 16));
        symbol1Field.setBorder(BorderFactory.createTitledBorder("Symbol"));
        
        JPanel player1Fields = new JPanel(new GridLayout(2, 1, 10, 10));
        player1Fields.setOpaque(false);
        player1Fields.add(name1Field);
        player1Fields.add(symbol1Field);
        
        player1Panel.add(player1Title, BorderLayout.NORTH);
        player1Panel.add(player1Fields, BorderLayout.CENTER);
        
        // Player 2 setup
        JPanel player2Panel = new JPanel(new BorderLayout());
        player2Panel.setBackground(Color.WHITE);
        player2Panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(16, 185, 129), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel player2Title = new JLabel("Player 2 (O)");
        player2Title.setFont(new Font("Arial", Font.BOLD, 18));
        player2Title.setForeground(new Color(30, 41, 59));
        player2Title.setHorizontalAlignment(SwingConstants.CENTER);
        
        name2Field = new JTextField(player2Name);
        name2Field.setFont(new Font("Arial", Font.PLAIN, 16));
        name2Field.setBorder(BorderFactory.createTitledBorder("Name"));
        
        symbol2Field = new JTextField(player2Symbol);
        symbol2Field.setFont(new Font("Arial", Font.PLAIN, 16));
        symbol2Field.setBorder(BorderFactory.createTitledBorder("Symbol"));
        
        JPanel player2Fields = new JPanel(new GridLayout(2, 1, 10, 10));
        player2Fields.setOpaque(false);
        player2Fields.add(name2Field);
        player2Fields.add(symbol2Field);
        
        player2Panel.add(player2Title, BorderLayout.NORTH);
        player2Panel.add(player2Fields, BorderLayout.CENTER);
        
        // Board size setup
        JPanel boardSizePanel = new JPanel(new BorderLayout());
        boardSizePanel.setBackground(Color.WHITE);
        boardSizePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(59, 130, 246), 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel boardSizeTitle = new JLabel("Board Size");
        boardSizeTitle.setFont(new Font("Arial", Font.BOLD, 18));
        boardSizeTitle.setForeground(new Color(30, 41, 59));
        boardSizeTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(2, 2, 10, 1);
        boardSizeSpinner = new JSpinner(spinnerModel);
        boardSizeSpinner.setFont(new Font("Arial", Font.PLAIN, 16));
        JComponent editor = boardSizeSpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        JLabel boardSizeLabel = new JLabel("Grid Size (2×2 to 10×10)");
        boardSizeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        boardSizeLabel.setForeground(new Color(100, 116, 139));
        boardSizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel boardSizeFields = new JPanel(new GridLayout(2, 1, 10, 10));
        boardSizeFields.setOpaque(false);
        boardSizeFields.add(boardSizeSpinner);
        boardSizeFields.add(boardSizeLabel);
        
        boardSizePanel.add(boardSizeTitle, BorderLayout.NORTH);
        boardSizePanel.add(boardSizeFields, BorderLayout.CENTER);
        
        formPanel.add(player1Panel);
        formPanel.add(player2Panel);
        formPanel.add(boardSizePanel);
        
        // Start button
        JButton startButton = parent.createStyledButton("START GAME", new Color(59, 130, 246));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> {
            player1Name = name1Field.getText().trim().isEmpty() ? "Player 1" : name1Field.getText().trim();
            player2Name = name2Field.getText().trim().isEmpty() ? "Player 2" : name2Field.getText().trim();
            player1Symbol = symbol1Field.getText().trim().isEmpty() ? "X" : symbol1Field.getText().trim().substring(0, 1);
            player2Symbol = symbol2Field.getText().trim().isEmpty() ? "O" : symbol2Field.getText().trim().substring(0, 1);
            boardSize = (Integer) boardSizeSpinner.getValue();
            if (player1Symbol.equals(player2Symbol)) {
                JOptionPane.showMessageDialog(this, "Players must have different symbols!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            parent.playSound("click");
            // Remove old gamePanel if it exists
            contentPanel.remove(gamePanel);
            // Recreate gamePanel with new settings
            gamePanel = createGamePanel();
            contentPanel.add(gamePanel, "GAME");
            startNewGame();
            cardLayout.show(contentPanel, "GAME");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGamePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 250, 252));
        // Header components
        JButton backButtonGame = parent.createStyledButton("← Back to Games", new Color(100, 116, 139));
        backButtonGame.addActionListener(e -> {
            parent.playSound("click");
            parent.showHub();
        });
        backButtonGame.setForeground(Color.WHITE);
        JLabel titleLabel = new JLabel("TIC-TAC-TOE ARENA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28)); // Slightly smaller font
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false);
        JButton undoButton = parent.createStyledButton("Undo", new Color(59, 130, 246));
        undoButton.addActionListener(e -> undoMove());
        undoButton.setForeground(Color.WHITE);
        JButton redoButton = parent.createStyledButton("Redo", new Color(16, 185, 129));
        redoButton.addActionListener(e -> redoMove());
        redoButton.setForeground(Color.WHITE);
        JButton resetButton = parent.createStyledButton("Reset", new Color(239, 68, 68));
        resetButton.addActionListener(e -> resetGame());
        resetButton.setForeground(Color.WHITE);
        JButton changeGridButton = parent.createStyledButton("Change Board", new Color(100, 116, 139));
        changeGridButton.addActionListener(e -> {
            parent.playSound("click");
            cardLayout.show(contentPanel, "SETUP");
        });
        changeGridButton.setForeground(Color.WHITE);
        controlPanel.add(undoButton);
        controlPanel.add(redoButton);
        controlPanel.add(resetButton);
        controlPanel.add(changeGridButton);
        // Header panel with GridBagLayout
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Reduce insets
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 4, 0, 4); // Reduce space between components
        gbc.gridy = 0;
        // Back button
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        headerPanel.add(backButtonGame, gbc);
        // Title label
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        headerPanel.add(titleLabel, gbc);
        // Control panel
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        headerPanel.add(controlPanel, gbc);
        // Players Panel
        JPanel playersPanel = new JPanel(new BorderLayout());
        playersPanel.setOpaque(false); // Make panel transparent
        playersPanel.setBorder(null); // Remove border
        player1Label = createPlayerCard(player1Name + " (" + player1Symbol + ")", player1Score, new Color(239, 68, 68));
        player2Label = createPlayerCard(player2Name + " (" + player2Symbol + ")", player2Score, new Color(16, 185, 129));
        playersPanel.add(player1Label, BorderLayout.WEST);
        playersPanel.add(player2Label, BorderLayout.EAST);
        // Game Panel
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(248, 250, 252));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setForeground(new Color(30, 41, 59));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        if (boardSize == 2) {
            tossButton[0] = parent.createStyledButton("Toss", new Color(168, 85, 247));
            tossButton[0].setForeground(Color.WHITE);
            tossButton[0].setAlignmentX(Component.CENTER_ALIGNMENT);
            tossButton[0].addActionListener(e -> {
                // --- Combined cryptographically secure and existing randomness for toss ---
                java.security.SecureRandom secureRandom = new java.security.SecureRandom();
                long nano = System.nanoTime();
                double rand1 = Math.random();
                double rand2 = new java.util.Random().nextDouble();
                int uuidHash = java.util.UUID.randomUUID().hashCode();
                int secureToss = secureRandom.nextInt(2);
                int combined = (int) ((nano ^ uuidHash) + (rand1 * 100000) - (rand2 * 100000) + secureToss);
                int toss = Math.abs(combined) % 2;
                boolean secureFlip = secureRandom.nextBoolean();
                boolean flip = ((Math.abs(uuidHash + nano) % 2 == 0) ^ secureFlip); // XOR for extra unpredictability
                String tossWinner, actualTurn;
                if (toss == 0) {
                    tossWinner = player1Name + " (" + player1Symbol + ")";
                    actualTurn = flip ? player2Symbol : player1Symbol;
                } else {
                    tossWinner = player2Name + " (" + player2Symbol + ")";
                    actualTurn = flip ? player1Symbol : player2Symbol;
                }
                currentPlayer = actualTurn;
                String shownWinner = (actualTurn.equals(player1Symbol)) ? player1Name + " (" + player1Symbol + ")" : player2Name + " (" + player2Symbol + ")";
                statusLabel.setText(shownWinner + "'s Turn (Toss Won)");
                tossButton[0].setVisible(false);
                statusLabel.setVisible(true);
                tossReadyForMove = true;
            });
            statusPanel.add(Box.createVerticalStrut(10));
            statusPanel.add(tossButton[0]);
            statusPanel.add(Box.createVerticalStrut(10));
            // --- Center statusLabel in a panel ---
            JPanel statusLabelPanel = new JPanel(new BorderLayout());
            statusLabelPanel.setOpaque(false);
            statusLabelPanel.add(statusLabel, BorderLayout.CENTER);
            statusPanel.add(statusLabelPanel);
            // --- End center statusLabel ---
            statusLabel.setVisible(false);
            tossButton[0].setVisible(true);
        } else {
            // --- Center statusLabel in a panel for other board sizes too ---
            JPanel statusLabelPanel = new JPanel(new BorderLayout());
            statusLabelPanel.setOpaque(false);
            statusLabelPanel.add(statusLabel, BorderLayout.CENTER);
            statusPanel.add(statusLabelPanel);
            // --- End center statusLabel ---
        }
        JPanel boardPanel = createGameBoard();
        gamePanel.add(statusPanel, BorderLayout.NORTH);
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(playersPanel, BorderLayout.CENTER);
        mainPanel.add(gamePanel, BorderLayout.SOUTH);
        return mainPanel;
    }
    
    private void startNewGame() {
        gameStartTime = System.currentTimeMillis();
        moveHistory.clear();
        redoStack.clear();
        gameOver = false;
        // --- For 2x2, randomize initial state to avoid previous pattern ---
        if (boardSize == 2) {
            tossReadyForMove = false;
            if (statusLabel != null) statusLabel.setVisible(false);
            java.security.SecureRandom secureRandom = new java.security.SecureRandom();
            String randomStart = secureRandom.nextBoolean() ? player1Symbol : player2Symbol;
            // Flip if same as previous
            if (previousStartingPlayer2x2 != null && randomStart.equals(previousStartingPlayer2x2)) {
                randomStart = randomStart.equals(player1Symbol) ? player2Symbol : player1Symbol;
            }
            currentPlayer = randomStart;
            previousStartingPlayer2x2 = currentPlayer;
        } else {
        currentPlayer = player1Symbol;
        statusLabel.setText(player1Name + " (" + player1Symbol + ")'s Turn");
        statusLabel.setForeground(new Color(30, 41, 59));
        }
        // Initialize board array
        board = new String[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = "";
            }
        }
        updatePlayerCards();
        if (gamePanel != null) gamePanel.repaint();
    }
    
    private void undoMove() {
        if (moveHistory.isEmpty()) {
            parent.playSound("error");
            return;
        }
        
        parent.playSound("click");
        Move lastMove = moveHistory.pop();
        redoStack.push(lastMove);
        
        board[lastMove.row][lastMove.col] = lastMove.previousValue;
        currentPlayer = lastMove.player;
        // After undo, recalculate game state
        gameOver = false;
        // Check if the board is in a win/draw state after undo
        boolean foundWin = false;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (!board[i][j].isEmpty() && checkWinner(i, j)) {
                    foundWin = true;
                    break;
                }
            }
            if (foundWin) break;
        }
        if (foundWin) {
            gameOver = true;
            // Show winner based on who had the last move
            if (lastMove.player.equals(player1Symbol)) {
                statusLabel.setText("🎉 " + player1Name + " (" + player1Symbol + ") Wins!");
            } else {
                statusLabel.setText("🎉 " + player2Name + " (" + player2Symbol + ") Wins!");
            }
        } else if (isBoardFull()) {
            gameOver = true;
            statusLabel.setText("🤝 It's a Draw!");
        } else {
            // Use actual player names in status label
            String playerName = currentPlayer.equals(player1Symbol) ? player1Name : player2Name;
            statusLabel.setText(playerName + " (" + currentPlayer + ")'s Turn");
        }
        statusLabel.setForeground(new Color(30, 41, 59));
        updatePlayerCards();
        if (this.boardPanel != null) this.boardPanel.repaint();
        if (boardSize == 2) {
            statusLabel.setVisible(false);
            if (tossButton[0] != null) tossButton[0].setVisible(true);
            tossReadyForMove = false;
            if (gameOver) {
                statusLabel.setVisible(true);
                if (tossButton[0] != null) tossButton[0].setVisible(false);
            }
            if (this.boardPanel != null) this.boardPanel.repaint();
            return;
        }
    }
    
    private void redoMove() {
        if (redoStack.isEmpty()) {
            parent.playSound("error");
            return;
        }
        
        parent.playSound("click");
        Move move = redoStack.pop();
        moveHistory.push(move);
        
        board[move.row][move.col] = move.player;
        // Set current player to the next player after the redo move
        currentPlayer = move.player.equals(player1Symbol) ? player2Symbol : player1Symbol;
        // After redo, recalculate game state
        gameOver = false;
        if (checkWinner(move.row, move.col)) {
            gameOver = true;
            if (move.player.equals(player1Symbol)) {
                player1Score++;
                statusLabel.setText("🎉 " + player1Name + " (" + player1Symbol + ") Wins!");
            } else {
                player2Score++;
                statusLabel.setText("🎉 " + player2Name + " (" + player2Symbol + ") Wins!");
            }
            parent.playSound("win");
            updatePlayerCards();
            recordGameResult();
        } else if (isBoardFull()) {
            gameOver = true;
            statusLabel.setText("🤝 It's a Draw!");
            recordGameResult();
        } else {
            // Use actual player names in status label
            String playerName = currentPlayer.equals(player1Symbol) ? player1Name : player2Name;
            statusLabel.setText(playerName + " (" + currentPlayer + ")'s Turn");
        }
        if (this.boardPanel != null) this.boardPanel.repaint();
        if (boardSize == 2) {
            statusLabel.setVisible(false);
            if (tossButton[0] != null) tossButton[0].setVisible(true);
            tossReadyForMove = false;
            if (gameOver) {
                statusLabel.setVisible(true);
                if (tossButton[0] != null) tossButton[0].setVisible(false);
            }
            if (this.boardPanel != null) this.boardPanel.repaint();
            return;
        }
    }
    
    // Modern custom board for Tic-Tac-Toe
    private JPanel createGameBoard() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(248, 250, 252));
        this.boardPanel = new ModernTicTacToeBoard();
        this.boardPanel.setPreferredSize(new Dimension(480, 480));
        container.add(this.boardPanel);
        return container;
    }

    // Custom JPanel for modern Tic-Tac-Toe board
    private class ModernTicTacToeBoard extends JPanel {
        public ModernTicTacToeBoard() {
            setBackground(new Color(245, 247, 252));
            setOpaque(true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int w = getWidth();
                    int h = getHeight();
                    int margin = Math.max(16, Math.min(32, Math.max(w, h) / 24));
                    int boardPx = Math.min(w, h) - 2 * margin;
                    int cellSize = boardPx / boardSize;
                    int boardX = (w - boardPx) / 2;
                    int boardY = (h - boardPx) / 2;
                    int x = e.getX();
                    int y = e.getY();
                    int col = (x - boardX) / cellSize;
                    int row = (y - boardY) / cellSize;
                    if (row >= 0 && row < boardSize && col >= 0 && col < boardSize && !gameOver) {
                        makeMove(row, col);
                        repaint();
                    }
                }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Fill the entire panel background first
            g2.setColor(new Color(248, 250, 252));
            g2.fillRect(0, 0, getWidth(), getHeight());
            int w = getWidth();
            int h = getHeight();
            int margin = Math.max(16, Math.min(32, Math.max(w, h) / 24));
            int boardPx = Math.min(w, h) - 2 * margin;
            int cellSize = boardPx / boardSize;
            int boardX = (w - boardPx) / 2;
            int boardY = (h - boardPx) / 2;
            // Board background
            g2.setColor(new Color(245, 247, 252));
            g2.fillRoundRect(boardX-12, boardY-12, boardPx+24, boardPx+24, 48, 48);
            // Perfect, soothing grid lines (single, thin, very light gray)
            g2.setStroke(new BasicStroke(Math.max(1.5f, cellSize/32f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(225, 228, 235, 110));
            for (int i = 1; i < boardSize; i++) {
                int xy = i * cellSize;
                g2.drawLine(boardX + xy, boardY, boardX + xy, boardY + boardPx);
                g2.drawLine(boardX, boardY + xy, boardX + boardPx, boardY + xy);
            }
            // Cells
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    int cx = boardX + j * cellSize;
                    int cy = boardY + i * cellSize;
                    // Cell shadow
                    g2.setColor(new Color(0,0,0,10));
                    g2.fillRoundRect(cx+4, cy+4, cellSize-8, cellSize-8, 24, 24);
                    // Cell border
                    g2.setColor(new Color(230, 233, 240, 60));
                    g2.setStroke(new BasicStroke(Math.max(1, cellSize/40f)));
                    g2.drawRoundRect(cx, cy, cellSize-8, cellSize-8, 24, 24);
                    // Cell fill
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(cx, cy, cellSize-8, cellSize-8, 24, 24);
                    // Draw X/O
                    String val = board != null && board.length > i && board[i].length > j ? board[i][j] : "";
                    if (!val.isEmpty()) {
                        g2.setStroke(new BasicStroke(Math.max(6, cellSize/8), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        if (val.equals(player1Symbol)) {
                            g2.setColor(new Color(239, 68, 68)); // Red for player 1
                        } else {
                            g2.setColor(new Color(16, 185, 129)); // Green for player 2
                        }
                        // Draw the actual symbol text with maximum size to fill the cell
                        int fontSize = Math.max(32, Math.min(cellSize-16, 72)); // Much larger font size
                        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
                        FontMetrics fm = g2.getFontMetrics();
                        
                        // Calculate text position to center it perfectly in the cell
                        int textWidth = fm.stringWidth(val);
                        int textHeight = fm.getAscent();
                        int textX = cx + (cellSize - textWidth) / 2;
                        int textY = cy + (cellSize + textHeight) / 2 - fm.getDescent()/2;
                        
                        // Add a subtle background highlight for better visibility
                        g2.setColor(new Color(255, 255, 255, 200));
                        g2.fillRoundRect(textX-6, textY-textHeight-4, textWidth+12, textHeight+8, 12, 12);
                        
                        // Draw the symbol with enhanced styling
                        if (val.equals(player1Symbol)) {
                            g2.setColor(new Color(239, 68, 68));
                        } else {
                            g2.setColor(new Color(16, 185, 129));
                        }
                        g2.drawString(val, textX, textY);
                    }
                }
            }
            g2.dispose();
        }
    }
    
    private void makeMove(int row, int col) {
        if (gameOver || !board[row][col].isEmpty()) return;
        if (boardSize == 2) {
            if (!tossReadyForMove) return; // Only allow move after toss
            String previousValue = board[row][col];
            board[row][col] = currentPlayer;
            moveHistory.push(new Move(row, col, currentPlayer, previousValue));
            redoStack.clear();
            parent.playSound("move");
            if (checkWinner(row, col)) {
                gameOver = true;
                if (currentPlayer.equals(player1Symbol)) {
                    player1Score++;
                    statusLabel.setText(player1Name + " (" + player1Symbol + ") Wins!");
                } else {
                    player2Score++;
                    statusLabel.setText(player2Name + " (" + player2Symbol + ") Wins!");
                }
                parent.playSound("win");
                updatePlayerCards();
                recordGameResult();
                if (tossButton[0] != null) tossButton[0].setVisible(false);
            } else if (isBoardFull()) {
                gameOver = true;
                statusLabel.setText("It's a Draw!");
                recordGameResult();
                if (tossButton[0] != null) tossButton[0].setVisible(false);
            } else {
                // After move, hide status and show toss button for next turn
                statusLabel.setVisible(false);
                if (tossButton[0] != null) tossButton[0].setVisible(true);
                tossReadyForMove = false;
            }
            if (gamePanel != null) gamePanel.repaint();
            return;
        }
        String previousValue = board[row][col];
        board[row][col] = currentPlayer;
        // Record move for undo/redo
        moveHistory.push(new Move(row, col, currentPlayer, previousValue));
        redoStack.clear(); // Clear redo stack when new move is made
        parent.playSound("move");
        if (checkWinner(row, col)) {
            gameOver = true;
            if (currentPlayer.equals(player1Symbol)) {
                player1Score++;
                statusLabel.setText("🎉 " + player1Name + " (" + player1Symbol + ") Wins!");
            } else {
                player2Score++;
                statusLabel.setText("🎉 " + player2Name + " (" + player2Symbol + ") Wins!");
            }
            parent.playSound("win");
            updatePlayerCards();
            recordGameResult();
        } else if (isBoardFull()) {
            gameOver = true;
            statusLabel.setText("🤝 It's a Draw!");
            recordGameResult();
        } else {
            // Toggle current player after a valid move
            currentPlayer = currentPlayer.equals(player1Symbol) ? player2Symbol : player1Symbol;
            String nextPlayerName = currentPlayer.equals(player1Symbol) ? player1Name : player2Name;
            statusLabel.setText(nextPlayerName + " (" + currentPlayer + ")'s Turn");
        }
        if (gamePanel != null) gamePanel.repaint();
    }
    
    private boolean checkWinner(int row, int col) {
        String symbol = board[row][col];
        
        // Check row
        int count = 1;
        for (int i = col - 1; i >= 0 && board[row][i].equals(symbol); i--) count++;
        for (int i = col + 1; i < boardSize && board[row][i].equals(symbol); i++) count++;
        if (count >= boardSize) return true;
        
        // Check column
        count = 1;
        for (int i = row - 1; i >= 0 && board[i][col].equals(symbol); i--) count++;
        for (int i = row + 1; i < boardSize && board[i][col].equals(symbol); i++) count++;
        if (count >= boardSize) return true;
        
        // Check main diagonal
        count = 1;
        for (int i = 1; row - i >= 0 && col - i >= 0 && board[row - i][col - i].equals(symbol); i++) count++;
        for (int i = 1; row + i < boardSize && col + i < boardSize && board[row + i][col + i].equals(symbol); i++) count++;
        if (count >= boardSize) return true;
        
        // Check anti-diagonal
        count = 1;
        for (int i = 1; row - i >= 0 && col + i < boardSize && board[row - i][col + i].equals(symbol); i++) count++;
        for (int i = 1; row + i < boardSize && col - i >= 0 && board[row + i][col - i].equals(symbol); i++) count++;
        if (count >= boardSize) return true;
        
        return false;
    }
    
    private boolean isBoardFull() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j].isEmpty()) return false;
            }
        }
        return true;
    }
    
    private void resetGame() {
        gameOver = false;
        // --- For 2x2, randomize initial state to avoid previous pattern ---
        if (boardSize == 2) {
            java.security.SecureRandom secureRandom = new java.security.SecureRandom();
            String randomStart = secureRandom.nextBoolean() ? player1Symbol : player2Symbol;
            // Flip if same as previous
            if (previousStartingPlayer2x2 != null && randomStart.equals(previousStartingPlayer2x2)) {
                randomStart = randomStart.equals(player1Symbol) ? player2Symbol : player1Symbol;
            }
            currentPlayer = randomStart;
            previousStartingPlayer2x2 = currentPlayer;
        } else {
        currentPlayer = player1Symbol;
        statusLabel.setText(player1Name + " (" + player1Symbol + ")'s Turn");
        }
        // Clear the board
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = "";
            }
        }
        
        // Clear undo/redo stacks - this is crucial for proper functionality
        moveHistory.clear();
        redoStack.clear();
        
        updatePlayerCards();
        // Repaint the custom board panel directly
        if (this.boardPanel != null) this.boardPanel.repaint();
        if (boardSize == 2) {
            tossReadyForMove = false;
            if (statusLabel != null) statusLabel.setVisible(false);
            if (tossButton[0] != null) tossButton[0].setVisible(true);
            return;
        }
    }
    
    private void showWinDialog() {
        javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Game Over! Would you like to play again?",
                "Game Finished",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                resetGame();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private JLabel createPlayerCard(String name, int score, Color color) {
        JLabel label = new JLabel("<html><div style='text-align:center;'><b>" + name + "</b><br/>Score: " + score + "</div></html>");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    private void updatePlayerCards() {
        player1Label.setText("<html><div style='text-align:center;'><b>" + player1Name + " (" + player1Symbol + ")</b><br/>Score: " + player1Score + "</div></html>");
        player2Label.setText("<html><div style='text-align:center;'><b>" + player2Name + " (" + player2Symbol + ")</b><br/>Score: " + player2Score + "</div></html>");
    }
    
    private void recordGameResult() {
        long gameDuration = System.currentTimeMillis() - gameStartTime;
        boolean won = statusLabel.getText().contains("Wins");
        parent.recordGameResult("TicTacToe", won, gameDuration);
    }
}

class OthelloGame extends JPanel {
    private GamePlatform parent;
    private String[][] board;
    private String currentPlayer = "B"; // Black starts
    private boolean gameOver = false;
    private final int SIZE = 8;
    private JLabel statusLabel;
    private JLabel player1ScoreLabel, player2ScoreLabel;
    private JPanel player1Label, player2Label;
    private JPanel player1Panel, player2Panel; // <-- Add these fields
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";
    private String player1Symbol = "B";
    private String player2Symbol = "W";
    private JTextField name1Field, name2Field, symbol1Field, symbol2Field;
    private boolean gameStarted = false;
    private JPanel setupPanel;
    private JPanel gamePanel;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel topBlackCountLabel;
    private JLabel bottomWhiteCountLabel;
    private JLabel topTimerLabel;
    private JLabel bottomTimerLabel;
    private javax.swing.Timer gameTimer;
    private long startTimeMillis;
    private boolean timerRunning = false;
    private long timerBaseMillis = 0;
    private OthelloBoardPanel boardPanel;
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},          {0, 1},
        {1, -1},  {1, 0}, {1, 1}
    };

    // Inner class for the Othello board panel
    class OthelloBoardPanel extends JPanel {
        private String[][] board;
        private int size;
        
        public OthelloBoardPanel(String[][] board, int size) {
            this.board = board;
            this.size = size;
            setBackground(new Color(34, 139, 34)); // Dark green background
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int margin = 20;
            int boardPx = Math.min(w, h) - 2 * margin;
            int cellSize = boardPx / size;
            int boardX = (w - boardPx) / 2;
            int boardY = (h - boardPx) / 2;

            // --- Draw dark wooden border ---
            int borderThickness = 28;
            int outerX = boardX - borderThickness;
            int outerY = boardY - borderThickness;
            int outerSize = boardPx + 2 * borderThickness;
            // Wood gradient
            GradientPaint woodGradient = new GradientPaint(
                outerX, outerY,
                new Color(60, 36, 18), // dark brown
                outerX + outerSize, outerY + outerSize,
                new Color(99, 66, 33) // lighter brown
            );
            g2d.setPaint(woodGradient);
            g2d.fillRoundRect(outerX, outerY, outerSize, outerSize, 36, 36);
            // Add subtle wood grain lines
            g2d.setStroke(new BasicStroke(2f));
            for (int i = 0; i < 12; i++) {
                int y = outerY + 8 + i * (outerSize - 16) / 12;
                g2d.setColor(new Color(80 + (i % 2) * 20, 52, 24, 60));
                g2d.drawLine(outerX + 10, y, outerX + outerSize - 10, y + (i % 3 - 1) * 6);
            }
            // Add some knots
            for (int i = 0; i < 3; i++) {
                int knotX = outerX + 30 + i * (outerSize - 60) / 3;
                int knotY = outerY + 20 + (i % 2) * (outerSize - 40) / 2;
                g2d.setColor(new Color(50, 30, 15, 90));
                g2d.drawOval(knotX, knotY, 16, 10);
            }
            // --- End wooden border ---
            
            // Draw board background
            g2d.setColor(new Color(34, 139, 34));
            g2d.fillRect(boardX, boardY, boardPx, boardPx);
            
            // Draw grid lines
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i <= size; i++) {
                g2d.drawLine(boardX + i * cellSize, boardY, boardX + i * cellSize, boardY + boardPx);
                g2d.drawLine(boardX, boardY + i * cellSize, boardX + boardPx, boardY + i * cellSize);
            }
            
            // Draw pieces and valid moves
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int x = boardX + j * cellSize;
                    int y = boardY + i * cellSize;
                    
                    // Draw valid move indicators
                    if (board[i][j].isEmpty() && isValidMove(i, j)) {
                        g2d.setColor(new Color(0, 255, 0, 80)); // Semi-transparent green
                        g2d.fillOval(x + 4, y + 4, cellSize - 8, cellSize - 8);
                        g2d.setColor(new Color(0, 255, 0));
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawOval(x + 4, y + 4, cellSize - 8, cellSize - 8);
                    }
                    
                    // Draw pieces
                    if (!board[i][j].isEmpty()) {
                        if (board[i][j].equals("B")) {
                            g2d.setColor(Color.BLACK);
                        } else {
                            g2d.setColor(Color.WHITE);
                        }
                        g2d.fillOval(x + 4, y + 4, cellSize - 8, cellSize - 8);
                    }
                }
            }
        }
    }

    private boolean isValidMove(int row, int col) {
        // If cell is not empty, move is invalid
        if (board[row][col] != null && !board[row][col].isEmpty()) return false;
        
        String opponent = currentPlayer.equals("B") ? "W" : "B";
        
        // Check all 8 directions
        for (int[] dir : DIRECTIONS) {
            int r = row + dir[0], c = col + dir[1];
            
            // Skip if first step is invalid or not opponent
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || 
                board[r][c] == null || !board[r][c].equals(opponent)) {
                continue;
            }
            
            // Continue in this direction
            r += dir[0];
            c += dir[1];
            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
                // Empty cell, invalid in this direction
                if (board[r][c] == null || board[r][c].isEmpty()) {
                    break;
                }
                // Found our own piece after opponent piece(s)
                if (board[r][c].equals(currentPlayer)) {
                    return true;
                }
                // Keep going if we find more opponent pieces
                if (board[r][c].equals(opponent)) {
                    r += dir[0];
                    c += dir[1];
                } else {
                    break;
                }
            }
        }
        return false;
    }

    private void flipPieces(int row, int col) {
        String opponent = currentPlayer.equals("B") ? "W" : "B";
        
        // Check all 8 directions
        for (int[] dir : DIRECTIONS) {
            int r = row + dir[0], c = col + dir[1];
            
            // Skip if first step is invalid or not opponent
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || 
                board[r][c] == null || !board[r][c].equals(opponent)) {
                continue;
            }
            
            // Store pieces to flip
            java.util.ArrayList<int[]> toFlip = new java.util.ArrayList<>();
            toFlip.add(new int[]{r, c});
            
            // Continue in this direction
            r += dir[0];
            c += dir[1];
            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
                // Empty cell, invalid in this direction
                if (board[r][c] == null || board[r][c].isEmpty()) {
                    break;
                }
                // Found our own piece, flip all pieces in between
                if (board[r][c].equals(currentPlayer)) {
                    for (int[] pos : toFlip) {
                        board[pos[0]][pos[1]] = currentPlayer;
                    }
                    break;
                }
                // Keep going if we find more opponent pieces
                if (board[r][c].equals(opponent)) {
                    toFlip.add(new int[]{r, c});
                    r += dir[0];
                    c += dir[1];
                } else {
                    break;
                }
            }
        }
    }
    
    public OthelloGame(GamePlatform parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        this.board = new String[SIZE][SIZE]; // Initialize the board array
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton backButton = createStyledButton("← Back to Games", new Color(100, 116, 139));
        backButton.addActionListener(e -> parent.showHub());
        backButton.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("OTHELLO ARENA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Timer and controls (top right of header)
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        timerPanel.setOpaque(false);
        topTimerLabel = new JLabel("Time: 00:00");
        topTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topTimerLabel.setForeground(new Color(60, 60, 60));
        JButton startTimerBtn = new JButton("Start Timer");
        startTimerBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton stopTimerBtn = new JButton("Stop Timer");
        stopTimerBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton resetTimerBtn = new JButton("Reset Timer");
        resetTimerBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton newGameBtn = new JButton("New Game");
        newGameBtn.setFont(new Font("Arial", Font.BOLD, 14));
        newGameBtn.setForeground(Color.WHITE);
        newGameBtn.setBackground(new Color(59, 130, 246)); // Match Othello header button blue
        newGameBtn.setOpaque(true);
        newGameBtn.setBorder(BorderFactory.createLineBorder(new Color(59, 130, 246), 2));
        newGameBtn.setPreferredSize(new Dimension(120, 40));
        timerPanel.add(topTimerLabel);
        timerPanel.add(startTimerBtn);
        timerPanel.add(stopTimerBtn);
        timerPanel.add(resetTimerBtn);
        timerPanel.add(newGameBtn);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(timerPanel, BorderLayout.EAST);

        // Player Setup Panel
        JPanel setupPanel = new JPanel(new GridBagLayout());
        setupPanel.setBackground(new Color(248, 250, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel setupTitle = new JLabel("Othello Player Setup");
        setupTitle.setFont(new Font("Arial", Font.BOLD, 28));
        setupTitle.setForeground(new Color(30, 41, 59));
        setupPanel.add(setupTitle, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel player1Label = new JLabel("Player 1 Name (Black):");
        player1Label.setFont(new Font("Arial", Font.BOLD, 18));
        setupPanel.add(player1Label, gbc);
        gbc.gridx = 1;
        name1Field = new JTextField(player1Name, 12);
        name1Field.setFont(new Font("Arial", Font.PLAIN, 18));
        Dimension inputSize = new Dimension(220, 40);
        name1Field.setPreferredSize(inputSize);
        name1Field.setMinimumSize(inputSize);
        name1Field.setMaximumSize(inputSize);
        setupPanel.add(name1Field, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel player2Label = new JLabel("Player 2 Name (White):");
        player2Label.setFont(new Font("Arial", Font.BOLD, 18));
        setupPanel.add(player2Label, gbc);
        gbc.gridx = 1;
        name2Field = new JTextField(player2Name, 12);
        name2Field.setFont(new Font("Arial", Font.PLAIN, 18));
        name2Field.setPreferredSize(inputSize);
        name2Field.setMinimumSize(inputSize);
        name2Field.setMaximumSize(inputSize);
        setupPanel.add(name2Field, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton startButton = createStyledButton("Start Game", new Color(16, 185, 129));
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> {
            player1Name = name1Field.getText().trim().isEmpty() ? "Player 1" : name1Field.getText().trim();
            player2Name = name2Field.getText().trim().isEmpty() ? "Player 2" : name2Field.getText().trim();
            cardLayout.show(contentPanel, "GAME");
            updatePlayerPanels(); // <-- update side panels with new names
            initializeBoard();
        });
        setupPanel.add(startButton, gbc);
        
        // Main Game Panel
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(248, 250, 252));
        
        // Status
        statusLabel = new JLabel("Enter player names and click Start Game");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setForeground(new Color(30, 41, 59));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Game Layout with Players on sides
        JPanel mainGamePanel = new JPanel(new BorderLayout());
        mainGamePanel.setBackground(new Color(248, 250, 252));
        
        // Player 1 (Left - Black)
        player1Panel = createPlayerPanel(player1Name, "BLACK", Color.BLACK, true); // <-- assign to field
        // Player 2 (Right - White)  
        player2Panel = createPlayerPanel(player2Name, "WHITE", Color.WHITE, false); // <-- assign to field
        // Board in center
        JPanel boardPanel = createGameBoard();
        mainGamePanel.add(player1Panel, BorderLayout.WEST); // <-- use field
        mainGamePanel.add(boardPanel, BorderLayout.CENTER);
        mainGamePanel.add(player2Panel, BorderLayout.EAST); // <-- use field
        
        gamePanel.add(statusLabel, BorderLayout.NORTH);
        gamePanel.add(mainGamePanel, BorderLayout.CENTER);
        
        // Use CardLayout to switch between setup and game
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(setupPanel, "SETUP");
        contentPanel.add(gamePanel, "GAME");
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        
        // Store references for later use
        this.setupPanel = setupPanel;
        this.gamePanel = gamePanel;
        this.contentPanel = contentPanel;
        this.cardLayout = cardLayout;
        // Show setup panel after everything is initialized
        cardLayout.show(contentPanel, "SETUP");
        
        startTimerBtn.addActionListener(e -> {
            if (!timerRunning) {
                timerBaseMillis = System.currentTimeMillis();
                timerRunning = true;
                updateTimerLabels(0);
                startGameTimer();
            }
        });
        stopTimerBtn.addActionListener(e -> {
            if (timerRunning) {
                stopGameTimer();
                timerRunning = false;
            }
        });
        resetTimerBtn.addActionListener(e -> {
            stopGameTimer();
            timerBaseMillis = System.currentTimeMillis();
            updateTimerLabels(0);
            timerRunning = false;
        });
        newGameBtn.addActionListener(e -> {
            stopGameTimer();
            timerBaseMillis = System.currentTimeMillis();
            updateTimerLabels(0);
            timerRunning = false;
            resetGame();
        });
    }
    
    private void endGame() {
        gameOver = true;
        int blackCount = Integer.parseInt(player1ScoreLabel.getText());
        int whiteCount = Integer.parseInt(player2ScoreLabel.getText());
        
        String winner;
        if (blackCount > whiteCount) {
            winner = "Black wins!";
        } else if (whiteCount > blackCount) {
            winner = "White wins!";
        } else {
            winner = "It's a draw!";
        }
        
        statusLabel.setText(winner);
        
        javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                winner + " Would you like to play again?",
                "Game Finished",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                resetGame();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void resetGame() {
        gameOver = false;
        currentPlayer = "B";
        statusLabel.setText("Black's Turn");
        initializeBoard();
        if (boardPanel != null) boardPanel.repaint();
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JPanel createPlayerSetupPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 250, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        // Title
        JLabel titleLabel = new JLabel("PLAYER SETUP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Setup form
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        formPanel.setBackground(new Color(248, 250, 252));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        // Player 1 setup (Black)
        JPanel player1Panel = new JPanel(new BorderLayout());
        player1Panel.setBackground(Color.WHITE);
        player1Panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        Dimension playerPanelSize = new Dimension(340, 520);
        player1Panel.setPreferredSize(playerPanelSize);
        player1Panel.setMinimumSize(playerPanelSize);
        player1Panel.setMaximumSize(playerPanelSize);
        JLabel player1Title = new JLabel("Player 1 (Black)");
        player1Title.setFont(new Font("Arial", Font.BOLD, 24));
        player1Title.setForeground(new Color(30, 41, 59));
        player1Title.setHorizontalAlignment(SwingConstants.CENTER);
        name1Field = new JTextField(player1Name);
        name1Field.setFont(new Font("Arial", Font.PLAIN, 16));
        name1Field.setBorder(BorderFactory.createTitledBorder("Name"));
        symbol1Field = new JTextField(player1Symbol);
        symbol1Field.setFont(new Font("Arial", Font.PLAIN, 16));
        symbol1Field.setBorder(BorderFactory.createTitledBorder("Symbol"));
        symbol1Field.setEditable(false);
        JPanel player1Fields = new JPanel(new GridLayout(2, 1, 10, 10));
        player1Fields.setOpaque(false);
        player1Fields.add(name1Field);
        player1Fields.add(symbol1Field);
        player1Panel.add(player1Title, BorderLayout.NORTH);
        player1Panel.add(player1Fields, BorderLayout.CENTER);
        // Player 2 setup (White)
        JPanel player2Panel = new JPanel(new BorderLayout());
        player2Panel.setBackground(Color.WHITE);
        player2Panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(128, 128, 128), 3), // grey
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        player2Panel.setPreferredSize(new Dimension(300, 420));
        JLabel player2Title = new JLabel("Player 2 (White)");
        player2Title.setFont(new Font("Arial", Font.BOLD, 24));
        player2Title.setForeground(new Color(30, 41, 59));
        player2Title.setHorizontalAlignment(SwingConstants.CENTER);
        name2Field = new JTextField(player2Name);
        name2Field.setFont(new Font("Arial", Font.PLAIN, 16));
        name2Field.setBorder(BorderFactory.createTitledBorder("Name"));
        symbol2Field = new JTextField(player2Symbol);
        symbol2Field.setFont(new Font("Arial", Font.PLAIN, 16));
        symbol2Field.setBorder(BorderFactory.createTitledBorder("Symbol"));
        symbol2Field.setEditable(false);
        JPanel player2Fields = new JPanel(new GridLayout(2, 1, 10, 10));
        player2Fields.setOpaque(false);
        player2Fields.add(name2Field);
        player2Fields.add(symbol2Field);
        player2Panel.add(player2Title, BorderLayout.NORTH);
        player2Panel.add(player2Fields, BorderLayout.CENTER);
        formPanel.add(player1Panel);
        formPanel.add(player2Panel);
        // Center the formPanel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);
        // Start button
        JButton startButton = parent.createStyledButton("START GAME", new Color(59, 130, 246));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setForeground(Color.WHITE);
        startButton.addActionListener(e -> {
            player1Name = name1Field.getText();
            player2Name = name2Field.getText();
            parent.playSound("click");
            cardLayout.show(contentPanel, "GAME");
            gameStarted = true;
            initializeBoard();
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel createPlayerPanel(String playerName, String colorName, Color color, boolean isLeft) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 3),
            BorderFactory.createEmptyBorder(32, 28, 32, 28)
        ));
        panel.setPreferredSize(new Dimension(200, 300));
        
        // Player info
        JLabel nameLabel = new JLabel(playerName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(new Color(30, 41, 59));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel colorLabel = new JLabel(colorName);
        colorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        colorLabel.setForeground(new Color(100, 116, 139));
        colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Score display
        JLabel scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 56));
        // Use dark color for white piece count for visibility
        if (color.equals(Color.WHITE)) {
            scoreLabel.setForeground(new Color(60, 60, 60));
        } else {
            scoreLabel.setForeground(color);
        }
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (isLeft) {
            player1ScoreLabel = scoreLabel;
        } else {
            player2ScoreLabel = scoreLabel;
        }
        
        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(colorLabel, BorderLayout.CENTER);
        panel.add(scoreLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGameBoard() {
        boardPanel = new OthelloBoardPanel(board, SIZE);
        boardPanel.setPreferredSize(new Dimension(480, 480));
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int w = boardPanel.getWidth();
                int h = boardPanel.getHeight();
                int margin = 20;
                int boardPx = Math.min(w, h) - 2 * margin;
                int cellSize = boardPx / SIZE;
                int boardX = (w - boardPx) / 2;
                int boardY = (h - boardPx) / 2;
                int x = e.getX();
                int y = e.getY();
                int col = (x - boardX) / cellSize;
                int row = (y - boardY) / cellSize;
                if (row >= 0 && row < SIZE && col >= 0 && col < SIZE) {
                    makeMove(row, col);
                    boardPanel.repaint();
                }
            }
        });
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(248, 250, 252));
        container.add(boardPanel);
        return container;
    }
    
    private void initializeBoard() {
        // Clear board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = "";
            }
        }
        
        // Set initial pieces
        board[3][3] = "W";
        board[3][4] = "B";
        board[4][3] = "B";
        board[4][4] = "W";
        
        currentPlayer = "B";
        gameOver = false;
        statusLabel.setText(player1Name + "'s Turn (Black)");
        updateScores();
        updatePieceCounters();
        // Reset timer display and base
        stopGameTimer();
        timerBaseMillis = System.currentTimeMillis();
        updateTimerLabels(0);
        timerRunning = false;
    }
    
    private void makeMove(int row, int col) {
        if (gameOver || !board[row][col].isEmpty()) {
            parent.playSound("error");
            return;
        }
        
        if (isValidMove(row, col)) {
            // Play soothing piece placement sound
            parent.playSound("othello_move");
            
            board[row][col] = currentPlayer;
            
            // Play gentle flip sound for piece flipping
            parent.playSound("othello_flip");
            
            flipPieces(row, col);
            updateScores();
            updatePieceCounters();
            
            // Switch players
            currentPlayer = currentPlayer.equals("B") ? "W" : "B";
            
            // If the next player has no valid moves, skip their turn
            if (!hasValidMoves()) {
                // Switch back to the other player
                currentPlayer = currentPlayer.equals("B") ? "W" : "B";
                // If neither player has valid moves, end the game
                if (!hasValidMovesForPlayer("B") && !hasValidMovesForPlayer("W")) {
                endGame();
                    return;
                } else {
                    // Inform that the other player was skipped
                    statusLabel.setText((currentPlayer.equals("B") ? player1Name : player2Name) +
                        " (" + (currentPlayer.equals("B") ? "Black" : "White") + ")'s Turn (Opponent skipped)");
                    return;
                }
            } else {
                statusLabel.setText((currentPlayer.equals("B") ? player1Name : player2Name) +
                    " (" + (currentPlayer.equals("B") ? "Black" : "White") + ")'s Turn");
            }
        } else {
            parent.playSound("error");
        }
    }
    
    private boolean checkWinner(int row, int col) {
        String symbol = board[row][col];
        
        // Check row
        int count = 1;
        for (int i = col - 1; i >= 0 && board[row][i].equals(symbol); i--) count++;
        for (int i = col + 1; i < SIZE && board[row][i].equals(symbol); i++) count++;
        if (count >= SIZE) return true;
        
        // Check column
        count = 1;
        for (int i = row - 1; i >= 0 && board[i][col].equals(symbol); i--) count++;
        for (int i = row + 1; i < SIZE && board[i][col].equals(symbol); i++) count++;
        if (count >= SIZE) return true;
        
        // Check main diagonal
        count = 1;
        for (int i = 1; row - i >= 0 && col - i >= 0 && board[row - i][col - i].equals(symbol); i++) count++;
        for (int i = 1; row + i < SIZE && col + i < SIZE && board[row + i][col + i].equals(symbol); i++) count++;
        if (count >= SIZE) return true;
        
        // Check anti-diagonal
        count = 1;
        for (int i = 1; row - i >= 0 && col + i < SIZE && board[row - i][col + i].equals(symbol); i++) count++;
        for (int i = 1; row + i < SIZE && col - i >= 0 && board[row + i][col - i].equals(symbol); i++) count++;
        if (count >= SIZE) return true;
        
        return false;
    }
    
    private boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == null || board[i][j].isEmpty()) return false;
            }
        }
        return true;
    }
    
    private void showWinDialog() {
        javax.swing.Timer timer = new javax.swing.Timer(1500, e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Game Over! Would you like to play again?",
                "Game Finished",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                resetGame();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private JLabel createPlayerCard(String name, String colorName, Color color) {
        JLabel label = new JLabel("<html><div style='text-align:center;'><b>" + name + "</b><br/>" + colorName + "</div></html>");
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setOpaque(true);
        label.setBackground(color);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    private void updatePlayerCards() {
        // Update player cards if they exist
        if (player1Label != null && player2Label != null) {
            player1Label.removeAll();
            player1Label.add(createPlayerCard(player1Name, "BLACK", Color.BLACK));
            player2Label.removeAll();
            player2Label.add(createPlayerCard(player2Name, "WHITE", Color.WHITE));
            player1Label.revalidate();
            player2Label.revalidate();
        }
    }
    
    private void recordGameResult() {
        long gameDuration = System.currentTimeMillis() - startTimeMillis;
        boolean won = statusLabel.getText().contains("Wins");
        parent.recordGameResult("Othello", won, gameDuration);
    }

    // --- OTHELLO LOGIC IMPLEMENTATION ---
    private boolean hasValidMoves() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isValidMove(i, j)) return true;
            }
        }
        return false;
    }

    private void updateScores() {
        int black = 0, white = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ("B".equals(board[i][j])) black++;
                else if ("W".equals(board[i][j])) white++;
            }
        }
        if (player1ScoreLabel != null) player1ScoreLabel.setText(String.valueOf(black));
        if (player2ScoreLabel != null) player2ScoreLabel.setText(String.valueOf(white));
    }

    private void updatePieceCounters() {
        // Optionally update any additional piece counters if present
        updateScores();
    }
    // --- END OTHELLO LOGIC IMPLEMENTATION ---

    // --- PROPER TIMER IMPLEMENTATION ---
    private long getTimerElapsedMillis() { 
        if (timerRunning) {
            return System.currentTimeMillis() - timerBaseMillis;
        }
        return 0;
    }
    
    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new javax.swing.Timer(1000, e -> {
            long elapsed = getTimerElapsedMillis() / 1000;
            updateTimerLabels((int)elapsed);
        });
        gameTimer.start();
    }
    
    private void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    private void updateTimerLabels(int elapsedSeconds) {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        String timeText = String.format("Time: %02d:%02d", minutes, seconds);
        if (topTimerLabel != null) {
            topTimerLabel.setText(timeText);
        }
        if (bottomTimerLabel != null) {
            bottomTimerLabel.setText(timeText);
        }
    }
    // --- END TIMER IMPLEMENTATION ---

    // Helper to check valid moves for any player (robustness)
    private boolean hasValidMovesForPlayer(String player) {
        String originalPlayer = currentPlayer;
        currentPlayer = player;
        boolean result = hasValidMoves();
        currentPlayer = originalPlayer;
        return result;
    }

    // Add this method to update player panels
    private void updatePlayerPanels() {
        if (player1Panel != null) {
            player1Panel.removeAll();
            JLabel nameLabel = new JLabel(player1Name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            nameLabel.setForeground(new Color(30, 41, 59));
            JLabel colorLabel = new JLabel("BLACK", SwingConstants.CENTER);
            colorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            colorLabel.setForeground(new Color(100, 116, 139));
            player1Panel.add(nameLabel, BorderLayout.NORTH);
            player1Panel.add(colorLabel, BorderLayout.CENTER);
            player1Panel.add(player1ScoreLabel, BorderLayout.SOUTH);
            player1Panel.revalidate();
            player1Panel.repaint();
        }
        if (player2Panel != null) {
            player2Panel.removeAll();
            JLabel nameLabel = new JLabel(player2Name, SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            nameLabel.setForeground(new Color(30, 41, 59));
            JLabel colorLabel = new JLabel("WHITE", SwingConstants.CENTER);
            colorLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            colorLabel.setForeground(new Color(100, 116, 139));
            player2Panel.add(nameLabel, BorderLayout.NORTH);
            player2Panel.add(colorLabel, BorderLayout.CENTER);
            player2Panel.add(player2ScoreLabel, BorderLayout.SOUTH);
            player2Panel.revalidate();
            player2Panel.repaint();
        }
    }
}

class SudokuGame extends JPanel {
    private GamePlatform parent;
    private JTextField[][] cells;
    private int[][] solution;
    private int[][] puzzle;
    private boolean[][] fixed;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private long startTime;
    private javax.swing.Timer timer;
    private int difficulty = 40; // Number of cells to remove
    private JComboBox<String> difficultyBox;
    // Track last selected cell
    private int selectedRow = -1;
    private int selectedCol = -1;
    // In SudokuGame class fields:
    private boolean solutionViewed = false;
    // Lifeline system
    private int lifelines = 5;
    private JPanel heartLabel;
    private final String heartImage = "images/lifeline.png";
    
    // Track wrong moves for highlighting
    private boolean[][] wasWrong = new boolean[9][9];
    private boolean isResetting = false; // Flag to prevent lifeline decrement during reset
    
    public SudokuGame(GamePlatform parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        initializeComponents();
        generateNewPuzzle();
    }
    
    private void initializeComponents() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton backButtonSudoku = parent.createStyledButton("← Back to Games", new Color(100, 116, 139));
        backButtonSudoku.addActionListener(e -> {
            parent.playSound("click");
            parent.showHub();
        });
        backButtonSudoku.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("SUDOKU PUZZLE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(30, 41, 59));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Difficulty selector
        String[] levels = {"Easy", "Medium", "Hard", "Super Hard"};
        difficultyBox = new JComboBox<>(levels);
        difficultyBox.setFont(new Font("Arial", Font.BOLD, 14));
        difficultyBox.setSelectedIndex(1); // Default to Medium
        difficultyBox.addActionListener(e -> {
            setDifficulty();
            parent.playSound("click");
        });
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(difficultyBox);
        
        JButton newGameButton = parent.createStyledButton("NEW GAME", new Color(168, 85, 247));
        newGameButton.addActionListener(e -> {
            parent.playSound("click");
            generateNewPuzzle();
            solutionViewed = false;
        });
        newGameButton.setForeground(Color.WHITE);
        rightPanel.add(newGameButton);
        
        headerPanel.add(backButtonSudoku, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Main Game Panel
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBackground(new Color(248, 250, 252));
        
        // Status and Timer
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(248, 250, 252));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        statusLabel = new JLabel("Fill in the numbers 1-9 in each row, column, and 3×3 box");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(30, 41, 59));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        timerLabel = new JLabel("Time: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(new Color(100, 116, 139));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(timerLabel, BorderLayout.SOUTH);
        
        // Sudoku Board
        JPanel boardPanel = createSudokuBoard();
        
        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(new Color(248, 250, 252));
        
        JButton checkButton = parent.createStyledButton("CHECK SOLUTION", new Color(16, 185, 129));
        checkButton.setForeground(Color.WHITE);
        checkButton.addActionListener(e -> {
            parent.playSound("click");
            checkSolution();
        });
        
        JButton solveButton = parent.createStyledButton("SHOW SOLUTION", new Color(239, 68, 68));
        solveButton.setForeground(Color.WHITE);
        solveButton.addActionListener(e -> {
            parent.playSound("click");
            showSolution();
            solutionViewed = true;
        });
        
        JButton clearButton = parent.createStyledButton("CLEAR", new Color(100, 116, 139));
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> {
            parent.playSound("click");
            clearBoard();
        });
        
        JButton startTimerButton = parent.createStyledButton("START TIMER", new Color(59, 130, 246));
        startTimerButton.setForeground(Color.WHITE);
        startTimerButton.addActionListener(e -> {
            parent.playSound("click");
            startTimer();
        });
        JButton stopTimerButton = parent.createStyledButton("STOP TIMER", new Color(239, 68, 68));
        stopTimerButton.setForeground(Color.WHITE);
        stopTimerButton.addActionListener(e -> {
            parent.playSound("click");
            stopTimer();
        });
        
        JButton resetTimerBtn = new JButton("RESET TIMER") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(168, 85, 247)); // Bright purple
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        resetTimerBtn.setFont(new Font("Arial", Font.BOLD, 15));
        resetTimerBtn.setForeground(Color.WHITE);
        resetTimerBtn.setBackground(new Color(168, 85, 247));
        resetTimerBtn.setOpaque(false);
        resetTimerBtn.setBorder(BorderFactory.createEmptyBorder());
        resetTimerBtn.setPreferredSize(new Dimension(130, 40));
        resetTimerBtn.addActionListener(e -> {
            stopTimer();
            timerLabel.setText("00:00");
            // Do not reset solutionViewed here
        });
        
        controlPanel.add(checkButton);
        controlPanel.add(solveButton);
        controlPanel.add(clearButton);
        controlPanel.add(startTimerButton);
        controlPanel.add(stopTimerButton);
        controlPanel.add(resetTimerBtn);
        
        gamePanel.add(statusPanel, BorderLayout.NORTH);
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);
        
        // Do not start timer automatically
        timerLabel.setText("Time: 00:00");
    }
    
    // Custom Sudoku cell with visible grid lines
    class SudokuCell extends JTextField {
        int row, col;
        SudokuCell(int row, int col) {
            super();
            this.row = row;
            this.col = col;
            setFont(new Font("Segoe UI", Font.BOLD, 26));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setPreferredSize(new Dimension(56, 56));
            setMinimumSize(new Dimension(56, 56));
            setMaximumSize(new Dimension(56, 56));
            setOpaque(true);
            setBackground(new Color(245, 222, 179)); // Match board border color
            // Track selection
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedRow = row;
                    selectedCol = col;
                }
            });
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    selectedRow = row;
                    selectedCol = col;
                }
            });
            getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                private void checkInput() {
                    SudokuGame.this.updateAllCellHighlights();
                }
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
            });
        }
            @Override
            protected void paintComponent(Graphics g) {
            super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            // Draw thick black borders for 3x3 boxes
            if (row % 3 == 0) {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(0, 0, w, 0);
            }
            if (col % 3 == 0) {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(0, 0, 0, h);
            }
            // Draw thick border on right and bottom edges
            if (col == 8) {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(w - 1, 0, w - 1, h);
            }
            if (row == 8) {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(0, h - 1, w, h - 1);
            }
            // Draw thin gray borders for other cells
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(0, 0, w - 1, h - 1);
            g2d.dispose();
        }
    }

    private JPanel createSudokuBoard() {
        JPanel container = new JPanel(new BorderLayout(20, 0));
        container.setBackground(new Color(248, 250, 252));
        // Main Sudoku Board Panel
        JPanel boardPanel = new JPanel(new GridLayout(9, 9, 0, 0)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(44 * 9, 44 * 9);
            }
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(44 * 9, 44 * 9);
            }
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(44 * 9, 44 * 9);
            }
        };
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(new Color(245, 222, 179));
        cells = new JTextField[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j] = new SudokuCell(i, j);
                // Only allow numbers 1-9
                cells[i][j].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) || c == '0') {
                            e.consume();
                        }
                        if (((JTextField) e.getSource()).getText().length() >= 1) {
                            e.consume();
                        }
                    }
                });
                boardPanel.add(cells[i][j]);
            }
        }
        // Heart lifeline panel
        JPanel heartPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        heartPanel.setBackground(new Color(248, 250, 252));
        heartPanel.setPreferredSize(new Dimension(250, 40));
        heartLabel = heartPanel; // Use the panel as the heart label
        updateHeartImage();
          // Number Pad Panel (Right Side)
        JPanel numberPadPanel = new JPanel(new BorderLayout());
        numberPadPanel.setBackground(new Color(248, 250, 252));
        JPanel padGrid = new JPanel(new GridLayout(5, 2, 8, 8));
        padGrid.setBackground(new Color(248, 250, 252));
        padGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton[] padButtons = new JButton[10];
        for (int i = 1; i <= 9; i++) {
            JButton numberButton = new JButton(String.valueOf(i)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(59, 130, 246));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                    int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
            numberButton.setPreferredSize(new Dimension(50, 50));
            numberButton.setMaximumSize(new Dimension(50, 50));
            numberButton.setMinimumSize(new Dimension(50, 50));
            numberButton.setBorder(BorderFactory.createEmptyBorder());
            numberButton.setContentAreaFilled(false);
            numberButton.setFocusPainted(false);
            final int number = i;
            numberButton.addActionListener(e -> {
                parent.playSound("click");
                if (selectedRow < 0 || selectedCol < 0 || fixed[selectedRow][selectedCol]) {
                    outer: for (int r = 0; r < 9; r++) {
                        for (int c = 0; c < 9; c++) {
                            if (!fixed[r][c]) {
                                selectedRow = r;
                                selectedCol = c;
                                break outer;
                            }
                        }
                    }
                }
                if (selectedRow >= 0 && selectedCol >= 0 && !fixed[selectedRow][selectedCol]) {
                    cells[selectedRow][selectedCol].setText(String.valueOf(number));
                    cells[selectedRow][selectedCol].requestFocus();
                    cells[selectedRow][selectedCol].setCaretPosition(1);
                }
            });
            numberButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    numberButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    numberButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });
            padButtons[i - 1] = numberButton;
        }
        // 'X' button for clearing
        JButton clearButton = new JButton("X") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(59, 130, 246));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);
                g2d.dispose();
            }
        };
        clearButton.setPreferredSize(new Dimension(50, 50));
        clearButton.setMaximumSize(new Dimension(50, 50));
        clearButton.setMinimumSize(new Dimension(50, 50));
        clearButton.setBorder(BorderFactory.createEmptyBorder());
        clearButton.setContentAreaFilled(false);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> {
            parent.playSound("click");
            if (selectedRow < 0 || selectedCol < 0 || fixed[selectedRow][selectedCol]) {
                outer: for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        if (!fixed[r][c]) {
                            selectedRow = r;
                            selectedCol = c;
                            break outer;
                        }
                    }
                }
            }
            if (selectedRow >= 0 && selectedCol >= 0 && !fixed[selectedRow][selectedCol]) {
                cells[selectedRow][selectedCol].setText("");
                cells[selectedRow][selectedCol].requestFocus();
            }
        });
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                clearButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        padButtons[9] = clearButton;
        // Add buttons to the grid in 2 columns, 5 rows
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 2; col++) {
                int idx = row * 2 + col;
                padGrid.add(padButtons[idx]);
            }
        }
        numberPadPanel.add(heartPanel, BorderLayout.NORTH);
        numberPadPanel.add(padGrid, BorderLayout.CENTER);
        // Center the board and number pad horizontally and vertically
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 30);
        centerPanel.add(boardPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        centerPanel.add(numberPadPanel, gbc);
        container.add(centerPanel, BorderLayout.CENTER);
        return container;
    }
    
    private void setDifficulty() {
        String level = (String) difficultyBox.getSelectedItem();
        if (level.equals("Easy")) difficulty = 30;
        else if (level.equals("Medium")) difficulty = 40;
        else if (level.equals("Hard")) difficulty = 50;
        else if (level.equals("Super Hard")) difficulty = 55;
    }
    
    private void generateNewPuzzle() {
        setDifficulty();
        // Generate a complete Sudoku solution
        solution = generateSolution();
        // Create puzzle by removing some numbers
        puzzle = new int[9][9];
        fixed = new boolean[9][9];
        Random random = new Random();
        
        // Reduced retry limit for faster generation, especially for Super Hard
        int maxRetries = difficulty >= 55 ? 20 : 50; // Fewer retries for Super Hard
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            // Copy solution to puzzle
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    puzzle[i][j] = solution[i][j];
                }
            }
            
            // Remove numbers randomly
            int cellsToRemove = difficulty;
            while (cellsToRemove > 0) {
                int row = random.nextInt(9);
                int col = random.nextInt(9);
                if (puzzle[row][col] != 0) {
                    puzzle[row][col] = 0;
                    cellsToRemove--;
                }
            }
            
            // Quick check before expensive unique solution verification
            if (difficulty >= 55) {
                int emptyCells = 0;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (puzzle[i][j] == 0) emptyCells++;
                    }
                }
                // Skip expensive check if too many empty cells
                if (emptyCells <= 55 && hasUniqueSolution(puzzle)) break;
            } else {
                if (hasUniqueSolution(puzzle)) break;
            }
            
            retryCount++;
        }
        
        // If we couldn't find a unique solution within retries, use a fallback approach
        if (retryCount >= maxRetries) {
            // For Super Hard, try with fewer cells removed for faster generation
            if (difficulty >= 55) {
                // Reduce difficulty more aggressively to ensure quick generation
                int fallbackDifficulty = 50; // Reduced from 52 to 50
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        puzzle[i][j] = solution[i][j];
                    }
                }
                
                int cellsToRemove = fallbackDifficulty;
                while (cellsToRemove > 0) {
                    int row = random.nextInt(9);
                    int col = random.nextInt(9);
                    if (puzzle[row][col] != 0) {
                        puzzle[row][col] = 0;
                        cellsToRemove--;
                    }
                }
            }
        }
        
        // Mark fixed cells
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                fixed[i][j] = puzzle[i][j] != 0;
            }
        }
        
        // Reset lifelines and wrong move tracking
        lifelines = 5;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                wasWrong[i][j] = false;
            }
        }
        
        // Set reset flag to prevent lifeline decrement during board update
        isResetting = true;
        updateBoard();
        isResetting = false;
        
        // Update hearts in EDT to ensure proper UI update
        SwingUtilities.invokeLater(() -> {
            updateHeartImage();
            if (heartLabel != null) {
                heartLabel.revalidate();
                heartLabel.repaint();
            }
        });
        
        // Reset timer and status
        stopTimer();
        timerLabel.setText("Time: 00:00");
        statusLabel.setText("Fill in the numbers 1-9 in each row, column, and 3×3 box");
        statusLabel.setForeground(new Color(30, 41, 59));
        
        parent.playSound("click");
        solutionViewed = false;
    }

    // Helper to check for unique solution with further optimization
    private boolean hasUniqueSolution(int[][] puzzle) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++)
            System.arraycopy(puzzle[i], 0, copy[i], 0, 9);
        
        // For Super Hard difficulty, use even more aggressive early termination
        if (difficulty >= 55) {
            // Quick check: if too many cells are empty, it's likely to have multiple solutions
            int emptyCells = 0;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (copy[i][j] == 0) emptyCells++;
                }
            }
            // More aggressive threshold for Super Hard
            if (emptyCells > 53) return false; // Reduced from 55 to 53
        }
        
        return countSolutions(copy, 2) == 1;
    }
    
    // Further optimized backtracking solution counter
    private int countSolutions(int[][] board, int limit) {
        // More aggressive early termination for very sparse puzzles
        int emptyCells = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) emptyCells++;
            }
        }
        
        // More aggressive threshold for Super Hard
        if (difficulty >= 55 && emptyCells > 55) return 2; // Assume multiple solutions
        if (emptyCells > 58) return 2; // General threshold reduced from 60 to 58
        
        return countSolutionsRecursive(board, limit);
    }
    
    private int countSolutionsRecursive(int[][] board, int limit) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    int count = 0;
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            count += countSolutionsRecursive(board, limit - count);
                            if (count >= limit) {
                                board[row][col] = 0;
                                return count;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return count;
                }
            }
        }
        return 1;
    }
    
    private int[][] generateSolution() {
        int[][] board = new int[9][9];
        solveSudoku(board);
        return board;
    }
    
    private boolean solveSudoku(int[][] board) {
        // Use a more efficient solving approach
        return solveSudokuOptimized(board, 0, 0);
    }
    
    private boolean solveSudokuOptimized(int[][] board, int row, int col) {
        // Find next empty cell
        while (row < 9 && board[row][col] != 0) {
            col++;
            if (col == 9) {
                col = 0;
                row++;
            }
        }
        
        if (row == 9) return true; // Puzzle solved
        
        // Try numbers 1-9 in optimized order
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        shuffleArray(numbers);
        
        for (int num : numbers) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;
                if (solveSudokuOptimized(board, row, col)) {
                    return true;
                }
                board[row][col] = 0;
            }
        }
        return false;
    }
    
    private void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    private boolean isValid(int[][] board, int row, int col, int num) {
        // Check row
        for (int x = 0; x < 9; x++) {
            if (board[row][x] == num) return false;
        }
        
        // Check column
        for (int x = 0; x < 9; x++) {
            if (board[x][col] == num) return false;
        }
        
        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i + startRow][j + startCol] == num) return false;
            }
        }
        
        return true;
    }
    
    private void updateBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // Reset background to default color first
                cells[i][j].setBackground(new Color(245, 222, 179)); // Default tan background
                
                if (puzzle[i][j] == 0) {
                    cells[i][j].setText("");
                    cells[i][j].setEditable(true);
                    cells[i][j].setForeground(new Color(34, 139, 34)); // Vibrant green for user input
                    cells[i][j].setFont(new Font("Segoe UI", Font.BOLD, 26));
                } else {
                    cells[i][j].setText(String.valueOf(puzzle[i][j]));
                    cells[i][j].setEditable(false);
                    cells[i][j].setForeground(new Color(200, 0, 0)); // Red for fixed
                    cells[i][j].setFont(new Font("Segoe UI", Font.BOLD, 26));
                }
            }
        }
    }
    
    private void checkSolution() {
        int[][] currentBoard = new int[9][9];
        
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String text = cells[i][j].getText();
                if (text.isEmpty()) {
                    currentBoard[i][j] = 0;
                } else {
                    currentBoard[i][j] = Integer.parseInt(text);
                }
            }
        }
        
        if (solutionViewed) {
            statusLabel.setText("Solution was revealed. This does not count as a win.");
            statusLabel.setForeground(new Color(239, 68, 68));
            parent.playSound("error");
            return;
        }
        if (isComplete(currentBoard)) {
            statusLabel.setText("Congratulations! Puzzle solved correctly!");
            statusLabel.setForeground(new Color(16, 185, 129));
            stopTimer();
            parent.playSound("win");
            // Record successful completion
            long gameDuration = System.currentTimeMillis() - startTime;
            parent.recordGameResult("Sudoku", true, gameDuration);
            // Ask to play again
            javax.swing.Timer timer = new javax.swing.Timer(1200, e2 -> {
                int choice = javax.swing.JOptionPane.showConfirmDialog(
                    this,
                    "Congratulations! Would you like to play again?",
                    "Puzzle Solved",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE
                );
                if (choice == javax.swing.JOptionPane.YES_OPTION) {
                    generateNewPuzzle();
                }
            });
            timer.setRepeats(false);
            timer.start();
            return;
        } else {
            statusLabel.setText("Not quite right. Keep trying!");
            statusLabel.setForeground(new Color(239, 68, 68));
            parent.playSound("error");
            // Show error message for 2 seconds, then revert
            javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
                statusLabel.setText("Fill in the numbers 1-9 in each row, column, and 3×3 box");
                statusLabel.setForeground(new Color(30, 41, 59));
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private boolean isComplete(int[][] board) {
        // Check if all cells are filled
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        
        // Check if solution is correct
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] != solution[i][j]) return false;
            }
        }
        
        return true;
    }
    
    private void showSolution() {
        solutionViewed = true; // Set this flag first!
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setText(String.valueOf(solution[i][j]));
                cells[i][j].setEditable(false);
                cells[i][j].setBackground(new Color(245, 222, 179)); // Default tan
                cells[i][j].setForeground(new Color(100, 116, 139)); // Grey for all
                cells[i][j].setFont(new Font("Segoe UI", Font.BOLD, 26));
            }
        }
        statusLabel.setText("Solution revealed");
        statusLabel.setForeground(new Color(168, 85, 247));
        stopTimer();
        parent.playSound("click");
        
        // Record completion with solution help
        long gameDuration = System.currentTimeMillis() - startTime;
        parent.recordGameResult("Sudoku", false, gameDuration);
    }
    
    private void clearBoard() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!fixed[i][j]) {
                    cells[i][j].setText("");
                }
            }
        }
        statusLabel.setText("Fill in the numbers 1-9 in each row, column, and 3×3 box");
        statusLabel.setForeground(new Color(30, 41, 59));
    }
    
    private void startTimer() {
        stopTimer();
        startTime = System.currentTimeMillis();
        timer = new javax.swing.Timer(1000, e -> updateTimer());
        timer.start();
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    private void updateTimer() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    // Add this method to SudokuGame class (outside SudokuCell)
    private void updateAllCellHighlights() {
        // Don't update lifelines during reset or if solution is revealed
        if (isResetting || solutionViewed) return;
        
        boolean lostLife = false;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                JTextField cell = cells[i][j];
                String text = cell.getText();
                if (text.isEmpty()) {
                    cell.setBackground(new Color(245, 222, 179));
                    if (cell.isEditable()) {
                        cell.setForeground(new Color(34, 139, 34));
                        cell.setFont(new Font("Segoe UI", Font.BOLD, 26));
                    } else {
                        cell.setForeground(new Color(200, 0, 0));
                        cell.setFont(new Font("Segoe UI", Font.BOLD, 26));
                    }
                    wasWrong[i][j] = false;
                    continue;
                }
                boolean isValid = true;
                if (text.length() == 1 && Character.isDigit(text.charAt(0)) && text.charAt(0) != '0') {
                    int val = Integer.parseInt(text);
                    if (solution != null && solution[i][j] != val) {
                        isValid = false;
                    }
                    for (int k = 0; k < 9; k++) {
                        if (k != j && cells[i][k].getText().equals(text)) {
                            isValid = false;
                        }
                    }
                    for (int k = 0; k < 9; k++) {
                        if (k != i && cells[k][j].getText().equals(text)) {
                            isValid = false;
                        }
                    }
                    int boxRow = (i / 3) * 3;
                    int boxCol = (j / 3) * 3;
                    for (int r = boxRow; r < boxRow + 3; r++) {
                        for (int c = boxCol; c < boxCol + 3; c++) {
                            if ((r != i || c != j) && cells[r][c].getText().equals(text)) {
                                isValid = false;
                            }
                        }
                    }
                    if (!isValid) {
                        cell.setBackground(Color.RED);
                        cell.setForeground(Color.WHITE);
                        if (!wasWrong[i][j]) {
                            lostLife = true;
                            wasWrong[i][j] = true;
                        }
                    } else {
                        cell.setBackground(new Color(245, 222, 179));
                        if (cell.isEditable()) {
                            cell.setForeground(new Color(34, 139, 34));
                            cell.setFont(new Font("Segoe UI", Font.BOLD, 26));
                        } else {
                            cell.setForeground(new Color(200, 0, 0));
                            cell.setFont(new Font("Segoe UI", Font.BOLD, 26));
                        }
                        wasWrong[i][j] = false;
                    }
                } else {
                    cell.setBackground(Color.RED);
                    cell.setForeground(Color.WHITE);
                    if (!wasWrong[i][j]) {
                        lostLife = true;
                        wasWrong[i][j] = true;
                    }
                }
            }
        }
        if (lostLife && lifelines > 0) {
            lifelines--;
            updateHeartImage();
            if (lifelines == 0) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Game Over! No lifelines left. Showing solution.", "Game Over", JOptionPane.ERROR_MESSAGE);
                    showSolution();
                    solutionViewed = true;
                });
            }
        }
    }
    // Helper to update heart image
    private void updateHeartImage() {
        if (heartLabel == null) return;
        
        // Clear existing hearts
        heartLabel.removeAll();
        
        // Add small heart images based on remaining lifelines
        for (int i = 0; i < lifelines; i++) {
            JLabel smallHeart = new JLabel();
            try {
                java.net.URL imgUrl = getClass().getResource("/images/lifeline.png");
                if (imgUrl != null) {
                    ImageIcon originalIcon = new ImageIcon(imgUrl);
                    Image originalImage = originalIcon.getImage();
                    Image scaledImage = originalImage.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
                    smallHeart.setIcon(new ImageIcon(scaledImage));
                } else {
                    smallHeart.setText("♥");
                    smallHeart.setForeground(Color.RED);
                    smallHeart.setFont(new Font("Arial", Font.BOLD, 24));
                }
            } catch (Exception e) {
                smallHeart.setText("♥");
                smallHeart.setForeground(Color.RED);
                smallHeart.setFont(new Font("Arial", Font.BOLD, 24));
            }
            heartLabel.add(smallHeart);
        }
        
        // Force immediate update
        heartLabel.revalidate();
        heartLabel.repaint();
        
        // Also repaint the parent container
        if (heartLabel.getParent() != null) {
            heartLabel.getParent().revalidate();
            heartLabel.getParent().repaint();
        }
    }
}

// Game Statistics Class
class GameStats {
    public int gamesPlayed = 0;
    public int gamesWon = 0;
    public long totalPlayTime = 0; // in milliseconds
    public double winRate = 0.0;
    
    public void updateWinRate() {
        if (gamesPlayed > 0) {
            winRate = (double) gamesWon / gamesPlayed * 100;
        }
    }
    
    public String getFormattedPlayTime() {
        long seconds = totalPlayTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }
}