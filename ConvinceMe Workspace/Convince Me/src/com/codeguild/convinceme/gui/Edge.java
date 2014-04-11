package com.codeguild.convinceme.gui;

import java.awt.*;

/**
 * <p>Description: An explanation edge that can draw itself.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 * @author FSF
 */

public class Edge {
    protected Node mFrom, mTo;

    public void setFrom(Node n) {
        mFrom = n;
    }

    public void setTo(Node n) {
        mTo = n;
    }
    
    public Node getFrom() {
    	return mFrom;
    }
    
    public int getLength() {
    	int vx = mTo.getX() - mFrom.getX();
		int vy = mTo.getY() - mFrom.getY();
		//Debug.println("vx="+vx+", vy="+vy);
		return (int) Math.sqrt(vx * vx + vy * vy);
    }

    public Node getTo() {
    	return mTo;
    }
    
    public void draw(Graphics gr) {
    	Graphics2D g = (Graphics2D)gr;
        Color old = g.getColor();
        int x1 = mFrom.getX();
        int y1 = mFrom.getY();
        int x2 = mTo.getX();
        int y2 = mTo.getY();
        drawEdge(g, x1, y1, x2, y2);
        g.setColor(old);
    }

    public void drawEdge(Graphics2D g, int x1, int y1, int x2, int y2) {
    	BasicStroke regular_stroke = new BasicStroke (2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
    	g.setStroke(regular_stroke);
    	g.setColor(Color.black);
        g.drawLine(x1, y1, x2, y2);
    }
}




