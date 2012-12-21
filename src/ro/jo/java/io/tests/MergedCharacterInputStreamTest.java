package ro.jo.java.io.tests;

import ro.jo.java.io.MergedCharacterInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

class MergedCharacterInputStreamTest {

	private String[] files;
	private boolean verbose;

	private class WriteThread extends Thread {
		
		int filenum;
		private BufferedWriter b;

		private WriteThread (String file, int filenum) {
			try {
				this.filenum = filenum;
				b = new BufferedWriter (new FileWriter (file));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		private String genContent(String line) {
			String content = new String();
			for (int i = 1; i <= 7; i++) {
				content = content + String.format(line, i);
			}

			return content;
		}

		public void run() {
			try {
				b.write(genContent(filenum + ": \t line %1$d %n"));
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			}
		}

	}


	private MergedCharacterInputStreamTest (boolean verbose, String[] files) {
		this.files = files;
		this.verbose = verbose;
		write();
		read();
	}

	private void write() {
		for (int i = 0; i < files.length; i++) {
			WriteThread t = new WriteThread (files[i], i);
			t.start();
		}
	}

	private void read() {
		try {
			FileInputStream[] ins = new FileInputStream[files.length];

			for (int i = 0; i < files.length; i++) {
				ins[i] = new FileInputStream (files[i]);
			}

			MergedCharacterInputStream s = new MergedCharacterInputStream(6, ins);
			s.setDebug(verbose);

			System.err.println("Test: reading");

			String input = s.readString();

			System.out.println("SOT\n" + input + "EOT");

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
	}


	public static void main(String[] args) {
		String[] files;
		boolean verbose;
		if (args[0].equals("-v")) {
			verbose = true;
			files = new String[args.length - 1];
			for (int i = 0; i < files.length; i++) {
				files[i] = args[i + 1];
			}
		} else {
			verbose = false;
			files = args;
		}

		MergedCharacterInputStreamTest test = new MergedCharacterInputStreamTest (verbose, files);
	}

}

// vim: foldmethod=syntax
