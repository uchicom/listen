// (c) 2016 uchicom
package com.uchicom.listen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Context {

	private static final Context context = new Context();
	private File file;
	private Context() {

	}
	public static Context singleton() {
		return context;
	}
	public void setFile(File file) throws IOException {
		this.file = file;
		if (!file.exists()) {
			file.createNewFile();
		}
	}
	synchronized public void print(String value) throws FileNotFoundException, IOException {

		try (FileOutputStream fos = new FileOutputStream(file, true);) {
			fos.write(value.getBytes());
			fos.flush();
		}
	}
}
