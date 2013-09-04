/*
 * MergedCharacterInputStreamTest --- Test MergedCharacterInputStream
 * 
 * Usage (after compilation):
 *     java ro.jo.java.io.MergedCharacterInputStream file1 ... filen
 *
 * Copyright (C) 2013, Johannes Rosenberger <jo.rosenberger(at)gmx-topmail.de>
 *
 * This code is released under a BSD Style License.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * For more details please read the '[LICENSE]' file.
 *
 * [LICENSE]: https://github.com/jorsn/sdreams/blob/master/LICENSE
 */

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
			s.debug = verbose;

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
