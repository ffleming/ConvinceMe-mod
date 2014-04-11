package com.codeguild.convinceme.gui.swing;

import javax.swing.table.AbstractTableModel;

/**
 * <p>Description: A model for the proposition table</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class PropositionTableModel extends AbstractTableModel {

    private String[][] mData;
    private String[] mHeaders;

    public PropositionTableModel(String kind) {
        mData = new String[0][0];
        mHeaders = new String[4];
        mHeaders[0] = "Label";
        mHeaders[1] = kind;
        mHeaders[2] = "You";
        mHeaders[3] = "ECHO";
    }

    public String getColumnName(int c) {
        return mHeaders[c];
    }

    public int getRowCount() {
        return mData.length;
    }

    public int getColumnCount() {
        return mHeaders.length;
    }

    public Object getValueAt(int r, int c) {
        return mData[r][c];
    }

    public void updateTable(String[][] data) {
        mData = data;
        fireTableDataChanged();
        fireTableRowsUpdated(0, mData.length - 1);
    }

//		public void setValueAt (Object v, int r, int c) {
//			if ((r < NROWS) && (c < NCOLS)) {
//				mData[r][c] = (String)v;
//				}
//			}

    public void clear() {
        mData = new String[0][0];
    }
}

