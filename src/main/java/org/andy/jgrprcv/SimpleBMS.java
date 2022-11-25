package org.andy.jgrprcv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class SimpleBMS extends ReceiverAdapter {
	
    JChannel channel;
    String user_name=System.getProperty("user.name", "n/a");
    Address myAddress;
    private final String appName;
    static FileWriter simpleLogOS;
    
    public SimpleBMS() throws FileNotFoundException {
        this.appName = "BMS";
        
    }

    private void start() throws Exception {
        channel=new JChannel(); // use the default config, udp.xml
        channel.setReceiver(this);
        channel.connect("ChatCluster");
        myAddress = channel.getAddress();
        eventLoop();
        channel.close();
    }

    public static void main(String[] args) throws Exception {
        
        simpleLogOS = new FileWriter(new File("//home//andy//logCollect//bm//BMSLogs.txt"));
        
        simpleLogOS.write("I AM BMS");
        
        new SimpleBMS().start();
        
    }
    
    private void eventLoop() {
        
        int i = 0;
        
        while(true) {
            try {
                Thread.sleep(3000L);
                System.out.print("> "); 
                System.out.flush();
                String line="MESSAGE FROM BM MSG ID " + (i++);
                System.out.println("App " + appName + " Sending line " + line);
                simpleLogOS.write("App " + appName + " Sending line " + line + "\n");
                simpleLogOS.flush();
                line="[" + user_name + "] " + line;
                Message msg=new Message(null, line);
                channel.send(msg);
                
            }
            catch(Exception e) {
            }
        }
    }
    
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
        try {
            if(simpleLogOS != null) {
                simpleLogOS.write("** view: " + new_view + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(Message msg) {
        
        if(!msg.getSrc().equals(myAddress)) {
            System.out.println("App->" + appName + " received message: " + msg.getSrc() + ": " + msg.getObject());
            try {
                if(simpleLogOS != null) {
                    simpleLogOS.write("App->" + appName + " received message: " + msg.getSrc() + ": " + msg.getObject() + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}