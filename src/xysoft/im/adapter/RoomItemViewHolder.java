package xysoft.im.adapter;

import xysoft.im.components.Colors;
import xysoft.im.components.GBC;
import xysoft.im.components.RCBorder;
import xysoft.im.utils.AvatarUtil;
import xysoft.im.utils.FontUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;


public class RoomItemViewHolder extends ViewHolder
{
    /**
	 * 原子单聊、群聊展示单元
	 */
	private static final long serialVersionUID = -7769321903603399638L;
	public JLabel avatar = new JLabel();
    public JLabel roomName = new JLabel();
    public JLabel brief = new JLabel();
    public JLabel time = new JLabel();
    public JLabel unreadCount = new JLabel();
    
    public JPanel timeUnread = new JPanel();
    public JPanel nameBrief = new JPanel();
    private Object tag;

    public RoomItemViewHolder()
    {
        initComponents();
        initView();

    }

    private void initComponents()
    {
        setPreferredSize(new Dimension(100, 64));
        setBackground(Colors.DARK);
        setBorder(new RCBorder(RCBorder.BOTTOM));
        setOpaque(true);
        setForeground(Colors.FONT_WHITE);

//        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/avatar.jpg"));
//        imageIcon.setImage(imageIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
//        avatar.setIcon(imageIcon);


        roomName.setFont(FontUtil.getDefaultFont(14));
        roomName.setForeground(Colors.FONT_WHITE);

        brief.setForeground(Colors.FONT_GRAY);
        brief.setFont(FontUtil.getDefaultFont(12));

        nameBrief.setLayout(new BorderLayout());
        nameBrief.setBackground(Colors.DARK);
        nameBrief.add(roomName, BorderLayout.NORTH);
        nameBrief.add(brief, BorderLayout.CENTER);

        time.setForeground(Colors.FONT_GRAY);
        time.setFont(FontUtil.getDefaultFont(12));

        unreadCount.setIcon(new ImageIcon(getClass().getResource("/image/count_bg.png")));
        unreadCount.setFont(FontUtil.getDefaultFont(12));
        unreadCount.setPreferredSize(new Dimension(10, 10));
        unreadCount.setForeground(Colors.FONT_WHITE);
        unreadCount.setHorizontalTextPosition(SwingConstants.CENTER);
        unreadCount.setHorizontalAlignment(SwingConstants.CENTER);
        unreadCount.setVerticalAlignment(SwingConstants.CENTER);
        unreadCount.setVerticalTextPosition(SwingConstants.CENTER);

        //时间和未读信息放置在一起
        timeUnread = new JPanel();
        timeUnread.setLayout(new BorderLayout());
        timeUnread.setBackground(Colors.DARK);
        timeUnread.add(time, BorderLayout.NORTH);
        timeUnread.add(unreadCount, BorderLayout.CENTER);

    }

    private void initView()
    {
        setLayout(new GridBagLayout());
        add(avatar, new GBC(0, 0).setWeight(2, 1).setFill(GBC.BOTH).setInsets(0, 5, 0, 0));
        add(nameBrief, new GBC(1, 0).setWeight(100, 1).setFill(GBC.BOTH).setInsets(5, 5, 0, 0));
        add(timeUnread, new GBC(2, 0).setWeight(1, 1).setFill(GBC.BOTH).setInsets(5, 0, 0, 0));
    }


    public Object getTag()
    {
        return tag;
    }

    public void setTag(Object tag)
    {
        this.tag = tag;
    }
}
