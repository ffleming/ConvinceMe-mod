package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.LinkVector;
import com.codeguild.convinceme.model.PropositionVector;

import javax.swing.*;
import java.awt.*;

/**
 * <p>Description: Panel that shows the diagram and status information</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */
public class DiagramPanel extends JPanel {

    protected GraphPanel mGraphPanel;
    protected ToolPanel mToolPanel;
    protected JTextField mStatusField;

    public DiagramPanel(MainFrame controller) {
        setLayout(new BorderLayout(1, 1));

        mToolPanel = new ToolPanel(controller);
        mStatusField = new JTextField("Move the mouse over a node; text appears here.", 80);
        mGraphPanel = new GraphPanel(this);

        add(new JScrollPane(mGraphPanel), BorderLayout.CENTER);
        add(mToolPanel, BorderLayout.NORTH);
        add(mStatusField, BorderLayout.SOUTH);
    }

    public void showGraph() {
        setVisible(true);
    }

    public void setText(String s) {
        mStatusField.setText(s);
    }

    public void setGraph(PropositionVector h, PropositionVector d, LinkVector e, LinkVector c) {
        mGraphPanel.setGraph(h, d, e, c);
    }

    public void graph(PropositionVector pv) {
        mGraphPanel.graph(pv);
    }

    public void graph(LinkVector lv) {
        mGraphPanel.graph(lv);
    }
}