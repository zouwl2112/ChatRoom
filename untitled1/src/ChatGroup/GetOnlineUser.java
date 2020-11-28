package ChatGroup;

import appProtocol.Request;
import appProtocol.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Vector;

public class GetOnlineUser extends JPanel implements ActionListener {
    private JButton getOnlineUser;
    private JButton submit;
    private JList list;
    private CommWithServer commWithServer;
    private Request request;
    private Response response;
    private ObjectOutputStream pipedOut;
    private ObjectInputStream pipedIn;
    private static String registerName;
    private int clickNum=0;
    public GetOnlineUser(CommWithServer commWithServer){
        this.commWithServer=commWithServer;
        setLayout(new BorderLayout());
        getOnlineUser=new JButton("获取在线用户");
        getOnlineUser.setBackground(Color.blue);
        submit=new JButton("选择完成");
        submit.setBackground(Color.blue);
        getOnlineUser.addActionListener(this);
        submit.addActionListener(this);
        list=new JList();
        list.setFont(new Font("宋体", Font.BOLD, 15));
        JScrollPane scroll=new JScrollPane();
        scroll.getViewport().setView(list);
        Box box=Box.createHorizontalBox();
        box.add(new JLabel("单击 '获取' ：",JLabel.CENTER));
        box.add(getOnlineUser);
        JPanel panelR=new JPanel(new BorderLayout());
        panelR.setBackground(Color.CYAN);
        panelR.add(submit,BorderLayout.SOUTH);
        JPanel panel=new JPanel(new BorderLayout());
        panel.setBackground(Color.cyan);
        panel.add(box,BorderLayout.NORTH);
        panel.add(new JLabel("选择聊天用户："),BorderLayout.WEST);
        panel.add(scroll,BorderLayout.CENTER);
        panel.add(panelR,BorderLayout.EAST);
        add(panel,BorderLayout.CENTER);
        submit.setEnabled(false);
        validate();
    }
    public static void setRegisterName(String name){
        registerName=name;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(registerName==null||commWithServer==null||!commWithServer.isAlive()){
            JOptionPane.showMessageDialog(null, "请先注册！","错误",JOptionPane.PLAIN_MESSAGE);
            return;
        }
        try{
            if(e.getSource()==getOnlineUser){
                clickNum++;
                if(clickNum==1){
                    PipedInputStream pipedI=new PipedInputStream();
                    PipedOutputStream pipedO=new PipedOutputStream(pipedI);
                    pipedOut=new ObjectOutputStream(pipedO);
                    pipedIn=new ObjectInputStream(pipedI);
                }
                //因为该commWithServer与Register类的commWithServer是同一个，在Register类里commWithServer
                //已经启动了，所以这里不需要再次connect
                request=new Request(2, registerName);
                commWithServer.setRequest(request);
                commWithServer.setPipedOut(pipedOut);
                commWithServer.notifyCommWithServer();;
                response=(Response)pipedIn.readObject();
                //从响应中得到在线的用户注册名列表
                Vector<String> onLineUser=response.getAllNameOfRegister();
                //尝试将null值传递给此方法会导致未定义的行为，并且最有可能发生异常。 创建的模型直接引用给定的
                // Vector 。调用此方法后尝试修改Vector会导致未定义的行为。
                list.setListData(onLineUser);
                submit.setEnabled(true);
            }
            if(e.getSource()==submit){
                List<Object> list2=list.getSelectedValuesList();
                int len=list2.size();
                if(len==0){
                    JOptionPane.showMessageDialog(this, "你还未选择聊天聊天！","信息提示",JOptionPane.PLAIN_MESSAGE);
                    return;
                }
                String register[]=new String[list2.size()];
                for(int i=0;i<list2.size();i++){
                    register[i]=(String)list2.get(i);//获得选择聊天的注册名
                }
                Vector<InetSocketAddress> UsersAddress=new Vector<>();
                int chatUsers=0;
                for(int i=0;i<len;i++){
                    if(register[i].equals(registerName))//如果聊天对象名与当前相同，则跳过
                    {
                        continue;
                    }
                    request=new Request(3, registerName, register[i]);
                    commWithServer.setRequest(request);
                    commWithServer.setPipedOut(pipedOut);
                    commWithServer.notifyCommWithServer();
                    response=(Response)pipedIn.readObject();
                    //从响应中得到的聊天对象地址加入到列表中
                    UsersAddress.add(response.getChatUserAddress());
                    chatUsers++;
                }
                String message=null;
                if(chatUsers==0){
                    message="不能选择自己！！";
                }else{
                    Chat.setChatP2PEndAddress(UsersAddress);
                    message="已获取到你选择用户的地址，请单击左侧的|聊天|按钮";
                }
                JOptionPane.showMessageDialog(this, message,"信息提示",JOptionPane.PLAIN_MESSAGE);
                UsersAddress.clear();//清空地址列表
                list.setListData(UsersAddress);
            }
        }catch (Exception e1){
            JOptionPane.showMessageDialog(this, "服务器连接失败","警告",JOptionPane.WARNING_MESSAGE);
        }
    }
}
