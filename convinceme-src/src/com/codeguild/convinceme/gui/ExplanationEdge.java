package com.codeguild.convinceme.gui;

import java.awt.*;

/**
 * <p>Description: An explanation edge that can draw itself.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class ExplanationEdge extends Edge {

    public ExplanationEdge() {
    }

    public void drawEdge(Graphics2D g, int x1, int y1, int x2, int y2) {
    	BasicStroke regular_stroke = new BasicStroke (2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    	g.setStroke(regular_stroke);
    	
    	g.setColor(Color.black);
        g.drawLine(x1, y1, x2, y2);
    }
}