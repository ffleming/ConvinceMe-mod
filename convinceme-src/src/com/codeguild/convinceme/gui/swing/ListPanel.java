package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.LinkVector;
import com.codeguild.convinceme.model.PropositionVector;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Description: A panel that holds lists of propositions and links</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */
public class ListPanel extends JPanel {

    protected PropositionPanel mHypPanel, mDataPanel;
    protected LinkPanel mExpPanel, mContPanel;
    protected ToolPanel mToolPanel;

    public ListPanel(MainFrame controller) {
        mToolPanel = new ToolPanel(controller);
        mHypPanel = new PropositionPanel("Hypotheses");
        mDataPanel = new PropositionPanel("Data");
        mExpPanel = new LinkPanel("Explanations");
        mContPanel = new LinkPanel("Contradictions");

        setLayout(new BorderLayout(5,5));

        JPanel lists = new JPanel(new GridLayout(4,1,10,10));
        lists.add(mHypPanel);
        lists.add(mDataPanel);
        lists.add(mExpPanel);
        lists.add(mContPanel);

        add(mToolPanel, BorderLayout.NORTH);
        add(lists, BorderLayout.CENTER);
    }

    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    public void setText(PropositionVector h, PropositionVector d, LinkVector e, LinkVector c) {
        mHypPanel.update(h);
        mDataPanel.update(d);
        mExpPanel.update(e);
        mContPanel.update(c);
    }

    public int[] getSelectedHyps() {
        return mHypPanel.getIndexes();
    }

    public int[] getSelectedData() {
        return mDataPanel.getIndexes();
    }

    public int[] getSelectedExps() {
        return mExpPanel.getIndexes();
    }

    public int[] getSelectedConts() {
        return mContPanel.getIndexes();
    }
}

