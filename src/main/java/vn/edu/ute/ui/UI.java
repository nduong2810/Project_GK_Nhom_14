package vn.edu.ute.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

/**
 * Khởi tạo Look & Feel và cài đặt global UI defaults.
 */
public final class UI {
    private UI() {
    }

    public static void initLookAndFeel() {
        try {
            // Cài đặt FlatLaf Light theme
            FlatLightLaf.setup();

            // Global UI defaults
            UIManager.put("defaultFont", UITheme.FONT_BODY);

            // Button styling
            UIManager.put("Button.arc", UITheme.BORDER_RADIUS);

            // TextField styling
            UIManager.put("TextField.arc", 6);
            UIManager.put("PasswordField.arc", 6);
            UIManager.put("ComboBox.arc", 6);

            // TabbedPane
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("TabbedPane.hoverColor", UITheme.PRIMARY_LIGHT);
            UIManager.put("TabbedPane.focusColor", UITheme.PRIMARY);
            UIManager.put("TabbedPane.underlineColor", UITheme.PRIMARY);
            UIManager.put("TabbedPane.tabHeight", 36);

            // ScrollBar
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);

            // Table
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);

            // OptionPane
            UIManager.put("OptionPane.messageFont", UITheme.FONT_BODY);
            UIManager.put("OptionPane.buttonFont", UITheme.FONT_BUTTON);

        } catch (Exception e) {
            // Fallback to system L&F
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
    }
}
