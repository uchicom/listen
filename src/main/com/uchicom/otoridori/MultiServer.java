/**
 * (c) 2012 uchicom
 */
package com.uchicom.otoridori;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Uchiyama Shigeki
 *
 */
public class MultiServer extends SingleServer implements Runnable {

    
    /**
     * メイン処理.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("args.length != 2");
            return;
        }
        // アクセスログ出力ファイル
        File file = new File(args[0]);

        // ポート
        int port = 0;
        if (args.length > 2) {
            port = Integer.parseInt(args[2]);
        }
        // 接続待ち数
        int back = 10;
        if (args.length == 3) {
            back = Integer.parseInt(args[3]);
        }
        execute(file, port, back);

    }
    

    /**
     * 
     * @param hostName
     * @param file
     * @param port
     * @param back
     */
    private static void execute(File file, int port, int back) {
        FileOutputStream fos = null;
        ServerSocket server = null;
        try {
            fos = new FileOutputStream(file, true);
            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(port), back);
            serverQueue.add(server);
            while (true) {
                Socket socket = server.accept();
                MultiServer decoy = new MultiServer(port, fos, socket);
                new Thread(decoy).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            synchronized (fos) {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        fos = null;
                    }
                }
            }
            synchronized (server) {
                if (server != null) {
                    try {
                        server.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        server = null;
                    }
                }
            }
        }
    }
    
    public MultiServer(int port, FileOutputStream fos, Socket socket) {
        super(port, fos, socket);
    }
    

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        decoy(socket);
    }
}
