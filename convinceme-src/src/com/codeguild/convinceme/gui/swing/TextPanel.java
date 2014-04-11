package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;

/**
 * <p>Description: A generic text panel.
 * Old entries get truncated if it gets too long</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */
public class TextPanel extends JPanel {

    protected static final int BACKSCROLL_OVERDRAFT = 2048; // 2K
    protected static final int DEFAULT_BACKSCROLL = 8192;// 8K
    protected static final Font DEFAULT_FONT = new Font("Helvetica", Font.PLAIN, 12);

    protected JTextArea mText;
    protected String mType;
    protected boolean mUseBuffer;
    protected int mBackScroll = DEFAULT_BACKSCROLL;


    /**
     * Create a panel with a text area that can be made editable or
     * not (default not editable) and can have a buffer
     * @param type Header at the top of the text field
     */
    public TextPanel(String type) {
        setLayout(new BorderLayout(10, 10));
        mText = new JTextArea(10, 60);
        mText.setEditable(false);
        mType = type;
        mUseBuffer = false;

        JLabel header = new JLabel(type);
        add(header, BorderLayout.NORTH);
        add(new JScrollPane(mText), BorderLayout.CENTER);
    }

    public String getText() {
        return mText.getText();
    }

    public void setEditable(boolean editable) {
        mText.setEditable(editable);
    }

    public void setUseBuffer(boolean useBuffer) {
        mUseBuffer = useBuffer;
    }
    public void appendText(String text) {
        try {
            if (mUseBuffer) {
                mBackScroll -= text.length();
                if (mBackScroll < -BACKSCROLL_OVERDRAFT) {
                    String temp = mText.getText();
                    mText.setText("");
                    Debug.println("Text pane full, adjusting buffer...");
                    mText.append(temp.substring(-mBackScroll));
                    mBackScroll = 0;
                }
            }
            mText.append(text + "\n");
        } catch (Exception e) {
            Debug.println("Unexpected append error, try again.");
        }
    }

    public void clear() {
        mText.setText("");
    }

    public void setText(String text) {
        mText.setText(text);
    }

    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }

     /**
     * Save text to a file
     * @param currentDir the default directory to save to
     * @return currentDir the directory save to (in case it changed)
     */
    public File saveFile(File currentDir) {
        JFileChooser fileChooser = new JFileChooser();
        if (currentDir != null) {
            fileChooser.setCurrentDirectory(currentDir);
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        int choice = fileChooser.showDialog(this, "Save " + mType);
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getName();
                currentDir = fileChooser.getCurrentDirectory();
                // tack on directory for save
                filename = currentDir + "/" + filename;
                FileWriter fw = new FileWriter(filename);
                fw.write(getText());
                fw.close();
                Debug.println("Saving " + mType + " as " + filename);
            } catch (IOException e) {
                Debug.println("Couldn't write " + mType + " file.");
                Debug.printStackTrace(e);
            }
        }
        return currentDir;
    }
}