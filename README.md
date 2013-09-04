sdreams — dream streams for java
========================================

**sdreams** is a bundle of a stream and a "reader" that I have written for
my own java programs. Both shall be used with *text files/character streams*!



Classes
-----------


###MergedCharacterInputStream

This class merges multiple character InputStreams.

It is not really documented but I think the source code is almost
self-explaining if you know the [javadoc of `InputStream`][javadoc-IS],
its superclass.

I am using this class to merge stdin and stdout when using the java compiler API.

[javadoc-IS]: http://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html


###CharsetSensitiveFileToStringReader

*In fact, this is no real `Reader`!*
It is only a class with three static methods of type `String`:

* `readFile(File file)`,
* `readFile(String path)` and 
* `readFile(URI uri)`.

They do all the same: They return the `String` content of a file,
automatically detecting and taking into a count the character set.
Tis is achieved by using [juniversalchardet].

[juniversalchardet]: https://code.google.com/p/juniversalchardet/



Dependencies
--------------

* Certainly the standard java libs.
* CharsetSensitiveFileToStringReader needs [juniversalchardet].



Copyright
-----------

Copyright (c) 2013 Johannes Rosenberger <jo.rosenberger(at)gmx-topmail.de>

This code is released under a BSD Style License.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
DAMAGE.

For more details please read the '[LICENSE]' file.

[LICENSE]: https://github.com/jorsn/sdreams/blob/master/LICENSE
