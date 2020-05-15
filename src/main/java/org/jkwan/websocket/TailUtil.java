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
        String filename = "C:\\Oracle\\Middleware\\OCCAS72\\user_projects\\domains\\plain_base\\servers\\AdminServer\\logs\\AdminServer.log";
        System.out.println(tailN.getLastLines(filename, 50).toString());

        System.out.println("Execution Time : " + (System.currentTimeMillis() - startTime));

    }

}