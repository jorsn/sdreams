/*
 * MergedCharacterInputStream --- Merge multiple character InputStreams.
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

package ro.jo.java.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MergedCharacterInputStream extends InputStream {

	public boolean debug = false;
	private int debugMsgIndent;

	protected int bufQsize;
	public LinkedBlockingQueue<byte[]> bufQueue;
	protected byte[] buf;

	protected ArrayList<ReadThread> threads;
	protected int count;
	protected boolean closed;

	protected class ReadThread extends Thread {

		protected InputStream in;

		protected ArrayList<ReadThread> threads;
		protected LinkedBlockingQueue<byte[]> queue;

		protected BufferedReader reader;
		protected byte[] line;
		protected int interrupts;

		protected ReadThread (InputStream in, LinkedBlockingQueue<byte[]> queue, ArrayList<ReadThread> threads) {
			threads.add(this);
			this.in = in;
			this.queue = queue;
			this.threads = threads;

			this.reader = new BufferedReader(new InputStreamReader(in));
			this.line = null;
			this.interrupts = 0;
		}

		public void run() {
			debugMsg("ReadThread.run:", 1);
			try {
				while (!reader.ready())
					if (debug)
						System.err.print(".");
				System.err.print("\n");
				debugMsg("reader.ready() == " + (reader.ready() ? "true" : "false"), 0);

				boolean read = true;
				while (reader.ready() && read) {
					debugMsg("reading lines", 1);

					if (line == null)
						line = (reader.readLine() + "\n").getBytes();

					if (line != null) {
						debugMsg(new String(line), 0);
						queue.put(line);
						line = null;
					} else {
						read = false;
					}

					debugMsg("complete", -1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				interrupts++;

				if (interrupts == 3)
					System.err.println("FATAL ERROR: enqueueing read data failed 3 times!");
				else
					run();

				e.printStackTrace();
			}
			debugMsg("ReadThread.read: complete", -1);
			threads.remove(this);
		}

		protected void close() throws IOException {
			reader.close();
		}
	}

	/*public MergedCharacterInputStream (InputStream in) {
		super(in);
		// return new MergedCharacterInputStream(4, new InputStream[] { in });
	}*/

	public MergedCharacterInputStream (int queueSize, InputStream... ins) {
		debugMsgIndent = 0;

		bufQsize = queueSize;
		bufQueue = new LinkedBlockingQueue<byte[]>(queueSize);
		threads = new ArrayList<ReadThread>(ins.length);

		closed = false;

		merge(ins);
	}

	protected void merge(InputStream[] ins) {
		for (InputStream in : ins) {
			ReadThread t = new ReadThread(in, bufQueue, threads);
			t.start();
		}
	}

	public int available() throws IOException {
		if (closed) {
			throw new IOException("Stream is closed!");
		} else if ((threads.size() + bufQueue.size()) == 0 && (buf == null || count >= buf.length)) {
			return 0;
		} else {
			return 1;
		}
	}

	public void close() throws IOException {
		for (ReadThread t : threads.toArray(new ReadThread[threads.size()])) {
			closed = true;
			t.close();
			bufQueue.clear();
			buf = null;
		}
	}

	public boolean markSupported() {
		return false;
	}

	public int read() throws IOException {
		debugMsg("read:", 1);

		int ret;

		if (available() == 0) {
			debugMsg("available() == 0 -> return -1", 0);

			ret = -1;

		} else if (buf != null && count < buf.length) {
			debugMsg(String.format("buf available -> return (int) buf[%1$s]", count), 0);
			ret = (int) buf[count];

			count++;

		} else {
			try {
				debugMsg("take buf", 1);
				debugMsg(bufQueue.toString(), 0);
				buf = bufQueue.take();
				debugMsg("complete", -1);

				debugMsg("reset 'count' (count = 0)", 0);
				count = 0;

				ret = this.read();

			} catch (InterruptedException e) {
				debugPrintStackTrace(e);
				throw new IOException("Taking line from the queue failed!");
			}
		}

		debugMsg("reading completed", -1);
		return ret;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int rlen = 0;

		if (len > 0) {
			skip(off);

			for (
					int ib = b.length;
					ib > 0 && rlen < len && available() > 0;
					ib--, rlen++
			    )
				b[ib] = (byte) read();
		}

		return rlen;
	}

	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	public String readString() throws IOException {
		debugMsg("readString:", 1);

		int bufSize = (buf != null && buf.length > 0)
			? buf.length
			: 10;
		debugMsg("bufSize: " + bufSize, 0);


		ArrayList<Byte> list = new ArrayList<Byte>(bufSize * bufQsize);
		debugMsg("read bytes into 'list'", 1);

		for (int i = read(); i > -1; i = read()) {
			byte b =  (byte) i;
			list.add(Byte.valueOf(b));

			debugMsg(String.format("read char: '%1$s'", new String(new byte[] { b })), 0);
		}
		debugMsg("'ArrayList<Byte> list' readding completed.", -1);

		Byte[] B = list.toArray(new Byte[list.size()]);
		byte[] b = new byte[B.length];

		for (int i = 0; i < b.length; i++) {
			b[i] = B[i].byteValue();
		}

		return new String(b);
	}

	protected void debugMsg(String msg, int addIndent) {
		if (debug) {
			System.err.println(indent(debugMsgIndent) + msg);
			debugMsgIndent += addIndent;
		}
	}

	protected String indent(int num) {
		String ind = new String();
		for (int i = 0; i < num; i++)
			ind += "  ";
		return ind;
	}

	protected void debugPrintStackTrace(Exception e) {
		if (debug)
			e.printStackTrace();
	}

}

// vim: foldmethod=syntax
