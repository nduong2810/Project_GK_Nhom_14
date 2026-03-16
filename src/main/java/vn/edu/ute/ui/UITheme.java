package vn.edu.ute.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Lớp UITheme hoạt động như một hệ thống "Design Token" tập trung cho toàn bộ giao diện người dùng.
 * Nó cung cấp các hằng số cho màu sắc, font chữ, kích thước và các phương thức factory
 * để tạo ra các thành phần Swing đã được định dạng sẵn, đảm bảo sự nhất quán trong toàn bộ ứng dụng.
 */
public final class UITheme {
    private UITheme() {
    }

    // ==================== BẢNG MÀU (COLOR PALETTE) ====================

    // Màu chính (Xanh dương)
    public static final Color PRIMARY = new Color(41, 98, 255);
    public static final Color PRIMARY_HOVER = new Color(28, 78, 216); // Đậm hơn cho hiệu ứng hover
    public static final Color PRIMARY_LIGHT = new Color(227, 236, 255); // Nhạt hơn cho nền hoặc highlight
    public static final Color PRIMARY_VERY_LIGHT = new Color(240, 245, 255);

    // Màu thành công (Xanh lá)
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color SUCCESS_HOVER = new Color(18, 138, 62);
    public static final Color SUCCESS_LIGHT = new Color(220, 252, 231);

    // Màu cảnh báo (Vàng cam)
    public static final Color WARNING = new Color(234, 159, 12);
    public static final Color WARNING_HOVER = new Color(202, 138, 8);
    public static final Color WARNING_LIGHT = new Color(254, 243, 199);

    // Màu nguy hiểm/lỗi (Đỏ)
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color DANGER_HOVER = new Color(185, 28, 28);
    public static final Color DANGER_LIGHT = new Color(254, 226, 226);

    // Màu trung tính (Xám)
    public static final Color NEUTRAL_50 = new Color(249, 250, 251);
    public static final Color NEUTRAL_100 = new Color(243, 244, 246);
    public static final Color NEUTRAL_200 = new Color(229, 231, 235);
    public static final Color NEUTRAL_300 = new Color(209, 213, 219);
    public static final Color NEUTRAL_400 = new Color(156, 163, 175); // Dùng cho placeholder text
    public static final Color NEUTRAL_500 = new Color(107, 114, 128);
    public static final Color NEUTRAL_600 = new Color(75, 85, 99);
    public static final Color NEUTRAL_700 = new Color(55, 65, 81);
    public static final Color NEUTRAL_800 = new Color(31, 41, 55);
    public static final Color NEUTRAL_900 = new Color(17, 24, 39);

    // Màu nền
    public static final Color BG_MAIN = new Color(246, 248, 252); // Nền chính của các panel
    public static final Color BG_CARD = Color.WHITE; // Nền cho các "thẻ"
    public static final Color BG_HEADER = new Color(30, 58, 138); // Nền cho header chính
    public static final Color BG_HEADER_LIGHT = new Color(37, 73, 170);

    // Màu cho bảng (Table)
    public static final Color TABLE_HEADER_BG = new Color(241, 245, 249);
    public static final Color TABLE_HEADER_FG = NEUTRAL_700;
    public static final Color TABLE_ALT_ROW = new Color(248, 250, 252); // Màu cho hàng xen kẽ
    public static final Color TABLE_SELECTION_BG = new Color(219, 234, 254);
    public static final Color TABLE_SELECTION_FG = NEUTRAL_900;
    public static final Color TABLE_GRID = new Color(226, 232, 240);

    // ==================== FONT CHỮ ====================

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUB_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_TABLE_BODY = new Font("Segoe UI", Font.PLAIN, 12);

    // ==================== KÍCH THƯỚC ====================

    public static final int BUTTON_HEIGHT = 32;
    public static final int FIELD_HEIGHT = 32;
    public static final int TABLE_ROW_HEIGHT = 30;
    public static final int TOOLBAR_PADDING = 8;
    public static final int PANEL_PADDING = 12;
    public static final int BORDER_RADIUS = 8;

    // ==================== PHƯƠNG THỨC FACTORY ĐỂ TẠO COMPONENT ====================

