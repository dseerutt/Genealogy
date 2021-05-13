package Genealogy.GUI;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConsoleScreenOutputStream extends OutputStream {

    private PipedOutputStream out = new PipedOutputStream();
    private Reader reader;
    private JTextArea textArea;
    private PrintStream systemOutputStreamOut;
    private PipedInputStream in;
    private static final int BLOCK_SIZE = 1024;

    public ConsoleScreenOutputStream(JTextArea textArea) throws IOException {
        in = new PipedInputStream(out);
        reader = new InputStreamReader(in, StandardCharsets.UTF_8.name());
        this.textArea = textArea;
    }

    public PrintStream getSystemOutputStreamOut() {
        return systemOutputStreamOut;
    }

    public void setSystemOutputStreamOut(PrintStream systemOutputStreamOut) {
        this.systemOutputStreamOut = systemOutputStreamOut;
    }

    public void write(int i) throws IOException {
        out.write(i);
    }

    /**
     * Write fonction : write data divided by BLOCK_SIZE 1024 data chunks bytes
     *
     * @param bytes
     * @param i
     * @param i1
     * @throws IOException
     */
    public void write(byte[] bytes, int i, int i1) throws IOException {
        if (i1 > BLOCK_SIZE) {
            int offset = 0;
            int remainingBytes = i1;
            int length = BLOCK_SIZE;
            while (remainingBytes > 0) {
                if (remainingBytes < BLOCK_SIZE) {
                    length = remainingBytes;
                }
                out.write(bytes, offset, length);
                remainingBytes -= BLOCK_SIZE;
                offset += BLOCK_SIZE;
                flush();
            }
        } else {
            out.write(bytes, i, i1);
        }
    }

    public void flush() throws IOException {
        if (reader.ready()) {
            char[] chars = new char[BLOCK_SIZE];
            int n = reader.read(chars);

            String txt = new String(chars, 0, n);
            textArea.append(txt);
            systemOutputStreamOut.print(txt);

            // scrolls the text area to the end of data
            textArea.update(textArea.getGraphics());
            textArea.invalidate();
            textArea.validate();
            textArea.repaint();
        }
    }
}