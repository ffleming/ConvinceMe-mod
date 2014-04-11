package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.Link;
import com.codeguild.convinceme.model.Proposition;
import com.codeguild.convinceme.model.PropositionVector;
import com.codeguild.convinceme.utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * <p>Description: Dialog that prompts for link information</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */

public class LinkEditPanel extends JPanel {

    protected static final String EXPLAIN_TEXT = "Explains:";
    protected static final String CONTRADICT_TEXT = "Contradicts:";
    protected static final String EXPLAIN_INST =
            "Select one or more propositions from the left, and one from the right.";
    protected static final String CONTRADICT_INST =
            "Select one proposition from each list.";

    private Link mLink;
    private MainFrame mConfig;
    private JList mStartList, mEndList;
    private PropositionVector mStartVector, mEndVector;

    private DefaultListModel mStartModel, mEndModel;

    public LinkEditPanel(MainFrame config, Link link) {
        mConfig = config;
        mLink = link;
        mStartVector = new PropositionVector();
        mEndVector = new PropositionVector();

        // create top list
        mStartModel = new DefaultListModel();
        mStartList = new JList(mStartModel);

        DefaultListSelectionModel m1 = new DefaultListSelectionModel();
        if (mLink.isExplanation()) {
            m1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
        	m1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            //m1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        	// Changed this to allow joint contradiction --FSF
        }
        m1.setLeadAnchorNotificationEnabled(false);
        mStartList.setSelectionModel(m1);

        JScrollPane startList = new JScrollPane(mStartList);

        // create bottom list
        mEndModel = new DefaultListModel();
        mEndList = new JList(mEndModel);

        DefaultListSelectionModel m2 = new DefaultListSelectionModel();
        m2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m2.setLeadAnchorNotificationEnabled(false);
        mEndList.setSelectionModel(m2);

        JScrollPane endList = new JScrollPane(mEndList);

        JPanel lists = new JPanel(new BorderLayout(2, 2));
        lists.add(startList, BorderLayout.NORTH);
        lists.add(endList, BorderLayout.SOUTH);

        JPanel wholePanel = new JPanel(new BorderLayout(2, 2));
        wholePanel.add(lists, BorderLayout.CENTER);

        if (mLink.isExplanation()) {
            lists.add(new JLabel("Explains:"), BorderLayout.CENTER);
            wholePanel.add(new JLabel("Select one or more propositions from the top, and one from the bottom."), BorderLayout.NORTH);
            wholePanel.add(new JLabel("(Shift-click to select multiple propositions for a joint explanation)"), BorderLayout.SOUTH);
        } else {
            lists.add(new JLabel("Contradicts:"), BorderLayout.CENTER);
            wholePanel.add(new JLabel("Select one proposition from each list."), BorderLayout.NORTH);
        }

        setLayout(new BorderLayout(2, 2));
        add(wholePanel, BorderLayout.CENTER);
        setSize(getPreferredSize());
    }

    public Dimension getPreferredSize() {
        return new Dimension(350, 400);
    }

    /**
     * Show this pane in a dialog, and save values if not cancelled
     */
    public void showDialog() {
        loadValues();
        String title;
        if (mLink.isExplanation()) {
            title = "Edit Explanation";
        } else {
            title = "Edit Contradiction";
        }
        int response = JOptionPane.showConfirmDialog(null,
                this,
                title,
                JOptionPane.OK_CANCEL_OPTION);

        if (response == JOptionPane.CANCEL_OPTION) {
            Debug.println("User cancelled link edit.");
        } else {
            Debug.println("Saving link.");
            saveValues();
        }
    }

    /**
     * Load initial values into the dialog
     */
    private void loadValues() {
        addPropsToStart(mConfig.getArg().getHypotheses()); // add starts as 2nd arg?
        addPropsToStart(mConfig.getArg().getData());
        addPropsToEnd(mConfig.getArg().getHypotheses());
        addPropsToEnd(mConfig.getArg().getData());
    }

    /**
     * Save values after editing
     */
    private void saveValues() {
        PropositionVector newp1 = mStartVector.getProps(mStartList.getSelectedIndices());
        PropositionVector newp2 = mEndVector.getProps(mEndList.getSelectedIndices());
        PropositionVector newp = newp1.concatenate(newp2);

        mLink.setProps(newp);
        if (mLink.isExplanation()) {
            mConfig.getArg().addExplanation(mLink);
        } else {
            mConfig.getArg().addContradiction(mLink);
        }
        mConfig.log("Adding link " + mLink.getText());
        mConfig.updatePanels();
    }

    private void addPropsToStart(PropositionVector pv) {
        Proposition p;
        for (Enumeration e = pv.elements(); e.hasMoreElements();) {
            p = (Proposition) e.nextElement();
            mStartModel.addElement(p.getText());
            mStartVector.addElement(p);
        }

    }

    private void addPropsToEnd(PropositionVector pv) {
        Proposition p;
        for (Enumeration e = pv.elements(); e.hasMoreElements();) {
            p = (Proposition) e.nextElement();
            mEndModel.addElement(p.getText());
            mEndVector.addElement(p);
        }
    }
}

