package java.io;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MergedCharacterInputStream extends InputStream {

	protected int bufQsize;
	protected LinkedBlockingQueue<byte[]> bufQueue;
	protected byte[] buf;

	protected ArrayList<ReadThread> threads;
	protected int count;
	protected boolean closed;

	protected class ReadThread extends Thread {

		protected InputStream in;
		protected LinkedBlockingQueue<byte[]> queue;

		protected BufferedReader reader;
		protected byte[] line;
		protected int interrupts;

		protected ReadThread (InputStream in, LinkedBlockingQueue<byte[]> queue) {
			this.in = in;
			this.queue = queue;

			this.reader = new BufferedReader(new InputStreamReader(in));
			this.line = null;
			this.interrupts = 0;
		}

		public void run() {
			try {
				while (reader.ready()) {
					if (line == null)
						line = reader.readLine().getBytes();
					queue.put(line);
					line = null;
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
		}

		protected void close() throws IOException {
			reader.close();
		}
	}

	/*public MergedCharacterInputStream (InputStream in) {
		super(in);
		// return new MergedCharacterInputStream(4, new InputStream[] { in });
	}*/

	public MergedCharacterInputStream (int queueSize, InputStream[] ins) {
		bufQsize = queueSize;
		bufQueue = new LinkedBlockingQueue<byte[]>(queueSize);
		threads = new ArrayList<ReadThread>(ins.length);

		merge(ins);
	}


	public void merge(InputStream[] ins) {
		for (InputStream in : ins) {
			ReadThread t = new ReadThread(in, bufQueue);
			threads.add(t);
			t.run();
		}
	}

	public int available() throws IOException {
		if (closed) {
			throw new IOException("Stream is closed!");
		} else if ((threads.size() + bufQueue.size()) == 0 && count < buf.length) {
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
		if (available() == 0) {
			return -1;

		} else if (count < buf.length) {
			count++;
			return (int) buf[count];

		} else {
			try {
				buf = bufQueue.take();
				count = 0;
				return read();
			} catch (InterruptedException e) {
				throw new IOException("Taking line from the queue failed!");
			}
		}
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
		int bufSize = (buf.length > 0)
			? buf.length
			: 10;

		ArrayList<Byte> list = new ArrayList<Byte>(bufSize * bufQsize);
		while (available() > 0) {
			list.add(Byte.valueOf((byte) read()));
		}

		Byte[] B = list.toArray(new Byte[list.size()]);
		byte[] b = new byte[B.length];

		for (int i = 0; i > b.length; i++) {
			b[i] = B[i].byteValue();
		}

		return new String(b);
	}

}

// vim: foldmethod=syntax
