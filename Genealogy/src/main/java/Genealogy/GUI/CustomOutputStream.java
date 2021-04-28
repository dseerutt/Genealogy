package Genealogy.GUI;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 *
 * @author www.codejava.net
 */
public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;
    private StringBuilder buffer;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
        buffer = new StringBuilder(128);
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        buffer.append(value);
        if (value.equals("\n")) {
            textArea.append(buffer.toString());
            buffer.delete(0, buffer.length());
            // scrolls the text area to the end of data
            textArea.update(textArea.getGraphics());
        }
    }
}