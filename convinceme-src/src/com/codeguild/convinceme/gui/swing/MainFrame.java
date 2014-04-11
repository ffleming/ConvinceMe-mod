package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.*;
import com.codeguild.convinceme.utils.Debug;
import com.codeguild.convinceme.gui.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * <p>Description: Create Convince Me menus and windows.
 * ECHO simulation run as a method on this class</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */
public class MainFrame extends Configuration {

    public static void main(String args[]) {
        new MainFrame();
    }

    protected ListPanel mTextListPanel;
    protected DiagramPanel mDiagramPanel;
    protected TextPanel mNotePanel,  mEncodingPanel, mLogPanel;
    protected ParameterPanel mParameterPanel;
    protected JFrame mAppWindow;
    protected JTabbedPane mTabbedPane;
    
    protected File mCurrentDir;

    public MainFrame() {
        setCustomLookAndFeel();

        mTextListPanel = new ListPanel(this);
        mDiagramPanel = new DiagramPanel(this);
        mLogPanel = new TextPanel("Log");
        mLogPanel.setUseBuffer(true);
        mNotePanel = new TextPanel("Notes");
        mNotePanel.setEditable(true);
        mEncodingPanel = new TextPanel("ECHO Encoding");
        mParameterPanel = new ParameterPanel();

        // create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Diagram", mDiagramPanel);
        tabbedPane.setSelectedIndex(0);
        tabbedPane.addTab("Text", mTextListPanel);
        tabbedPane.addTab("Notes", mNotePanel);
        tabbedPane.addTab("Log", mLogPanel);
        tabbedPane.addTab("Encoding", mEncodingPanel);
        tabbedPane.setBackground(Configuration.BACKGROUND);
        mTabbedPane = tabbedPane;
        
        // Create application window
        mAppWindow = new JFrame("Convince Me");
        mAppWindow.setJMenuBar(getMenuBar());
        mAppWindow.getContentPane().setBackground(Configuration.BACKGROUND);
        mAppWindow.getContentPane().setLayout(new BorderLayout(10, 10));
        mAppWindow.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        mAppWindow.setSize(getPreferredSize());

        // specify that my listener takes care of all closing duties
        mAppWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mAppWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });

        Utils.centerFrameOnScreen(mAppWindow);
        mAppWindow.setVisible(true);
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 900);
    }

    public void log(String s) {
        mLogPanel.appendText(s);
    }

    public void newCM() {
        new MainFrame();
    }

    public void dispose() {
        mAppWindow.dispose();
    }

    /**
     * Save the current argument to a file
     */
    public void save() {
        JFileChooser fileChooser = new JFileChooser();
        if (mCurrentDir != null) {
            fileChooser.setCurrentDirectory(mCurrentDir);
        }
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        int choice = fileChooser.showDialog(mAppWindow, "Save Argument");
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                String filename = fileChooser.getSelectedFile().getName();
                mCurrentDir = fileChooser.getCurrentDirectory();
                // tack on directory for save
                filename = mCurrentDir + "/" + filename;
                // create necessary output streams to save Argument
                Document document = DocumentHelper.createDocument();
                Element arg = mArgument.getXML();
                String notes = mNotePanel.getText();
                if (notes.length() > 0) {
                    arg.addAttribute(Argument.NOTES, notes);
                }
                document.add(arg);
                FileWriter fw = new FileWriter(filename);
                OutputFormat format = OutputFormat.createPrettyPrint();
                XMLWriter writer = new XMLWriter(fw, format);
                writer.write(document);
                writer.close();
                log("Saving argument: " + filename);
            } catch (Exception e) {
                Debug.println("Couldn't write argument file.");
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Load an argument
     */
    public void load() {
        JFileChooser fileChooser = new JFileChooser();
        if (mCurrentDir != null) { fileChooser.setCurrentDirectory(mCurrentDir); }
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int option = fileChooser.showDialog(mAppWindow,"Open Argument");
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                // get full file path
                String filename = fileChooser.getSelectedFile().getName();
                mCurrentDir = fileChooser.getCurrentDirectory();
                filename = mCurrentDir + "/" + filename;
                // create necessary input streams
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                SAXReader xmlReader = new SAXReader();
                Document doc = xmlReader.read(reader);
                reader.close();
                org.dom4j.Element root = doc.getRootElement();
                mNotePanel.setText(root.attributeValue(Argument.NOTES));
                mArgument = Argument.readXML(root);
                log("Loading argument: " + filename);
                updatePanels();
            } catch (Exception e) {
                Debug.println("Couldn't read argument file.");
                Debug.printStackTrace(e);
            }
        }
    }

    public void saveLog() {
        mCurrentDir = mLogPanel.saveFile(mCurrentDir);
    }

    public void saveEncoding() {
        mCurrentDir = mEncodingPanel.saveFile(mCurrentDir);
    }

    public void clearLog() {
        mLogPanel.clear();
    }

    public void addHypDialog() {
        PropositionEditPanel dialog = new PropositionEditPanel(this, new Proposition(), true);
        dialog.showDialog();
    }

    void addDataDialog() {
        PropositionEditPanel dialog = new PropositionEditPanel(this, new Proposition(), false);
        dialog.showDialog();
    }

    void addExpDialog() {
        LinkEditPanel dialog = new LinkEditPanel(this, new Link(Link.EXPLAIN));
        dialog.showDialog();
    }

    void addContDialog() {
        LinkEditPanel dialog = new LinkEditPanel(this, new Link(Link.CONTRADICT));
        dialog.showDialog();
    }

    public void updatePanels() {
        updateEncoding();
        updateText();
        updateGraph();
    }

    public void updateEncoding() {
        mEncoding = new Encoding(mArgument);
        mEncodingPanel.setText(mEncoding.getText());
    }

    public void updateText() {
        mTextListPanel.setText(getArg().getHypotheses(),
                               getArg().getData(),
                               getArg().getExps(),
                               getArg().getConts());
    }

    public void updateGraph() {
        mDiagramPanel.setGraph(getArg().getHypotheses(),
                               getArg().getData(),
                               getArg().getExps(),
                               getArg().getConts());
    }

    public void deleteSelected() {
        log("Deleting selected propositions and links...");
        getArg().deleteHypotheses(mTextListPanel.getSelectedHyps());
        getArg().deleteData(mTextListPanel.getSelectedData());
        getArg().deleteExplanations(mTextListPanel.getSelectedExps());
        getArg().deleteContradictions(mTextListPanel.getSelectedConts());
        updatePanels();
    }

    public void editSelected() {
        log("Editing selected propositions...");
        editFromVector(getArg().getHypotheses(), mTextListPanel.getSelectedHyps(), true);
        editFromVector(getArg().getData(), mTextListPanel.getSelectedData(), false);
        updatePanels();
    }

    public void editFromVector(PropositionVector v, int[] indexes, boolean isHyp) {
        Proposition prop;
        for (int i = 0; i < indexes.length; i++) {
            try {
                prop = v.getPropAt(indexes[i]);
                log("Editing " + prop.getLabel());
                PropositionEditPanel dialog = new PropositionEditPanel(this, prop, isHyp);
                dialog.showDialog();
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    public void showParams() {
        mParameterPanel.showDialog();
    }

    public void showEncoding() {
        JOptionPane.showConfirmDialog(null,
                mEncodingPanel,
                "ECHO Encoding",
                JOptionPane.OK_OPTION);
    }

    /**
     * Run the ECHO simulation, log what happens, and tell the user
     */
    public void runECHO() {
        runECHO(mParameterPanel.getExcitation(),
                mParameterPanel.getInhibition(),
                mParameterPanel.getDataExcitation(),
                mParameterPanel.getDecay());
        mTabbedPane.setSelectedIndex(3);
    }

    public void showCorrelationMessage(String message) {
        JOptionPane.showMessageDialog(mAppWindow, message, "Simulation Results", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Set custom look and feel
     */
    private void setCustomLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            UIManager.put("Menu.background", Color.white);
            UIManager.put("MenuBar.background", Color.white);
            UIManager.put("MenuItem.background", Color.white);
            UIManager.put("CheckBoxMenuItem.background", Color.white);
            UIManager.put("ComboBox.background", Color.white);
            UIManager.put("MenuBar.font", Configuration.MENU_FONT);

            UIManager.put("ComboBox.font", Configuration.TEXT_FONT);
            UIManager.put("CheckBox.font", Configuration.TEXT_FONT);
            UIManager.put("TextArea.font", Configuration.TEXT_FONT);
            UIManager.put("Label.font", Configuration.TEXT_FONT);
            UIManager.put("Slider.font", Configuration.TEXT_FONT);

            UIManager.put("Button.font", Configuration.TEXT_FONT);
            UIManager.put("Button.background", Configuration.BACKGROUND);
            UIManager.put("Button.margin", new Insets(1, 5, 1, 5));

            UIManager.put("TextArea.background", Color.white);

            UIManager.put("Label.background", Configuration.BACKGROUND);
            UIManager.put("Frame.background", Configuration.BACKGROUND);
            UIManager.put("Panel.background", Configuration.BACKGROUND);
            UIManager.put("RadioButton.background",Configuration.BACKGROUND);
            UIManager.put("CheckBox.background", Configuration.BACKGROUND);
            UIManager.put("Slider.background", Configuration.BACKGROUND);
            UIManager.put("ToolBar.background", Configuration.BACKGROUND);
            UIManager.put("Scrollbar.background", Configuration.BACKGROUND);
            UIManager.put("ScrollPane.background", Configuration.BACKGROUND);
            UIManager.put("Viewport.background", Configuration.BACKGROUND);
            UIManager.put("OptionPane.background", Configuration.BACKGROUND);
            UIManager.put("FileChooser.background", Configuration.BACKGROUND);
            UIManager.put("TabbedPane.background", Configuration.BACKGROUND);
            UIManager.put("TableHeader.background", Configuration.BACKGROUND);

        } catch (Exception e) {
            Debug.println("MainFrame.main(): UIManager couldn't set SystemLookAndFeel");
        }
    }

    private JMenuBar getMenuBar() {
        // file menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem menuItem = new JMenuItem("New Argument");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('N', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newCM();
            }
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Open Argument...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('O', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Close Argument");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('W', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        fileMenu.add(menuItem);

        fileMenu.addSeparator();

        menuItem = new JMenuItem("Save Argument As...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('S', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        fileMenu.add(menuItem);


        menuItem = new JMenuItem("Save Log As...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveLog();
            }
        });
        fileMenu.add(menuItem);

        menuItem = new JMenuItem("Save Encoding As...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveEncoding();
            }
        });
        fileMenu.add(menuItem);

        fileMenu.addSeparator();

        menuItem = new JMenuItem("Quit");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('Q', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // prompt to save code goes here
                quit();
            }
        });
        fileMenu.add(menuItem);

        // edit menu
        JMenu editMenu = new JMenu("Edit");

        menuItem = new JMenuItem("Add Hypothesis...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('H', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addHypDialog();
            }
        });
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Add Data...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('D', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDataDialog();
            }
        });
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Add Explanation...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('X', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addExpDialog();
            }
        });
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Add Contradiction...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('C', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addContDialog();
            }
        });

        editMenu.addSeparator();

        menuItem = new JMenuItem("Edit Selected Propositions");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('E', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editSelected();
            }
        });
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Unselect All");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('U', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePanels();
            }
        });
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Delete Selected");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelected();
            }
        });
        editMenu.add(menuItem);

        menuItem = new JMenuItem("Delete All");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteArgument();
            }
        });
        editMenu.add(menuItem);

        editMenu.addSeparator();

        menuItem = new JMenuItem("Clear Log");
        menuItem.addActionListener(new ActionListener() { // handle clear request
            public void actionPerformed(ActionEvent e) {
                clearLog();
            }
        });
        editMenu.add(menuItem);

        // simulation menu
        JMenu simulationMenu = new JMenu("Simulation");

        menuItem = new JMenuItem("Run");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('R', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runECHO();
           }
        });
        simulationMenu.add(menuItem);

        menuItem = new JMenuItem("Set parameters...");
        menuItem.setAccelerator(KeyStroke.getKeyStroke('P', java.awt.Event.CTRL_MASK, false));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showParams();
            }
        });
        simulationMenu.add(menuItem);

        // menubar
        JMenuBar menubar = new JMenuBar();
        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(simulationMenu);

        return menubar;
    }

}