    /** Tạo nút chính (màu xanh dương). */
    public static JButton createPrimaryButton(String text, String icon) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER, Color.WHITE);
    }

    /** Tạo nút thành công (màu xanh lá). */
    public static JButton createSuccessButton(String text, String icon) {
        return createStyledButton(text, SUCCESS, SUCCESS_HOVER, Color.WHITE);
    }

    /** Tạo nút cảnh báo (màu vàng cam). */
    public static JButton createWarningButton(String text, String icon) {
        return createStyledButton(text, WARNING, WARNING_HOVER, Color.WHITE);
    }

    /** Tạo nút nguy hiểm/lỗi (màu đỏ). */
    public static JButton createDangerButton(String text, String icon) {
        return createStyledButton(text, DANGER, DANGER_HOVER, Color.WHITE);
    }

    /** Tạo nút trung tính (màu xám). */
    public static JButton createNeutralButton(String text, String icon) {
        return createStyledButton(text, NEUTRAL_500, NEUTRAL_600, Color.WHITE);
    }

    /**
     * Tạo nút có viền (outline), không có màu nền. Thường dùng cho các hành động phụ.
     */
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(4, 14, 4, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 0, 0, 0));
        SwingUtilities.invokeLater(() -> {
            Dimension pref = btn.getPreferredSize();
            btn.setPreferredSize(new Dimension(Math.max(pref.width, 80), BUTTON_HEIGHT));
        });
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_LIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    /**
     * Phương thức private để tạo một nút đã được định dạng cơ bản với hiệu ứng hover.
     */
    private static JButton createStyledButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 0, 0, 0));
        SwingUtilities.invokeLater(() -> {
            Dimension pref = btn.getPreferredSize();
            btn.setPreferredSize(new Dimension(Math.max(pref.width, 80), BUTTON_HEIGHT));
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    /**
     * Tạo một JTextField với văn bản gợi ý (placeholder) màu xám khi trống và không được focus.
     */
    public static JTextField createSearchField(String placeholder, int columns) {
        JTextField field = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(NEUTRAL_400);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 1);
                    g2.dispose();
                }
            }
        };
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_300, 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, FIELD_HEIGHT));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7)));
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(NEUTRAL_300, 1, true),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                field.repaint();
            }
        });
        return field;
    }

    /**
     * Tạo một JLabel được định dạng để dùng làm nhãn cho các trường trong form.
     */
    public static JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY_BOLD);
        label.setForeground(NEUTRAL_700);
        return label;
    }

    /**
     * Tạo một JLabel được định dạng để dùng làm tiêu đề cho một khu vực (section).
     */
    public static JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUB_HEADER);
        label.setForeground(NEUTRAL_800);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return label;
    }

    /**
     * Áp dụng các định dạng cho một JTable (màu xen kẽ, header, v.v.).
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE_BODY);
        table.setRowHeight(TABLE_ROW_HEIGHT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setSelectionForeground(TABLE_SELECTION_FG);
        table.setGridColor(TABLE_GRID);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        // Định dạng màu xen kẽ cho các hàng
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        // Định dạng cho header của bảng
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, NEUTRAL_200));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * Tạo một JScrollPane chứa một JTable đã được định dạng.
     */
    public static JScrollPane createStyledScrollPane(JTable table) {
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(NEUTRAL_200, 1, true));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    /**
     * Tạo một JPanel được định dạng để dùng làm thanh công cụ (toolbar).
     */
    public static JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, TOOLBAR_PADDING, 0));
        return toolbar;
    }

    /**
     * Áp dụng định dạng cho một panel chính (nền, padding).
     */
    public static void applyPanelStyle(JPanel panel) {
        panel.setBackground(BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
    }

    /**
     * Áp dụng định dạng cho một JDialog (nền, padding).
     */
    public static void styleDialog(JDialog dialog) {
        dialog.getContentPane().setBackground(BG_MAIN);
        ((JPanel) dialog.getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(16, 20, 16, 20));
    }

    /**
     * Tạo titled border hiện đại.
     */
    public static Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(NEUTRAL_200, 1, true),
                " " + title + " ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                FONT_BODY_BOLD,
                NEUTRAL_600);
    }

    /**
     * Tạo top panel chứa toolbar bên trái và search bên phải.
     */
    public static JPanel createTopPanel(JPanel leftToolbar, JPanel rightPanel) {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        topPanel.add(leftToolbar, BorderLayout.WEST);
        if (rightPanel != null) {
            topPanel.add(rightPanel, BorderLayout.EAST);
        }
        return topPanel;
    }

    /**
     * Tạo search panel bên phải toolbar.
     */
    public static JPanel createSearchPanel(JTextField searchField) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Tìm:");
        searchLabel.setFont(FONT_BODY_BOLD);
        searchLabel.setForeground(NEUTRAL_600);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        return searchPanel;
    }

    /**
     * Tạo card panel (white background, viền nhẹ, bo góc).
     */
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEUTRAL_200, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        return card;
    }

    /**
     * Tạo separator ngang đẹp.
     */
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(NEUTRAL_200);
        return sep;
    }

    /**
     * Tạo header panel gradient cho MainFrame.
     */
    public static JPanel createGradientHeader() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(
                        0, 0, BG_HEADER,
                        getWidth(), 0, BG_HEADER_LIGHT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
    }
}
