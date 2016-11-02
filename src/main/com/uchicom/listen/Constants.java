// (c) 2016 uchicom
package com.uchicom.listen;

import java.text.SimpleDateFormat;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Constants {

    public static final boolean DEBUG = true;

    public static final SimpleDateFormat format = new SimpleDateFormat(
             "yyyy/MM/dd HH:mm:ss.SSS");
    /** デフォルト出力ファイル */
    public static String DEFAULT_FILE = "listen.txt";
    /** デフォルト待ち受けポート番号 */
    public static String DEFAULT_PORT = "23";
    /** デフォルト接続待ち数 */
	public static String DEFAULT_BACK = "10";
	/** デフォルトスレッドプール数 */
	public static String DEFAULT_POOL = "10";
}
