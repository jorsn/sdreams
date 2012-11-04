package ro.jo.java.io;


import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.URI;


public class CharsetSensitiveFileToStringReader {

	public static String readFile(File file) throws FileNotFoundException, IOException {
		String content;

		UniversalDetector chardet = new UniversalDetector(null);
		
		// read file into byte array 'buf'
		byte[] buf = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(buf);

		// detect charset
		chardet.handleData(buf, 0, buf.length);
		chardet.dataEnd();

		// decode bytes into a String
		try {
			content = new String(buf, chardet.getDetectedCharset());
		} catch (Exception e1) {
			content = new String(buf);
			
		}

		// return content : String
		return content;
	}

	public static String readFile(String path) throws FileNotFoundException, IOException {
		return readFile(new File(path));
	}

	public static String readFile(URI uri) throws FileNotFoundException, IOException {
		return readFile(new File(uri));
	}

}

// vim: foldmethod=syntax
