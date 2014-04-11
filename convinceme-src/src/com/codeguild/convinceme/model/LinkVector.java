package com.codeguild.convinceme.model;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Description: A serializable vector that holds links</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class LinkVector extends Vector implements Serializable {

    public LinkVector(Link l) {
        addElement(l);
    }

    public LinkVector() {
    }

    /**
     * Concatenate a link vector to the end of me
     * @param lv The LinkVector to concatenate
     * @return A new LinkVector with this concatenated
     */
    public LinkVector concatenate(LinkVector lv) {
        LinkVector l = new LinkVector();
        for (Enumeration e = this.elements(); e.hasMoreElements();) {
            l.addElement((Link) e.nextElement());
        }
        for (Enumeration e = lv.elements(); e.hasMoreElements();) {
            l.addElement((Link) e.nextElement());
        }
        return l;
    }
    
    /**
     * Set the weight of all the the links
     * @param w The weight to set
     * @param divWeight True if the weight should be divided among multiple links
     */
    public void setWeights(float w, boolean divWeight) {
        for (Enumeration e = this.elements(); e.hasMoreElements();) {
            ((Link) e.nextElement()).setWeights(w, divWeight);
        }
    }
}

