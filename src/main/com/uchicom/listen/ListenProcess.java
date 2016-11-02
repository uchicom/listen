// (c) 2016 uchicom
package com.uchicom.listen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;

import com.uchicom.server.Parameter;
import com.uchicom.server.ServerProcess;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class ListenProcess implements ServerProcess {

	private Parameter parameter;
	private Socket socket;
	public ListenProcess(Parameter parameter, Socket socket) {
		this.parameter = parameter;
		this.socket = socket;
	}
	/* (非 Javadoc)
	 * @see com.uchicom.server.ServerProcess#getLastTime()
	 */
	@Override
	public long getLastTime() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	/* (非 Javadoc)
	 * @see com.uchicom.server.ServerProcess#forceClose()
	 */
	@Override
	public void forceClose() {
		// TODO 自動生成されたメソッド・スタブ

	}

	/* (非 Javadoc)
	 * @see com.uchicom.server.ServerProcess#execute()
	 */
	@Override
	public void execute() {
        String ip = String.valueOf(socket.getRemoteSocketAddress());
        if (Constants.DEBUG) {
            debugPrint(parameter.get("port"), ip, "START", false);
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        		PrintStream ps = new PrintStream(socket.getOutputStream());) {
            // 1接続に対する受付開始
            ps.print("Welcome to Decoy Server. Your IP and message are published.\r\n");
            ps.flush();
            String line = br.readLine();
            StringBuffer strBuff = new StringBuffer(1024);
            while (line != null) {
            	strBuff.setLength(0);
                // デバッグ文字列
                if (Constants.DEBUG) {
                    debugPrint(parameter.get("port"), ip, line, true);
                }
                strBuff.append(Constants.format.format(new Date()))
                .append(":")
                .append(ip)
                .append("[")
                .append(line)
                .append("]\r\n");
                Context.singleton().print(strBuff.toString());
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

            if (Constants.DEBUG) {
                debugPrint(parameter.get("port"), ip, "END", false);
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
    public void debugPrint(String port, String ip, String value, boolean user) {
        System.out.print("Decoy(");
        System.out.print(port);
        System.out.print(") ");
        System.out.print(Constants.format.format(new Date()));
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

}
