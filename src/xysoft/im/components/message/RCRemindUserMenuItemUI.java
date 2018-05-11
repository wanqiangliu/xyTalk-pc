package xysoft.im.components.message;

import xysoft.im.components.Colors;
import xysoft.im.components.RCMenuItemUI;
import xysoft.im.utils.FontUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;


public class RCRemindUserMenuItemUI extends BasicMenuItemUI
{
    private int width;
    private int height;

    public RCRemindUserMenuItemUI()
    {
        this(100, 30);
    }

    public RCRemindUserMenuItemUI(int width, int height)
    {

        this.width = width;
        this.height = height;
    }

    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);
        c.setPreferredSize(new Dimension(width, height));
        c.setBackground(Colors.FONT_WHITE);
        c.setFont(FontUtil.getDefaultFont(12));
        selectionForeground = Colors.FONT_BLACK;
        selectionBackground = Colors.SCROLL_BAR_TRACK_LIGHT;
        c.setBorder(null);
    }


    @Override
    protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text)
    {
        g.setColor(Colors.FONT_BLACK);
        Rectangle newRect =  new Rectangle(28, textRect.y, textRect.width, textRect.height);
        super.paintText(g, menuItem, newRect, text);
    }
}
