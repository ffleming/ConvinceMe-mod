package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.Link;
import com.codeguild.convinceme.model.LinkVector;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * <p>Description: Pane that contains a table of links</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */
public class LinkPanel extends JScrollPane {

    private JTable mLinkTable;
    private LinkTableDataModel mModel;

    public LinkPanel(String labeltext) {
        mModel = new LinkTableDataModel(labeltext);
        mLinkTable = new JTable(mModel);

        setViewportView(mLinkTable);
    }

    public Dimension getPreferredSize() {
        return new Dimension(700, 100);
    }

    public int[] getIndexes() {
        return mLinkTable.getSelectedRows();
    }

    public void update(LinkVector lv) {
        int i = 0;
        String[][] data = new String[lv.size()][1];
        for (Enumeration e = lv.elements(); e.hasMoreElements();) {
            Link l = (Link) (e.nextElement());
            data[i][0] = l.getText();
            i = i + 1;
        }
        mModel.updateTable(data);
    }
}