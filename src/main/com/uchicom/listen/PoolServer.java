/**
 * (c) 2013 uchicom
 */
package com.uchicom.listen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class PoolServer extends SingleServer implements Runnable {
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
        // スレッドプール数
        int pool = 10;
        if (args.length > 3) {
            pool = Integer.parseInt(args[3]);
        }
        execute(file, port, back, pool);

       
    }

    /**
     * 
     * @param hostName
     * @param file
     * @param port
     * @param back
     */
    private static void execute(File file, int port, int back, int pool) {
        ExecutorService exec = null;
        FileOutputStream fos = null;
        ServerSocket server = null;
        try {
            fos = new FileOutputStream(file, true);
            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(new InetSocketAddress(port), back);
            serverQueue.add(server);

            exec = Executors.newFixedThreadPool(pool);
            while (true) {
                Socket socket = server.accept();
                MultiServer decoy = new MultiServer(port, fos, socket);
                exec.execute(decoy);
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


    public PoolServer(int port, FileOutputStream fos, Socket socket) {
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
