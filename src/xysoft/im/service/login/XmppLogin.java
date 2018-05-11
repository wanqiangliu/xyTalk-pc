package xysoft.im.service.login;

import java.io.IOException;
import java.net.InetAddress;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException.NotAMucServiceException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser.Invite;
import org.jivesoftware.smackx.muclight.MultiUserChatLightManager;
import org.jivesoftware.smackx.push_notifications.PushNotificationsManager;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import xysoft.im.app.Launcher;
import xysoft.im.cache.UserCache;
import xysoft.im.constant.Res;
import xysoft.im.listener.SessionManager;
import xysoft.im.service.ChatService;
import xysoft.im.service.ErrorMsgService;
import xysoft.im.service.FormService;
import xysoft.im.service.HeadlineChatService;
import xysoft.im.service.MucChatService;
import xysoft.im.service.ProviderRegister;
import xysoft.im.utils.DebugUtil;

public class XmppLogin implements Login {

	String username;
	String password;
	String tag;
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public XmppLogin() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String login() {
		
		if ( Launcher.IS_DEBUGMODE )
        {
            SmackConfiguration.DEBUG = true;
        }

        try
        {
        	XMPPTCPConnectionConfiguration conf = retrieveConnectionConfiguration();
        	if (conf==null){
        		return "XMPPTCPConnectionConfiguration is null";
        	}
        	
        	Launcher.connection = new XMPPTCPConnection( conf );
        	
            //连接监听       	
            SessionManager sessionManager = new SessionManager();
            Launcher.connection.addConnectionListener( sessionManager );
            
            //重连管理
            ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(Launcher.connection);
            reconnectionManager.setFixedDelay(Res.RE_CONNET_INTERVAL);//重联间隔
            reconnectionManager.enableAutomaticReconnection();//开启重联机制

            StanzaFilter filterMsg = new StanzaTypeFilter(Message.class);
            StanzaFilter filterIQ = new StanzaTypeFilter(IQ.class);
            //PacketCollector myCollector = Launcher.connection.createPacketCollector(filterMsg);        	
            StanzaListener listenerMsg = new StanzaListener() {

				@Override
				public void processStanza(Stanza stanza)
						throws NotConnectedException, InterruptedException, NotLoggedInException {
					DebugUtil.debug("processPacket-MSG:"+stanza.toString());
					if (stanza instanceof Message) {//消息包
	                    Message message = (Message) stanza;
	                    if (message.getType() == Message.Type.chat) {//单聊
	                    	if (message.getBody()==null){
		                    	DebugUtil.debug("(抛弃的)processPacket-Message.Type.chat:"+message.toString());	 	                    			                    		
	                    	}else{
		                    	ChatService.recivePacket(message);
		                    	DebugUtil.debug("processPacket-Message.Type.chat:"+message.toString());	 	                    		
	                    	}
	                    	
	                    }
	                    if (message.getType() == Message.Type.headline) {//重要消息
	                    	HeadlineChatService.recivePacket(message);
	                    	DebugUtil.debug("processPacket-Message.Type.headline:"+message.toString());
	                    }
	                    if (message.getType() == Message.Type.normal ) {
	                    	//可能是普通消息，也可能是回执消息，也可能是扩展消息
	                    	if (message.hasExtension("urn:xmpp:receipts")){	                    	
	                    		//回执消息
	                    		ChatService.receiptArrived(message);
	                    		DebugUtil.debug("urn:xmpp:receipts:"+message.toString());
	                    	}
	                    	else if (message.hasExtension("urn:xmpp:attention:0")){
	                    		//震动提醒消息
	                    		DebugUtil.debug("urn:xmpp:attention:0:"+message.toString());
	                    	}	                    	
	                    	else if (message.hasExtension("http://jabber.org/protocol/muc#user")){
	                    		//被邀请加入群聊
	                    		DebugUtil.debug("被邀请加入群聊:"+message.getFrom().toString());
	                    	}	   
	                    	else if (message.hasExtension("jabber:x:conference")){
	                    		//被邀请加入群聊
	                    		DebugUtil.debug("被邀请加入群聊:"+message.getFrom().toString());
	                    	}	   
	                    	else{
	                    		if (message.getBody()==null){
			                    	DebugUtil.debug("(抛弃的)processPacket-Message.Type.normal:"+message.toString());	 	                    			                    		
		                    	}else{
			                    	ChatService.recivePacket(message);
			                    	DebugUtil.debug("processPacket-Message.Type.normal:"+message.toString());	 	                    		
		                    	}

	                    	}
	                    		
	                    }
	                    if (message.getType() == Message.Type.groupchat) {//表示群聊
	                    	DebugUtil.debug("processPacket-Message.Type.groupchat:"+message.toString());
	                    	MucChatService.recivePacket(message);
	                    }
	                    if (message.getType() == Message.Type.error) {//表示错误信息
	                    	DebugUtil.debug("processPacket-Message.Type.error:"+message.toString());
	                    	ErrorMsgService.recivePacket(message);
	                    }
	                }			
				}
        	};
        	
        	StanzaListener listenerIQ = new StanzaListener() {

				@Override
				public void processStanza(Stanza stanza)
						throws NotConnectedException, InterruptedException, NotLoggedInException {
					DebugUtil.debug("processPacket-IQ:"+stanza.toString());
					
					if (stanza instanceof IQ) {
						//TODO IQ包
						
					}
				}
        	};
        	
        	Launcher.connection.addSyncStanzaListener(listenerMsg, filterMsg);
        	Launcher.connection.addSyncStanzaListener(listenerIQ, filterIQ);

        	try {
				Launcher.connection.connect();
	        	Launcher.connection.login();  
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        	//注册扩展
        	ProviderRegister.register();
        	
            sessionManager.initializeSession(Launcher.connection);
            sessionManager.setUserBareAddress(getUsername()+"@"+Launcher.DOMAIN);
            sessionManager.setJID(sessionManager.getUserBareAddress()+"/"+Launcher.RESOURCE);
            sessionManager.setUsername(username);
            sessionManager.setPassword(password);  
            
            MucChatService.mucInvitation();
            MucChatService.mucGetInfo("mc2@muc.win7-1803071731");
            
        	UserCache.CurrentUserName = sessionManager.getUsername();
        	UserCache.CurrentUserPassword = sessionManager.getPassword();
        	UserCache.CurrentUserToken = "";
        	UserCache.CurrentBareJid = sessionManager.getUserBareAddress();
        	UserCache.CurrentFullJid = sessionManager.getJID();
        	
        	DebugUtil.debug("UserCache.CurrentUserName:" +UserCache.CurrentUserName);
        	DebugUtil.debug("UserCache.CurrentBareJid:" +UserCache.CurrentBareJid);
        	DebugUtil.debug("UserCache.CurrentFullJid:" +UserCache.CurrentFullJid);
            
            DebugUtil.debug("Launcher.connection.getUser():"+Launcher.connection.getUser());
            DebugUtil.debug("Launcher.connection.getServiceName():"+Launcher.connection.getServiceName());
            
//            Chat chat = ChatManager.getInstanceFor(Launcher.connection)
//            		.createChat("test1@win7-1803071731");
//
//            ChatManager chatManager = ChatManager.getInstanceFor(Launcher.connection);
//            
//            chatManager.addChatListener(new ChatManagerListener() {               
//            	
//                @Override
//                public void chatCreated(Chat cc, boolean bb) {
//                    // 当收到来自对方的消息时触发监听函数
//                    cc.addMessageListener(new ChatMessageListener() {
//                        @Override
//                        public void processMessage(Chat cc, Message mm) {
//                            System.out.println("app2收到消息:" + cc + mm.getBody());
//                        }
//                    });
//                }
//            });
//        
//            Message message = new Message();
//            message.addExtension(new Receipt());
//            message.setBody("你好！");
//            chat.sendMessage(message);
            

            PushNotificationsManager pushNotificationsManager = PushNotificationsManager.getInstanceFor(Launcher.connection);


            try {
				boolean isSupported = pushNotificationsManager.isSupported();//.isSupportedByServer();
				boolean isSupportedByServer = pushNotificationsManager.isSupportedByServer();//.isSupportedByServer();

				DebugUtil.debug("PushNotificationsManager isSupported:" +isSupported);
				DebugUtil.debug("PushNotificationsManager isSupportedByServer:" +isSupportedByServer);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
			return "ok";
		} catch (XMPPException e) {
			return "XMPPException";
		} catch (SmackException e) {
			return "SmackException";
		} catch (IOException e) {
			return "IOException";
		}  
        
        

	}


	
	protected XMPPTCPConnectionConfiguration retrieveConnectionConfiguration() {

		try {
			
			DebugUtil.debug("login:"+getUsername()+"--"+ getPassword()
			+"--"+ Launcher.HOSTPORT+"--"+ Launcher.DOMAIN+"--"+ InetAddress.getByName(Launcher.HOSTNAME));
			
	        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder()
				        .setUsernameAndPassword(getUsername(), getPassword())
				        .setResource(Launcher.RESOURCE)
				        .setPort(Launcher.HOSTPORT)
				        .setConnectTimeout(5000)
				        .setXmppDomain(Launcher.DOMAIN)
				        //.setServiceName(Launcher.DOMAIN)
				        .setHost("127.0.0.1")
						.setHost(Launcher.HOSTNAME)
				        .setSecurityMode(SecurityMode.disabled )
				        .setDebuggerEnabled( true );
	        DebugUtil.debug("builder:"+builder.toString());
			return builder.build();
	        
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;


    }

}
