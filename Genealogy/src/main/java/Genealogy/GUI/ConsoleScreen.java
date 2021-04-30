package Genealogy.GUI;

import Genealogy.URLConnexion.Geneanet.GeneanetBrowser;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

import static Genealogy.GUI.MainScreen.logger;

public class ConsoleScreen extends JFrame {
    private JPanel panel1;
    private JTextArea textLog;
    private JScrollPane scrollPane;
    private JButton retourButton;
    private static ConsoleScreen instance;

    public ConsoleScreen(String title) {
        super(title);

        initButtons();
        initScrollPane();

        PrintStream printStream = new PrintStream(new CustomOutputStream(textLog));
        System.setOut(printStream);
        System.setErr(printStream);

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
                textLog.setText("");
                try {
                    GeneanetBrowser.setKill(true);
                } catch (Exception exception) {
                    logger.error("Failed to kill thread ", exception);
                }
                System.setOut(System.out);
                System.setErr(System.err);

            }
        });
    }

    private void initScrollPane() {
        DefaultCaret caret = (DefaultCaret) textLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public void reset() {
        textLog.setText("");
        GeneanetBrowser.setKill(false);
    }

    public static ConsoleScreen getInstance() {
        if (instance == null) {
            instance = new ConsoleScreen("Console");
        }
        return instance;
    }
}
