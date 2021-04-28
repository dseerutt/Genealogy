package Genealogy.GUI;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

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
                setVisible(false);
                MainScreen.getINSTANCE().setVisible(true);
                System.setOut(System.out);
                System.setErr(System.err);

            }
        });
    }

    private void initScrollPane() {
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        DefaultCaret caret = (DefaultCaret) textLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    public static ConsoleScreen getInstance() {
        if (instance == null) {
            instance = new ConsoleScreen("Console");
        }
        return instance;
    }
}
