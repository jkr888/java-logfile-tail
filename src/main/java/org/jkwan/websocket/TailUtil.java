/*******

Copyright (c) 2017, 2019, Oracle Corporation and/or its affiliates. All rights reserved.

Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.

Subject to the condition set forth below, permission is hereby granted to any person obtaining
a copy of this software, associated documentation and/or data (collectively the "Software"),
free of charge and under any and all copyright rights in the Software, and any and all patent
rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified
Software as contributed to or provided by such licensor, or (ii) the Larger Works
(as defined below), to deal in both

(a) the Software, and

(b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with
the Software (each a "Larger Work" to which the Software is contributed by such licensors), without
restriction, including without limitation the rights to copy, create derivative works of, display,
perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made,
and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either
these or other terms.

This license is subject to the following condition:

The above copyright notice and either this complete permission notice or at a minimum a reference to
the UPL must be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ***
 *
 * @author johny.kwan@oracle.com 
 *         jkr888@gmail.com
 *         
 ***/
package org.jkwan.websocket;
import java.io.File;
import java.io.RandomAccessFile;

public class TailUtil {
    public static StringBuilder getLastLines(String filename, int lines) throws Exception {
        int readLines = 0;
        File file = new File(filename);
        StringBuilder builder = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long fileLength = file.length() - 1;
            // Set the pointer at the last of the file
            randomAccessFile.seek(fileLength);

            for (long pointer = fileLength; pointer >= 0; pointer--) {
                randomAccessFile.seek(pointer);
                char c;
                // read from the last, one char at the time
                c = (char) randomAccessFile.read();
                // break when end of the line
                if (c == '\n') {
                    readLines++;
                    if (readLines == lines)
                        break;
                }
                builder.append(c);
                fileLength = fileLength - pointer;
            }
            // Since line is read from the last so it is in reverse order. Use reverse
            // method to make it correct order
            builder.reverse();
            //System.out.println(builder.toString());
        }
        return builder;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        TailUtil tailN = new TailUtil();
        String filename = "";
//        System.out.println(tailN.getLastLines(filename, 50).toString());
//        System.out.println("Execution Time : " + (System.currentTimeMillis() - startTime));

    }

}