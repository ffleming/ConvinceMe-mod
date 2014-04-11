package com.codeguild.convinceme.gui.swing;

import javax.swing.table.AbstractTableModel;

/**
 * <p>Description: A model for the Link tablef</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class LinkTableDataModel extends AbstractTableModel {

    private static final int NCOLS = 1;

    private String[][] mData;
    private String[] mHeaders;

    public LinkTableDataModel(String label) {
        mData = new String[0][0];
        mHeaders = new String[NCOLS];
        mHeaders[0] = label;
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

    public void clear() {
        mData = new String[0][0];
    }
}

