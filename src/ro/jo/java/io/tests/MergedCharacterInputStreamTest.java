package java.io.tests;

import java.io.MergedCharacterInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

class MergedCharacterInputStreamTest {

	private String[] files;

	private class WriteThread {
		
		int filenum;
		private BufferedWriter b;

		private WriteThread (String file, int filenum) {
			this.filenum = filenum;
			b = new BufferedWriter (new FileWriter (file));
		}

		private String genContent(String line) {
			for (int i = 1; i <= 7; i++) {
				content = content + String.format(line, i);
			}
		}

		public void run() {
			try {
				b.write(genContent(filenum + ": \t line %1$d %n"));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

	}


	private MergedCharacterInputStreamTest (String[] files) {
		this.files = files;
		write();
		read();
	}

	private void write() {
		for (int i = 0; i < files; i++) {
			WriteThread t = new WriteThread (files[i], i);
			t.start();
		}
	}

	private void read() {
		try {
			FileInputStream[] ins = new FileInputStream[files.length];

			for (int i = 0; i < files.length; i++)
				ins[i] = new FileInputStream (files[i]);

			MergedCharacterInputStream s = new MergedCharacterInputStream(6, ins);

			String input = s.readString();

			System.out.println(input);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}


	public static void main(String[] args) {
		MergedCharacterInputStreamTest test = new MergedCharacterInputStreamTest (args);
	}

}

// vim: foldmethod=syntax
