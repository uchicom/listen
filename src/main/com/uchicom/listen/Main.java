// (c) 2016 uchicom
package com.uchicom.listen;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ListenParameter parameter = new ListenParameter(args);
		if (parameter.init(System.err)) {
			parameter.createServer().execute();
		}
	}

}
