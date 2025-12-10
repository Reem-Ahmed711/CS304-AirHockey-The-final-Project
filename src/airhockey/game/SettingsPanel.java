package airhockey.game;

import airhockey.utils.Constants;
import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JComboBox<String> difficultyCombo;
    private JComboBox<String> themeCombo;
    private JCheckBox soundCheck;
    private JCheckBox musicCheck;
    private JSlider volumeSlider;
    private JButton backButton;

    private Runnable onBack;
    private JLabel backgroundLabel; // أضف هذا المتغير

    public SettingsPanel(Runnable onBack) {
        this.onBack = onBack;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Try to load background
        try {
            // Use a simpler approach without custom layout
            ImageIcon bg = null;
            try {
                bg = new ImageIcon(getClass().getResource("/images/blackboard_1205-357.jpg"));
            } catch (Exception e1) {
                try {
                    bg = new ImageIcon(getClass().getResource("/images/back.png"));
                } catch (Exception e2) {
                    try {
                        bg = new ImageIcon(getClass().getResource("/images/bakgrounf.png"));
                    } catch (Exception e3) {
                        // Use gradient background
                    }
                }
            }

            if (bg != null && bg.getImage() != null) {
                // Create a panel with background image
                ImageIcon finalBg = bg;
                JPanel backgroundPanel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Image scaledImage = finalBg.getImage().getScaledInstance(
                                getWidth(), getHeight(), Image.SCALE_SMOOTH);
                        g.drawImage(scaledImage, 0, 0, this);
                    }
                };
                backgroundPanel.setLayout(new BorderLayout());

                JPanel contentPanel = createContentPanel();
                backgroundPanel.add(contentPanel, BorderLayout.CENTER);

                setLayout(new BorderLayout());
                add(backgroundPanel, BorderLayout.CENTER);
                return;
            }
        } catch (Exception e) {
            System.out.println("Background image error: " + e.getMessage());
        }

        // Use gradient background if no image
        setBackground(new Color(30, 35, 50));
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("⚙️ GAME SETTINGS", SwingConstants.CENTER);
        titleLabel.setFont(createFont("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // Settings panel with glass effect
        JPanel settingsPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();

                // Glass effect background
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(40, 45, 60, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Border
                g2d.setColor(new Color(255, 215, 0, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);

                g2d.dispose();
            }
        };

        settingsPanel.setOpaque(false);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);

        // Difficulty setting
        addSettingLabel("AI Difficulty:", settingsPanel, gbc);
        String[] difficulties = {"EASY 😊", "MEDIUM 😐", "HARD 😠", "EXPERT 👿"};
        difficultyCombo = createStyledComboBox(difficulties);
        difficultyCombo.setSelectedIndex(1);
        settingsPanel.add(difficultyCombo, gbc);

        // Theme setting
        addSettingLabel("Theme:", settingsPanel, gbc);
        String[] themes = {"CLASSIC 🏛️", "MODERN 🎮", "DARK 🌙", "ICE ❄️"};
        themeCombo = createStyledComboBox(themes);
        themeCombo.setSelectedIndex(1);
        settingsPanel.add(themeCombo, gbc);

        // Sound settings
        soundCheck = createStyledCheckBox("🔊 Enable Sound Effects", true);
        musicCheck = createStyledCheckBox("🎵 Enable Background Music", true);

        settingsPanel.add(soundCheck, gbc);
        settingsPanel.add(musicCheck, gbc);

        // Volume
        addSettingLabel("Volume:", settingsPanel, gbc);
        volumeSlider = createStyledSlider();
        settingsPanel.add(volumeSlider, gbc);

        // Back button
        backButton = createStyledButton("← BACK TO MENU");
        backButton.addActionListener(e -> onBack.run());

        // Add components
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(settingsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(backButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        return contentPanel;
    }

    private void addSettingLabel(String text, JPanel panel, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(createFont("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(255, 255, 255));
        panel.add(label, gbc);
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<String>(items) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Rounded background
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(60, 65, 80));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border
                g2d.setColor(new Color(255, 195, 0));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 15, 15);

                g2d.dispose();

                super.paintComponent(g);
            }
        };

        combo.setFont(createFont("Segoe UI", Font.PLAIN, 16));
        combo.setForeground(Color.WHITE);
        combo.setBackground(new Color(60, 65, 80));
        combo.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        combo.setFocusable(false);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setFont(createFont("Segoe UI", Font.PLAIN, 16));
                label.setForeground(isSelected ? Color.YELLOW : Color.WHITE);
                label.setBackground(isSelected ? new Color(80, 85, 100) : new Color(60, 65, 80));
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        return combo;
    }

    private JCheckBox createStyledCheckBox(String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text, selected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        checkBox.setFont(createFont("Segoe UI", Font.PLAIN, 16));
        checkBox.setForeground(Color.WHITE);
        checkBox.setBackground(new Color(40, 45, 60));
        checkBox.setFocusPainted(false);

        // Remove icon loading lines (149 و 150) لأنها تسبب مشاكل
        // checkBox.setIcon(new ImageIcon(getClass().getResource("/images/unnamed.jpg")));
        // checkBox.setSelectedIcon(new ImageIcon(getClass().getResource("/images/unnamed (2).png")));

        return checkBox;
    }

    private JSlider createStyledSlider() {
        JSlider slider = new JSlider(0, 100, 80) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
                g2d.dispose();
            }
        };

        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(new Color(40, 45, 60));
        slider.setForeground(Color.WHITE);
        slider.setFont(createFont("Segoe UI", Font.PLAIN, 12));

        // Customize slider UI (مبسطة بدون مشاكل)
        try {
            slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
                @Override
                public void paintTrack(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Rectangle trackBounds = trackRect;
                    if (trackBounds != null) {
                        int trackHeight = 8;
                        int trackY = trackBounds.y + (trackBounds.height - trackHeight) / 2;

                        // Track background
                        g2d.setColor(new Color(60, 65, 80));
                        g2d.fillRoundRect(trackBounds.x, trackY, trackBounds.width, trackHeight, 4, 4);

                        // Track fill
                        if (thumbRect != null) {
                            int fillWidth = thumbRect.x - trackBounds.x;
                            g2d.setColor(new Color(30, 144, 255));
                            g2d.fillRoundRect(trackBounds.x, trackY, fillWidth, trackHeight, 4, 4);
                        }

                        // Track border
                        g2d.setColor(new Color(255, 195, 0));
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawRoundRect(trackBounds.x, trackY, trackBounds.width, trackHeight, 4, 4);
                    }
                }

                @Override
                public void paintThumb(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Rectangle thumbBounds = thumbRect;
                    if (thumbBounds != null) {
                        int thumbSize = 20;
                        int thumbX = thumbBounds.x + (thumbBounds.width - thumbSize) / 2;
                        int thumbY = thumbBounds.y + (thumbBounds.height - thumbSize) / 2;

                        // Thumb gradient
                        GradientPaint gradient = new GradientPaint(
                                thumbX, thumbY, new Color(255, 215, 0),
                                thumbX + thumbSize, thumbY + thumbSize, new Color(255, 140, 0)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillOval(thumbX, thumbY, thumbSize, thumbSize);

                        // Thumb border
                        g2d.setColor(Color.WHITE);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawOval(thumbX, thumbY, thumbSize, thumbSize);
                    }
                }

                @Override
                public void paintFocus(Graphics g) {
                    // Do nothing to remove focus painting
                }
            });
        } catch (Exception e) {
            System.out.println("Slider UI error: " + e.getMessage());
            // Use default slider UI if custom fails
        }

        return slider;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Rounded rectangle with effects
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor = getModel().isPressed() ?
                        new Color(100, 100, 100) :
                        (getModel().isRollover() ? new Color(180, 180, 180) : new Color(150, 150, 150));

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(2, 2, getWidth(), getHeight(), 20, 20);

                // Main button
                GradientPaint gradient = new GradientPaint(
                        0, 0, currentColor,
                        0, getHeight(), currentColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Highlight
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight()/2, 20, 20);

                // Border
                g2d.setColor(currentColor.brighter());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);

                g2d.dispose();

                // Draw text
                super.paintComponent(g);
            }
        };

        button.setFont(createFont("Segoe UI", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));

        return button;
    }

    // Helper method to create fonts safely
    private Font createFont(String name, int style, int size) {
        try {
            return new Font(name, style, size);
        } catch (Exception e) {
            // Fallback to Arial
            if (name.contains("Segoe")) {
                return new Font("Arial", style, size);
            }
            return new Font("Arial", style, size);
        }
    }

    // Getters for settings
    public int getDifficulty() {
        return difficultyCombo.getSelectedIndex() + 1;
    }

    public Constants.Theme getTheme() {
        int index = themeCombo.getSelectedIndex();
        switch(index) {
            case 0: return Constants.Theme.CLASSIC;
            case 1: return Constants.Theme.MODERN;
            case 2: return Constants.Theme.DARK;
            case 3: return Constants.Theme.ICE;
            default: return Constants.Theme.MODERN;
        }
    }

    public boolean isSoundEnabled() {
        return soundCheck != null && soundCheck.isSelected();
    }

    public boolean isMusicEnabled() {
        return musicCheck != null && musicCheck.isSelected();
    }

    public int getVolume() {
        return volumeSlider != null ? volumeSlider.getValue() : 80;
    }
}