package com.codeguild.convinceme.gui;

import java.awt.*;

/**
 * <p>Description: A contradiction edge that can draw itself.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 * @author Forrest Fleming
 */
public class ContradictionEdge extends Edge {

    public ContradictionEdge() {
    }

    public void drawEdge(Graphics2D g, int x1, int y1, int x2, int y2) {
        BasicStroke old_stroke = (BasicStroke)g.getStroke();
    	BasicStroke dashed_stroke = new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 1.0f, new float[] {10.0f}, 0.0f );
        g.setStroke(dashed_stroke);
        g.setColor(Color.magenta);
        g.drawLine(x1,y1,x2,y2);
        g.setStroke(old_stroke);
    }
}
