package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.gui.*;
import com.codeguild.convinceme.model.Link;
import com.codeguild.convinceme.model.LinkVector;
import com.codeguild.convinceme.model.Proposition;
import com.codeguild.convinceme.model.PropositionVector;
import com.codeguild.convinceme.utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;

/**
 * <p>Description: Panel on which graph is drawn.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 * Modified by FSF
 */

public class GraphPanel extends JPanel implements Runnable {

    private int mNnodes, mNedges;
    private Node mNodes[], mSelectedNode, mEnteredNode;
    private Edge mEdges[];
    private DiagramPanel mDiagramPanel;

    public GraphPanel(DiagramPanel diagramPanel) {
        initGraph();
        mDiagramPanel = diagramPanel;
        setLayout(new BorderLayout(1, 1));
        setSize(getPreferredSize());
        setBackground(Color.white);
        repaint();
        
        //Click on nodes
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                double bestdist = Double.MAX_VALUE;
                int x = e.getX();
                int y = e.getY();
                for (int i = 0; i < mNnodes; i++) {
                    Node n = mNodes[i];
                    double dist = (n.getX() - x) * (n.getX() - x) + (n.getY() - y) * (n.getY() - y);
                    if (dist < bestdist) {
                        mSelectedNode = n;
                        bestdist = dist;
                    }
                }
                if (mSelectedNode != null) {
                    mSelectedNode.setX(x);
                    mSelectedNode.setY(y);
                    repaint();
                }
            }
        });
        
        //Check for hovering over a node
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                double threshHold = 652; // 25 squared
                double mindist = threshHold;
                int x = e.getX();
                int y = e.getY();
                for (int i = 0; i < mNnodes; i++) {
                    Node n = mNodes[i];
                    double dist = (n.getX() - x) * (n.getX() - x) + (n.getY() - y) * (n.getY() - y);
                    if (dist < mindist) {
                        mEnteredNode = n;
                        mindist = dist;
                    }
                }
                if (mindist < threshHold) { // got within threshhold pixels of a node
                    mDiagramPanel.setText(mEnteredNode.getFullText());
                }
            }
        });
        
        //Clear the diagrap panel text when the mouse is no longer hovering over a node
        this.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
                mEnteredNode = null;
                mDiagramPanel.setText("");
            }
        });
        
        //Move nodes around when dragging
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (mSelectedNode != null) {
                    int x = e.getX();
                    int y = e.getY();
                    mSelectedNode.setX(x);
                    mSelectedNode.setY(y);
                    repaint();
                }
            }
        });

        //Set node location when mouse is released
        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (mSelectedNode != null) {
                    int x = e.getX();
                    int y = e.getY();
                    mSelectedNode.setX(x);
                    mSelectedNode.setY(y);
                    mSelectedNode = null;
                    repaint();
                }
            }
        });
    }

    public void initGraph() {
        mNnodes = 0;
        mNedges = 0;
        mNodes = new Node[100];
        mEdges = new Edge[200];
    }

    public void setGraph(PropositionVector h, PropositionVector d, LinkVector e, LinkVector c) {
        initGraph();
        graph(h);
        graph(d);
        graph(e);
        graph(c);
        repaint();
    }

    public void graph(PropositionVector pv) {
        for (Enumeration e = pv.elements(); e.hasMoreElements();) {
            Proposition p = (Proposition) (e.nextElement());
            addNode(p);
        }
    }

    public void graph(LinkVector lv) {
        for (Enumeration e = lv.elements(); e.hasMoreElements();) {
            addEdge((Link) e.nextElement());
        }
    }

    private int findNode(Proposition p) {
        for (int i = 0; i < mNnodes; i++) {
            if (mNodes[i].getLabel().equals(p.getLabel())) {
                return i;
            }
        }
        return addNode(p);
    }

    private int addNode(Proposition p) {
        Node n;
        int w = 200;
        int h = 200;
        if (p.isData()) {
            n = new DataNode(p);
            if (!p.isPlotted()) {
                p.setX((int) (20 + (w - 40) * Math.random()));
                p.setY((int) (h / 2 + 20 + (h / 2 - 40) * Math.random()));
            }
        } else {
            n = new HypothesisNode(p);
            if (!p.isPlotted()) {
                p.setX((int) (20 + (w - 40) * Math.random()));
                p.setY((int) (20 + (h / 2 - 40) * Math.random()));
            }
        }
        mNodes[mNnodes] = n;
        return mNnodes++;
    }

    private void addEdge(Link l) {
        Edge edge;
        PropositionVector starts = l.getExplainers();
        Proposition end = l.getExplained();

        for (Enumeration e = starts.elements(); e.hasMoreElements();) {
            if (l.isContradiction()) {
                edge = new ContradictionEdge();
            } else {
                edge = new ExplanationEdge();
            }
            edge.setFrom(mNodes[findNode((Proposition) e.nextElement())]);
            edge.setTo(mNodes[findNode(end)]);
            mEdges[mNedges++] = edge;
        }
    }

    // Override update so it doesn't clear background and flicker
    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, getSize().width, getSize().height);
        g.setColor(Color.white);
        g.fillRect(0, 0, getSize().width - 2, getSize().height - 2);
        g.setColor(Color.black);
        g.drawRect(0, 0, getSize().width - 2, getSize().height - 2);
        
        // draw all nodes and edges
        for (int i = 0; i < mNedges; i++) {
            mEdges[i].draw(g);
        }
        for (int i = 0; i < mNnodes; i++) {
            mNodes[i].draw(g);
        }
    }
    public void run() {
  	  	while (true) {
  	  		relax();
  	  		try {
  	  			Thread.sleep(100);
  	  		} 
  	  		catch (InterruptedException e) {
  	  			break;
  	  		}
  	  	}
    }
    
    synchronized public void relax() {

  	  /*Iterate through the edges
  	   * vx is the difference in X values between the start and endpoints of the edge
  	   * vy is the same for the Y values
  	   * len is, of course, the length as calculated from the legs of the triangle made up of vx and vy
  	   * 
  	   * 
  	   * No idea what node.dx and node.dy are
  	   */
    	for (int i = 0 ; i < mNedges ; i++) {
    		Edge e = mEdges[i];
    		float vx = e.getTo().getX() - e.getFrom().getX();
  		  	float vy = e.getTo().getY() - e.getFrom().getY();
  		  	//System.out.println("vx="+vx+", vy="+vy);
  		  	float len =  (float) Math.sqrt((vx * vx) + (vy * vy));
  		  
  		  	//double f = (edges[i].len - len) / (len * 3) ;
  		  	//What I put below seems to be equivalent
  		  	float f = (e.getLength() - len) / (len * 3) ;
  		  	Debug.println("Node: " + e.toString() + " e.getLength(): " + e.getLength() + " f: " + f + " len: " + len);
  		  	
  		  	float dx = f * vx;
  		  	float dy = f * vy;
  		  	
  		  	e.getTo().setDx(e.getTo().getDx() + dx);
  		  	e.getTo().setDy(e.getTo().getDy() + dy);
  		 	e.getFrom().setDx(e.getFrom().getDx() - dx);
  		 	e.getFrom().setDy(e.getFrom().getDy() - dy);
  		 	
    	}
    	//System.out.println("--end loop 1--------------------");
      
    	for (int i = 0 ; i < mNnodes ; i++) {
    		Node n1 = mNodes[i];
    		float dx = 0;
    		float dy = 0;
        
    		for (int j = 0 ; j < mNnodes ; j++) {
    			if (i == j) {
    				continue;
    			}
    			Node n2 = mNodes[j];
    			float vx = n1.getX() - n2.getX();
    			float vy = n1.getY() - n2.getY();
    			float len = vx * vx + vy * vy;
    			if (len == 0) {
    				dx += Math.random();
    				dy += Math.random();
    			} else if (len < 100 * 100) {
    				dx += vx / len;
    				dy += vy / len;
    			}
    		}
    		float dlen = dx * dx + dy * dy;
    		if (dlen > 0) {
    			dlen = (float)Math.sqrt(dlen) / 2;
  			  	n1.setDx(n1.getDx()+ (dx / dlen));
  			  	n1.setDy(n1.getDy()+ (dy / dlen));
  		  	}
    		//Debug.println("n1.x="+n1.getX()+", n1.y="+n1.getY());
    		//Debug.println(", n1.dx="+n1.getDx()+", n1.dy="+n1.getDy());
    	}
    	//System.out.println("--end loop 2--------------------");
      
    	//Dimension d = size();
    	//System.out.println("d="+d);
  	  
    	/*
    	 * Iterate through all nodes
    	 * 
    	 */
    	for (int i = 0 ; i < mNnodes ; i++) {
    		Node n = mNodes[i];
  		  	
    		//Keep evidence in a fixed location - only move hypotheses
    		if (n.getClass() == com.codeguild.convinceme.gui.HypothesisNode.class) {
	    		float new_x = (float)n.getX();
	    		new_x = new_x + Math.max(-5,Math.min(5,n.getDx()));
	    		n.setX((int)new_x);
	    		
	    		float new_y = (float)n.getY();
	    		new_y = new_y + Math.max(-5,Math.min(5,n.getDy()));
	    		
	    		n.setY((int)new_y);
	    		
	    		int buffer_zone = 50;
	  		  	if (n.getX() < buffer_zone) {
	  		  		//Debug.println("setting n.x to 0");
	  		  		n.setX(buffer_zone);
	  		  	} else if (n.getX() > (this.getWidth() - buffer_zone)) {
	  		  		n.setX(this.getWidth() - buffer_zone);
	  		  	}
	  		  	if (n.getY() < buffer_zone) {
	  		  		//Debug.println("setting n.y to 0");
	  		  		n.setY(buffer_zone);
	  		  	} else if (n.getY() > (this.getHeight() - buffer_zone)) {
	  		  		n.setY(this.getHeight() - buffer_zone);
	  		  	}
  			 
  	  		}
//  		  	n.setDx(n.getDx()/2);
  //		  	n.setDy(n.getDy()/2);
  		  	//System.out.print("n.x="+n.x+", n.y="+n.y);
  		  	//System.out.println(", n.dx="+n.dx+", n.dy="+n.dy);
    	}
    	//System.out.println("--end loop 3--------------------");
    	//repaint();
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
    
    
}

