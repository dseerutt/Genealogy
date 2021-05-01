package Genealogy.GUI;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;

/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 *
 * @author www.codejava.net
 */
public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;
    private StringBuilder buffer;
    private PrintStream systemOutputStreamOut;
    private PrintStream systemOutputStreamErr;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
        buffer = new StringBuilder(128);
    }

    public PrintStream getSystemOutputStreamOut() {
        return systemOutputStreamOut;
    }

    public void setSystemOutputStreamOut(PrintStream systemOutputStreamOut) {
        this.systemOutputStreamOut = systemOutputStreamOut;
    }

    public PrintStream getSystemOutputStreamErr() {
        return systemOutputStreamErr;
    }

    public void setSystemOutputStreamErr(PrintStream systemOutputStreamErr) {
        this.systemOutputStreamErr = systemOutputStreamErr;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            String line = StringUtils.trim(buffer.toString()) + value;
            systemOutputStreamOut.print(line);
            textArea.append(line);
            buffer.delete(0, buffer.length());
            // scrolls the text area to the end of data
            textArea.update(textArea.getGraphics());
            textArea.invalidate();
            textArea.validate();
            textArea.repaint();
        }
    }

    @Override
    public void write(byte in[], int off, int len) throws IOException {
        String value = StringUtils.stripAccents(new String(in));
        byte[] b = value.getBytes();
        Objects.checkFromIndexSize(off, len, b.length);
        // len == 0 condition implicitly handled by loop bounds
        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }
}