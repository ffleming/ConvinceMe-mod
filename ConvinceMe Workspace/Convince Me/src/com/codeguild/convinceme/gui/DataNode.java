package com.codeguild.convinceme.gui;

import com.codeguild.convinceme.model.Proposition;

import java.awt.*;

/**
 * <p>Description: A data node that can draw itself</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class DataNode extends Node {

    public DataNode(Proposition p) {
        super(p);
    }

    public void drawNode(Graphics2D g, int w, int h) {
    	BasicStroke regular_stroke = new BasicStroke (2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    	g.setStroke(regular_stroke);
        g.setColor((Color.green).brighter());
        g.fillRect(getX() - w / 2, getY() - h / 2, w, h);
        g.setColor(Color.black);
        g.drawRect(getX() - w / 2, getY() - h / 2, w - 1, h - 1);
    }
}