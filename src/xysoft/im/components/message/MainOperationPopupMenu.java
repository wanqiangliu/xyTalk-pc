package xysoft.im.components.message;

import xysoft.im.components.Colors;
import xysoft.im.components.RCMainOperationMenuItemUI;
import xysoft.im.frames.CreateGroupDialog;
import xysoft.im.frames.MainFrame;
import xysoft.im.frames.SystemConfigDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


public class MainOperationPopupMenu extends JPopupMenu
{
    public MainOperationPopupMenu()
    {
        initMenuItem();
    }

    private void initMenuItem()
    {
        JMenuItem item1 = new JMenuItem("创建群聊");
        JMenuItem item2 = new JMenuItem("设置");

        item1.setUI(new RCMainOperationMenuItemUI());
        item1.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                showCreateGroupDialog();
            }
        });
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/image/chat.png"));
        icon1.setImage(icon1.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        item1.setIcon(icon1);
        item1.setIconTextGap(5);


        item2.setUI(new RCMainOperationMenuItemUI());
        item2.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //System.out.println("系统设置");
                SystemConfigDialog dialog = new SystemConfigDialog(MainFrame.getContext(), true);
                dialog.setVisible(true);
            }
        });
        ImageIcon icon2 = new ImageIcon(getClass().getResource("/image/setting.png"));
        icon2.setImage(icon2.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        item2.setIcon(icon2);
        item2.setIconTextGap(5);


        this.add(item1);
        this.add(item2);

        setBorder(new LineBorder(Colors.SCROLL_BAR_TRACK_LIGHT));
        setBackground(Colors.FONT_WHITE);
    }

    /**
     * 弹出创建群聊窗口
     */
    private void showCreateGroupDialog()
    {
        CreateGroupDialog dialog = new CreateGroupDialog(null, true);
        dialog.setVisible(true);

        /*ShadowBorderDialog shadowBorderDialog = new ShadowBorderDialog(MainFrame.getContext(), true, dialog);
        shadowBorderDialog.setVisible(true);*/
    }
}
