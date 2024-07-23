import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameUI extends JFrame {
    private int tileWidth;
    private int tileHeight;
    private int cellWidth;
    private int cellHeight;

    private Color lightTileHidden = new Color(171,215, 83);
    private Color darkTileHidden = new Color(163,208, 73);
    private Color lightTileVisible = new Color(229,194, 158);
    private Color darkTileVisible = new Color(215,185, 152);

    private AtomicInteger flagCount;
    private AtomicInteger clockCount = new AtomicInteger(0);
    private int bestScore = 0;

    private JPanel controlPanel;
    private JPanel boardPanel;
    private JLabel flagLabel;
    private JLabel clockLabel;

    private ImageIcon flagPanelIcon;
    private ImageIcon clockPanelIcon;
    private ImageIcon flagTileIcon;
    private ImageIcon bombTileIcon;

    private Timer timer;

    public GameUI(int tileWidth, int tileHeight, int cellWidth, int cellHeight, int bombCount) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.flagCount = new AtomicInteger(bombCount);

        SwingUtilities.invokeLater(() -> {
            setTitle("MinesweeperRedux");
            setResizable(false);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Initializing Icons
            flagPanelIcon = new ImageIcon(((new ImageIcon("resources/flag.png")).getImage()).getScaledInstance(
                    cellWidth * 2, cellHeight * 2, Image.SCALE_SMOOTH));
            clockPanelIcon = new ImageIcon(((new ImageIcon("resources/clock.png")).getImage()).getScaledInstance(
                    (int)(cellWidth * 1.75), (int)(cellHeight * 1.75), Image.SCALE_SMOOTH));
            flagTileIcon = new ImageIcon(((new ImageIcon("resources/flag.png")).getImage()).getScaledInstance(
                    cellWidth, cellHeight, Image.SCALE_SMOOTH));
            bombTileIcon = new ImageIcon(((new ImageIcon("resources/bomb.png")).getImage()).getScaledInstance(
                    cellWidth, cellHeight, Image.SCALE_SMOOTH));

            // Creates controlPanel
            controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
            controlPanel.setPreferredSize(new Dimension(tileWidth * cellWidth, 2 * cellHeight));
            controlPanel.setBackground(new Color(74, 117, 44));

            // Adds Elements to controlPanel
            flagLabel = new JLabel();
            flagLabel.setIcon(flagPanelIcon);
            flagLabel.setForeground(new Color(250, 250, 250));
            flagLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));

            clockLabel = new JLabel();
            clockLabel.setIcon(clockPanelIcon);
            clockLabel.setForeground(new Color(250, 250, 250));
            clockLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));

            timer = new Timer(1000, e -> {
                if(clockCount.get() == 999) {
                    timer.stop();
                } else {
                    clockCount.incrementAndGet();
                    updateControlPanel(flagCount.get());
                }
            });

            // Creates center Board Panel
            boardPanel = new JPanel(new GridLayout(tileHeight, tileWidth, 0, 0));
            boardPanel.setPreferredSize(new Dimension(tileWidth * cellWidth, tileHeight * cellHeight));
            boardPanel.setBackground(new Color(74, 117, 44));
            boardPanel.addMouseListener(new BoardAdapter());

            add(controlPanel, BorderLayout.NORTH);
            add(boardPanel, BorderLayout.CENTER);

            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        });
    }

    public void updateControlPanel(int newFlagCount) {
        flagCount = new AtomicInteger(newFlagCount);

        controlPanel.removeAll();
        controlPanel.repaint();

        flagLabel.setText(String.format("%02d", flagCount.intValue()));
        clockLabel.setText(String.format("%03d", clockCount.intValue()));

        controlPanel.add(flagLabel);
        controlPanel.add(clockLabel);

        controlPanel.revalidate();
    }

    public void updateBoardPanel(Tile[] updatedBoard) {
        boardPanel.removeAll();
        boardPanel.repaint();

        for(Tile tile: updatedBoard) {
            JLabel tempLabel = new JLabel("", SwingConstants.CENTER);
            tempLabel.setOpaque(true);

            switch(tile.type){
                case lightHidden -> tempLabel.setBackground(lightTileHidden);
                case darkHidden -> tempLabel.setBackground(darkTileHidden);
                case lightVisible -> tempLabel.setBackground(lightTileVisible);
                case darkVisible -> tempLabel.setBackground(darkTileVisible);
            }

            tempLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

            switch(tile.content) {
                case EMPTY -> {}
                case FLAG -> tempLabel.setIcon(flagTileIcon);
                case BOMB -> tempLabel.setIcon(bombTileIcon);
                case ONE -> {
                    tempLabel.setText("1");
                    tempLabel.setForeground(Color.BLUE);
                }
                case TWO -> {
                    tempLabel.setText("2");
                    tempLabel.setForeground(new Color(0, 73, 32));
                }
                case THREE -> {
                    tempLabel.setText("3");
                    tempLabel.setForeground(Color.RED);
                }
                case FOUR -> {
                    tempLabel.setText("4");
                    tempLabel.setForeground(new Color(139, 0, 139));
                }
                case FIVE -> {
                    tempLabel.setText("5");
                    tempLabel.setForeground(new Color(128, 0 ,0));
                }
                case SIX -> {
                    tempLabel.setText("6");
                    tempLabel.setForeground(new Color(0, 200, 200));
                }
                case SEVEN -> {
                    tempLabel.setText("7");
                    tempLabel.setForeground(new Color(102, 51, 153));
                }
                case EIGHT -> {
                    tempLabel.setText("8");
                    tempLabel.setForeground(Color.GRAY);
                }
            }

            boardPanel.add(tempLabel);
        }

        boardPanel.revalidate();
    }

    public void handleBegin() {
        clockCount.set(0);
        timer.start();
    }

    public void handleEnd(boolean isWin, Tile[] updatedBoard) {
        timer.stop();

        if(bestScore < clockCount.get()) bestScore = clockCount.get();

        updateControlPanel(flagCount.get());
        updateBoardPanel(updatedBoard);

        /*
        boardPanel.removeAll();
        boardPanel.repaint();

        if(isWin) {
            JLabel tempLabel = new JLabel("Win", SwingConstants.CENTER);
            tempLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

            boardPanel.add(tempLabel);
        } else {
            JLabel tempLabel = new JLabel("Lose", SwingConstants.CENTER);
            tempLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));

            boardPanel.add(tempLabel);
        }

        boardPanel.revalidate();
        */


        /* TODO:
            if(win) Change board to water without tile details (just grass and water no flags or numbers)
            else(loss) All bomb spots (leave everything as is except reveal all bombs without flag)

           TODO:
            Show end screen with clockCount and bestScore with restart button on bottom
            if(win) score is clockCount
            else(loss) score is ---
        */
    }
}
