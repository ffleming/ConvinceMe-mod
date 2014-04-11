package com.codeguild.convinceme.gui;

import com.codeguild.convinceme.model.Proposition;

import java.awt.*;

/**
 * <p>Description: A hypothesis node that can draw itself</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author patti
 */
public class HypothesisNode extends Node {

    public HypothesisNode(Proposition p) {
        super(p);
    }

    public void drawNode(Graphics2D g, int w, int h) {
    	BasicStroke regular_stroke = new BasicStroke (2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    	g.setStroke(regular_stroke);
    	
    	g.setColor((Color.yellow).brighter());
        w += 8;
        h += 6;
        g.fillOval(getX() - w / 2, getY() - h / 2, w, h);
        g.setColor(Color.black);
        g.drawOval(getX() - w / 2, getY() - h / 2, w - 1, h - 1);
    }
}