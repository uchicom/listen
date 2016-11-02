/**
 * (c) 2013 uchicom
 */
package com.uchicom.listen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class SingleServer {

    public static final boolean DEBUG = true;

    public static final SimpleDateFormat format = new SimpleDateFormat(
             "yyyy/MM/dd HH:mm:ss.SSS");
    public static Queue<ServerSocket> serverQueue = new ConcurrentLinkedQueue<ServerSocket>();
    protected int port;
    protected int back;
    protected Socket socket;
    protected FileOutputStream fos;
    

    /**
     * メイン処理.
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("args.length < 2");
            return;
        }
        // アクセスログ出力ファイル
        File file = new File(args[0]);

        // ポート
        int port = 0;
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        // 接続待ち数
        int back = 10;
        if (args.length > 2) {
            back = Integer.parseInt(args[2]);
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
                SingleServer decoy = new SingleServer(port, fos, socket);
                decoy.decoy(socket);
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
    
    
    public SingleServer(int port, FileOutputStream fos, Socket socket) {
        this.port = port;
        this.fos = fos;
        this.socket = socket;
    }
    
    public void decoy(Socket socket) {

        String ip = String.valueOf(socket.getRemoteSocketAddress());
        if (DEBUG) {
            debugPrint(port, ip, "START", false);
        }
        BufferedReader br = null;
        PrintStream ps = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            ps = new PrintStream(socket.getOutputStream());
            // 1接続に対する受付開始
            ps.print("Welcome to Decoy Server. Your IP and message are published.\r\n");
            ps.flush();
            String line = br.readLine();
            while (line != null) {
                // デバッグ文字列
                if (DEBUG) {
                    debugPrint(port, ip, line, true);
                }
                fos.write(format.format(new Date()).getBytes());
                fos.write(":".getBytes());
                fos.write(ip.getBytes());
                fos.write("[".getBytes());
                fos.write(line.getBytes());
                fos.write("]\r\n".getBytes());
                ps.print("OK\r\n");
                ps.flush();
                if (line.matches("^ *[Qq][Uu][Ii][Tt] *$") ||
                        line.matches("^ *[Ee][Xx][Ii][Tt] *$")) {
                    break;
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    br = null;
                }
            }
            synchronized (socket) {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        socket = null;
                    }
                }
            }

            if (DEBUG) {
                debugPrint(port, ip, "END", false);
            }
        }
    }

    /**
     * デバッグ出力
     * @param port
     * @param ip
     * @param value
     * @param user
     */
    public void debugPrint(int port, String ip, String value, boolean user) {
        System.out.print("Decoy(");
        System.out.print(port);
        System.out.print(") ");
        System.out.print(format.format(new Date()));
        System.out.print(":");
        System.out.print(ip);
        if (user) {
            System.out.print("[");
            System.out.print(value);
            System.out.println("]");
        } else {
            System.out.print(" ");
            System.out.println(value);
        }
    }
    
    /**
     * 終了メソッド
     * @param args
     */
    public static void shutdown(String[] args) {
        if (!serverQueue.isEmpty()) {
            try {
                serverQueue.poll().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
