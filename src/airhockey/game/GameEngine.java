package airhockey.game;

import airhockey.utils.Constants;
import javax.swing.*;
import java.awt.*;

public class GameEngine {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameController gameController;
    private GameRenderer gameRenderer;
    private Timer gameTimer;
    private SettingsPanel settingsPanel;
    private JLabel backgroundLabel; // For background image

    private boolean gameActive = false;
    private int selectedDifficulty = 2;
    private Constants.Theme selectedTheme = Constants.Theme.MODERN;

    public GameEngine() {
        initialize();
    }

    private void initialize() {
        createMainFrame();
        showMainMenu();
    }

    private void createMainFrame() {
        mainFrame = new JFrame("Air Hockey 2D - CS304 Project");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        // Set application icon if available
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/if_33_61497.png"));
            mainFrame.setIconImage(icon.getImage());
        } catch (Exception e) {
            // Use default icon
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainFrame.add(mainPanel);

        // Center window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - Constants.WINDOW_WIDTH) / 2;
        int y = (screenSize.height - Constants.WINDOW_HEIGHT) / 2;
        mainFrame.setLocation(x, y);
    }

    private void showMainMenu() {
        // Create layered pane for background image
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));

        // Try to load background image
        ImageIcon backgroundImage = null;
        try {
            backgroundImage = new ImageIcon(getClass().getResource("/images/blackboard_1205-357.jpg"));
            // Or try other images
            if (backgroundImage == null) {
                backgroundImage = new ImageIcon(getClass().getResource("/images/back.png"));
            }
            if (backgroundImage == null) {
                backgroundImage = new ImageIcon(getClass().getResource("/images/bakgrounf.png"));
            }
        } catch (Exception e) {
            System.out.println("Could not load background image: " + e.getMessage());
        }

        // Create content panel with proper layout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        if (backgroundImage != null && backgroundImage.getImage() != null) {
            // Scale image to fit window
            Image scaledImage = backgroundImage.getImage().getScaledInstance(
                    Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT, Image.SCALE_SMOOTH);
            backgroundImage = new ImageIcon(scaledImage);

            // Create background label
            backgroundLabel = new JLabel(backgroundImage);
            backgroundLabel.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            layeredPane.add(backgroundLabel, JLayeredPane.DEFAULT_LAYER);

            contentPanel.setOpaque(false); // Make transparent
        } else {
            // Use gradient background if no image
            contentPanel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;

                    GradientPaint gradient = new GradientPaint(
                            0, 0, new Color(15, 25, 45),
                            getWidth(), getHeight(), new Color(35, 45, 75)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    g2d.setColor(new Color(255, 255, 255, 30));
                    for (int i = 0; i < 15; i++) {
                        int x = (int)(Math.random() * getWidth());
                        int y = (int)(Math.random() * getHeight());
                        int size = 50 + (int)(Math.random() * 100);
                        g2d.fillOval(x, y, size, size);
                    }
                }
            };
        }

        contentPanel.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        if (backgroundImage == null) {
            layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);
        } else {
            layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        // Title with beautiful styling
        JLabel titleLabel = new JLabel(" AIR HOCKEY 2D ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 64));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold color

        // Add multiple text shadows for 3D effect
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(new Color(139, 0, 0, 100), 3)
        ));

        // Create custom panel for title with background
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        gbc.insets = new Insets(20, 50, 40, 50);
        contentPanel.add(titlePanel, gbc);

        // Create buttons with modern styling
        Color[] buttonColors = {
                new Color(30, 144, 255),   // Dodger Blue - Single Player
                new Color(50, 205, 50),    // Lime Green - Two Players
                new Color(255, 140, 0),    // Dark Orange - Settings
                new Color(220, 20, 60)     // Crimson - Exit
        };

        String[] buttonTexts = {"🎮 SINGLE PLAYER", "👥 TWO PLAYERS", "⚙️ SETTINGS", "🚪 EXIT"};
        Runnable[] buttonActions = {
                () -> showDifficultySelection(true),
                () -> showDifficultySelection(false),
                () -> showSettings(),
                () -> System.exit(0)
        };

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = createModernButton(buttonTexts[i], buttonColors[i]);
            int finalI = i;
            button.addActionListener(e -> buttonActions[finalI].run());

            gbc.insets = new Insets(15, 100, 15, 100);
            contentPanel.add(button, gbc);
        }

        // Instructions panel with modern styling
        JPanel instructionPanel = createStyledPanel("🎮 CONTROLS");
        instructionPanel.setLayout(new GridLayout(0, 1, 5, 5));

        String[] controls = {
                "Player 1: WASD or Mouse Movement",
                "Player 2 (Two Players): Arrow Keys",
                "P / SPACE: Pause Game | R: Restart Game",
                "M: Toggle Mouse Control | ESC: Return to Menu"
        };

        for (String control : controls) {
            JLabel controlLabel = new JLabel(control, SwingConstants.CENTER);
            controlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            controlLabel.setForeground(new Color(240, 240, 240));
            instructionPanel.add(controlLabel);
        }

        gbc.insets = new Insets(30, 100, 10, 100);
        contentPanel.add(instructionPanel, gbc);

        // Version info with better styling
        JPanel versionPanel = new JPanel(new BorderLayout());
        versionPanel.setOpaque(false);
        versionPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel versionLabel = new JLabel(
                "<html><div style='text-align: center; color: #A9A9A9;'>" +
                        "CS304 Computer Graphics - Final Project<br>" +
                        "<span style='color: #DAA520;'>Cairo University - Faculty of Computers and AI</span>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionPanel.add(versionLabel, BorderLayout.CENTER);

        contentPanel.add(versionPanel, gbc);

        mainPanel.add(layeredPane, "MENU");
        mainFrame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainFrame.setVisible(true);
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 35, 50, 220));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 195, 0, 100), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        if (title != null) {
            JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(new Color(255, 215, 0));
            panel.add(titleLabel, BorderLayout.NORTH);
        }

        return panel;
    }

    private void showDifficultySelection(boolean singlePlayer) {
        JPanel diffPanel = new JPanel(new GridBagLayout());
        diffPanel.setBackground(new Color(20, 25, 40));

        // Try to add background
        try {
            ImageIcon bg = new ImageIcon(getClass().getResource("/images/table.png"));
            if (bg != null && bg.getImage() != null) {
                JLabel bgLabel = new JLabel(new ImageIcon(
                        bg.getImage().getScaledInstance(
                                Constants.WINDOW_WIDTH,
                                Constants.WINDOW_HEIGHT,
                                Image.SCALE_SMOOTH
                        )
                ));
                diffPanel = new JPanel(new GridBagLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (bg.getImage() != null) {
                            g.drawImage(bg.getImage(), 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };
            }
        } catch (Exception e) {
            // Use default background
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        // Title with styling
        JLabel titleLabel = new JLabel("SELECT DIFFICULTY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Impact", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        gbc.insets = new Insets(30, 50, 40, 50);
        diffPanel.add(titleLabel, gbc);

        String mode = singlePlayer ? "SINGLE PLAYER" : "TWO PLAYERS";
        JLabel modeLabel = new JLabel("Mode: " + mode, SwingConstants.CENTER);
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        modeLabel.setForeground(new Color(55, 55, 60));
        diffPanel.add(modeLabel, gbc);

        // Difficulty buttons with icons
        String[] difficulties = {"EASY", "MEDIUM", "HARD", "EXPERT"};
        String[] difficultyIcons = {"😊", "😐", "😠", "👿"};
        Color[] colors = {
                new Color(60, 179, 113),   // Medium Sea Green - Easy
                new Color(30, 144, 255),   // Dodger Blue - Medium
                new Color(255, 140, 0),    // Dark Orange - Hard
                new Color(220, 20, 60)     // Crimson - Expert
        };

        for (int i = 0; i < difficulties.length; i++) {
            int diff = i + 1;
            JButton diffBtn = createDifficultyButton(
                    difficultyIcons[i] + " " + difficulties[i],
                    colors[i]
            );
            diffBtn.addActionListener(e -> {
                selectedDifficulty = diff;
                startGame(singlePlayer ?
                        GameController.GameMode.SINGLE_PLAYER :
                        GameController.GameMode.TWO_PLAYERS);
            });

            gbc.insets = new Insets(10, 150, 10, 150);
            diffPanel.add(diffBtn, gbc);
        }

        // Back button
        JButton backBtn = createModernButton("← BACK", new Color(58, 56, 56));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        gbc.insets = new Insets(30, 150, 10, 150);
        diffPanel.add(backBtn, gbc);

        mainPanel.add(diffPanel, "DIFFICULTY");
        cardLayout.show(mainPanel, "DIFFICULTY");
    }

    private void showSettings() {
        if (settingsPanel == null) {
            settingsPanel = new SettingsPanel(() -> cardLayout.show(mainPanel, "MENU"));
            mainPanel.add(settingsPanel, "SETTINGS");
        }
        cardLayout.show(mainPanel, "SETTINGS");
    }

    private void startGame(GameController.GameMode mode) {
        // Get settings if available
        if (settingsPanel != null) {
            selectedDifficulty = settingsPanel.getDifficulty();
            selectedTheme = settingsPanel.getTheme();
        }

        // Stop any existing game
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // Create new game
        gameController = new GameController(mode, selectedDifficulty, selectedTheme);
        gameRenderer = new GameRenderer(gameController);

        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(gameRenderer, BorderLayout.CENTER);

        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");

        // Setup input listeners
        setupInputListeners();

        // Start game loop
        startGameLoop();

        gameActive = true;
        gameRenderer.requestFocusInWindow();
    }

    private void setupInputListeners() {
        // Keyboard input
        gameRenderer.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                gameController.keyPressed(e.getKeyCode());

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    if (!gameController.isGameRunning() || gameController.isGamePaused()) {
                        stopGame();
                        cardLayout.show(mainPanel, "MENU");
                    }
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                gameController.keyReleased(e.getKeyCode());
            }
        });

        // Mouse input
        gameRenderer.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                gameController.mouseMoved(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                gameController.mouseMoved(e.getX(), e.getY());
            }
        });

        gameRenderer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                gameController.mousePressed(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                gameController.mouseReleased();
            }
        });
    }

    private void startGameLoop() {
        gameTimer = new Timer(16, e -> { // ~60 FPS
            if (!gameActive) return;

            gameController.update();
            gameRenderer.repaint();

            if (!gameController.isGameRunning()) {
                // Game over state
                // Player can press R to restart or ESC to go to menu
            }
        });

        gameTimer.start();
    }

    private void stopGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameActive = false;

        // Dispose game controller resources
        if (gameController != null) {
            gameController.dispose();
        }
    }

    private JButton createModernButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Rounded rectangle background
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor = getModel().isPressed() ?
                        baseColor.darker().darker() :
                        (getModel().isRollover() ? baseColor.brighter() : baseColor);

                // Draw gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, currentColor,
                        0, getHeight(), currentColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Draw border
                g2d.setColor(currentColor.brighter().brighter());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 25, 25);

                g2d.dispose();

                // Draw text
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        return button;
    }

    private JButton createDifficultyButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Rounded rectangle with shadow effect
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 30, 30);

                Color currentColor = getModel().isPressed() ?
                        baseColor.darker() :
                        (getModel().isRollover() ? baseColor.brighter() : baseColor);

                // Draw main button
                GradientPaint gradient = new GradientPaint(
                        0, 0, currentColor,
                        0, getHeight(), currentColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Draw highlight
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight()/2, 30, 30);

                // Draw border
                g2d.setColor(currentColor.brighter().brighter());
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 28, 28);

                g2d.dispose();

                // Draw text
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI Black", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        return button;
    }

    public void start() {
        mainFrame.setVisible(true);
    }

    // Static method to start game directly
    public static void startGameDirectly() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GameEngine engine = new GameEngine();
            engine.start();
        });
    }
}