/*
 * CharsetSensitiveFileToStringReader --- Read text files charset sensitive.
 * This is no real `Reader`!
 *
 * Copyright (C) 2011, Johannes Rosenberger <jo.rosenberger(at)gmx-topmail.de>
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
