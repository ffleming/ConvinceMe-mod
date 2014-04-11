package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.Proposition;
import com.codeguild.convinceme.model.PropositionVector;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Enumeration;

/**
 * <p>Description: Scrolling table of propositions</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */

public class PropositionPanel extends JScrollPane {

    private JTable mPropTable;
    private PropositionTableModel mModel;

    public PropositionPanel(String labeltext) {
        mModel = new PropositionTableModel(labeltext);

        TableColumnModel cm = new DefaultTableColumnModel() {
            int n = 0;
            public void addColumn(TableColumn tc) {
                n = n + 1;
                if (n == 2) {
                    // proposition column, make it wider
                    tc.setMinWidth(200);
                } else {
                    tc.setMaxWidth(50);
                }
                super.addColumn(tc);
            }
        };

        mPropTable = new JTable(mModel, cm);
        mPropTable.createDefaultColumnsFromModel();
        mPropTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setViewportView(mPropTable);
    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 100);
    }

    public int[] getIndexes() {
        return mPropTable.getSelectedRows();
    }

    public void update(PropositionVector pv) {
        int i = 0;
        String[][] data = new String[pv.size()][4];
        for (Enumeration e = pv.elements(); e.hasMoreElements();) {
            Proposition p = (Proposition) (e.nextElement());
            data[i][0] = p.getLabel();
            data[i][1] = p.getPropText();
            data[i][2] = p.getRatingText();
            data[i][3] = p.getActivationText();
            i = i + 1;
        }
        mModel.updateTable(data);
    }

}


