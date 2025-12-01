import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Gobang extends JFrame {
    // 棋盘参数
    private static final int BOARD_SIZE = 15; // 15x15的棋盘
    private static final int CELL_SIZE = 40;  // 每个格子的像素大小
    private static final int MARGIN = 50;     // 边距
    
    // 游戏状态
    private int[][] board = new int[BOARD_SIZE][BOARD_SIZE]; // 0:空, 1:黑子, 2:白子
    private boolean isBlackTurn = true; // 当前轮到黑子
    private boolean gameOver = false;
    private List<Point> history = new ArrayList<>(); // 历史记录
    
    // UI组件
    private JPanel chessBoard;
    private JLabel statusLabel;
    private JButton restartButton;
    private JButton undoButton;

    public Gobang() {
        initComponents();
        initGame();
    }

    private void initComponents() {
        setTitle("五子棋");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // 棋盘面板
        chessBoard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawPieces(g);
            }
            
            @Override
            public Dimension getPreferredSize() {
                int size = (BOARD_SIZE - 1) * CELL_SIZE + 2 * MARGIN;
                return new Dimension(size, size);
            }
        };
        
        chessBoard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver) return;
                
                int x = e.getX();
                int y = e.getY();
                
                // 转换为棋盘坐标
                int col = Math.round((float)(x - MARGIN) / CELL_SIZE);
                int row = Math.round((float)(y - MARGIN) / CELL_SIZE);
                
                // 检查是否在棋盘范围内
                if (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE) {
                    placePiece(row, col);
                }
            }
        });
        
        // 状态面板
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        statusLabel = new JLabel("黑方回合");
        statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        
        restartButton = new JButton("重新开始");
        restartButton.addActionListener(e -> restartGame());
        
        undoButton = new JButton("悔棋");
        undoButton.addActionListener(e -> undoMove());
        
        controlPanel.add(statusLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(restartButton);
        controlPanel.add(undoButton);
        
        mainPanel.add(chessBoard, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void initGame() {
        // 初始化棋盘
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = 0;
            }
        }
        history.clear();
        isBlackTurn = true;
        gameOver = false;
        updateStatus();
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 设置背景色
        g2d.setColor(new Color(222, 184, 135));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // 画棋盘网格
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < BOARD_SIZE; i++) {
            // 横线
            int y = MARGIN + i * CELL_SIZE;
            g2d.drawLine(MARGIN, y, MARGIN + (BOARD_SIZE - 1) * CELL_SIZE, y);
            
            // 竖线
            int x = MARGIN + i * CELL_SIZE;
            g2d.drawLine(x, MARGIN, x, MARGIN + (BOARD_SIZE - 1) * CELL_SIZE);
        }
        
        // 画棋盘上的五个黑点（天元和星）
        int[][] dots = {
            {3, 3}, {3, 11}, {7, 7}, {11, 3}, {11, 11}
        };
        g2d.setColor(Color.BLACK);
        for (int[] dot : dots) {
            int x = MARGIN + dot[0] * CELL_SIZE;
            int y = MARGIN + dot[1] * CELL_SIZE;
            g2d.fillOval(x - 4, y - 4, 8, 8);
        }
    }

    private void drawPieces(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == 1) { // 黑子
                    drawBlackPiece(g2d, j, i);
                } else if (board[i][j] == 2) { // 白子
                    drawWhitePiece(g2d, j, i);
                }
            }
        }
    }

    private void drawBlackPiece(Graphics2D g, int x, int y) {
        int centerX = MARGIN + x * CELL_SIZE;
        int centerY = MARGIN + y * CELL_SIZE;
        
        // 渐变效果
        GradientPaint gradient = new GradientPaint(
            centerX - 15, centerY - 15, Color.BLACK,
            centerX + 10, centerY + 10, Color.GRAY
        );
        g.setPaint(gradient);
        g.fillOval(centerX - 16, centerY - 16, 32, 32);
        
        g.setColor(Color.BLACK);
        g.drawOval(centerX - 16, centerY - 16, 32, 32);
    }

    private void drawWhitePiece(Graphics2D g, int x, int y) {
        int centerX = MARGIN + x * CELL_SIZE;
        int centerY = MARGIN + y * CELL_SIZE;
        
        // 渐变效果
        GradientPaint gradient = new GradientPaint(
            centerX - 15, centerY - 15, Color.WHITE,
            centerX + 10, centerY + 10, Color.LIGHT_GRAY
        );
        g.setPaint(gradient);
        g.fillOval(centerX - 16, centerY - 16, 32, 32);
        
        g.setColor(Color.DARK_GRAY);
        g.drawOval(centerX - 16, centerY - 16, 32, 32);
    }

    private void placePiece(int row, int col) {
        // 检查位置是否已有棋子
        if (board[row][col] != 0) {
            JOptionPane.showMessageDialog(this, "此处已有棋子！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 放置棋子
        board[row][col] = isBlackTurn ? 1 : 2;
        history.add(new Point(col, row));
        
        // 检查是否获胜
        if (checkWin(row, col)) {
            gameOver = true;
            String winner = isBlackTurn ? "黑方" : "白方";
            JOptionPane.showMessageDialog(this, winner + "获胜！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            updateStatus();
            return;
        }
        
        // 检查是否平局
        if (history.size() == BOARD_SIZE * BOARD_SIZE) {
            gameOver = true;
            JOptionPane.showMessageDialog(this, "平局！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
            updateStatus();
            return;
        }
        
        // 切换回合
        isBlackTurn = !isBlackTurn;
        updateStatus();
        chessBoard.repaint();
    }

    private boolean checkWin(int row, int col) {
        int currentPlayer = board[row][col];
        int[][] directions = {
            {1, 0},  // 水平
            {0, 1},  // 垂直
            {1, 1},  // 对角线
            {1, -1}  // 反对角线
        };
        
        for (int[] dir : directions) {
            int count = 1; // 当前位置已经有1个棋子
            
            // 正向计数
            int r = row + dir[0];
            int c = col + dir[1];
            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == currentPlayer) {
                count++;
                r += dir[0];
                c += dir[1];
            }
            
            // 反向计数
            r = row - dir[0];
            c = col - dir[1];
            while (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE && board[r][c] == currentPlayer) {
                count++;
                r -= dir[0];
                c -= dir[1];
            }
            
            if (count >= 5) {
                return true;
            }
        }
        
        return false;
    }

    private void undoMove() {
        if (history.isEmpty() || gameOver) {
            return;
        }
        
        Point lastMove = history.remove(history.size() - 1);
        board[lastMove.y][lastMove.x] = 0;
        isBlackTurn = !isBlackTurn;
        updateStatus();
        chessBoard.repaint();
    }

    private void restartGame() {
        int result = JOptionPane.showConfirmDialog(this, "确定要重新开始游戏吗？", "重新开始", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            initGame();
            chessBoard.repaint();
        }
    }

    private void updateStatus() {
        if (gameOver) {
            statusLabel.setText("游戏结束 - " + (isBlackTurn ? "黑方" : "白方") + "获胜");
        } else {
            statusLabel.setText(isBlackTurn ? "黑方回合" : "白方回合");
        }
        statusLabel.setForeground(isBlackTurn ? Color.BLACK : Color.RED);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            Gobang game = new Gobang();
            game.setVisible(true);
        });
    }
}