package Genealogy.GUI;

import Genealogy.URLConnexion.Geneanet.GeneanetBrowser;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static Genealogy.GUI.MainScreen.logger;

public class ConsoleScreen extends JFrame {
    private JPanel panel1;
    private JTextArea textLog;
    private JScrollPane scrollPane;
    private JButton retourButton;
    private static ConsoleScreen instance;
    private ConsoleScreenOutputStream customOutputStream;
    private PrintStream printStream;

    public ConsoleScreen(String title) {
        super(title);
        initButtons();
        initScrollPane();

        setPreferredSize(new Dimension(700, 500));
        pack();
        setLocationRelativeTo(null);

        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            //I skipped unused callbacks for readability

            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                MainScreen.getINSTANCE().setVisible(true);
            }
        });

        setVisible(true);
        instance = this;
    }

    public JTextArea getTextLog() {
        return textLog;
    }

    private void initButtons() {
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = MainScreen.getINSTANCE().getThread();
                if (thread != null) {
                    thread.interrupt();
                }
                setVisible(false);
                MainScreen.getINSTANCE().setVisible(true);
                try {
                    GeneanetBrowser.setKill(true);
                } catch (Exception exception) {
                    logger.error("Failed to kill thread ", exception);
                }
                restoreLogs();
            }
        });
    }

    public void restoreLogs() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
        logger.info("Back to normal logs");
        textLog.setText(StringUtils.EMPTY);
    }

    private void initScrollPane() {
        DefaultCaret caret = (DefaultCaret) textLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void initLogs(PrintStream outputStreamOut) {
        logger.info("Start JTextArea logs");
        try {
            customOutputStream = new ConsoleScreenOutputStream(textLog);
            printStream = new PrintStream(customOutputStream, true, "UTF-8");
        } catch (Exception e) {
            logger.error("Failed to create PrintStream", e);
        }
        GeneanetBrowser.setKill(false);
        customOutputStream.setSystemOutputStreamOut(outputStreamOut);
        System.setOut(printStream);
        System.setErr(printStream);
        textLog.setText(StringUtils.EMPTY);
    }

    public static ConsoleScreen getInstance() {
        if (instance == null) {
            instance = new ConsoleScreen("Console");
        }
        return instance;
    }
}
