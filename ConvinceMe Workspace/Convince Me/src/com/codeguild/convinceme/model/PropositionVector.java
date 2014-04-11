package com.codeguild.convinceme.model;

import com.codeguild.convinceme.utils.Debug;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Description: A vector that holds propositions</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class PropositionVector extends Vector implements Serializable {

    public PropositionVector() {
    }

    public PropositionVector(Proposition p) {
        addElement(p);
    }

    /**
     * Get proposition at given position
     * @param i The position
     * @return The proposition at that position
     */
    public Proposition getPropAt(int i) {
        Proposition prop = null;
        try {
            prop = (Proposition) elementAt(i);
        } catch (Exception e) {
            Debug.printStackTrace(e);
        }
        return prop;
    }

    /**
     * Get a vector of propositions at the given indices
     * (used when select propositions from a list);
     * @param indices The indices to get
     * @return The new vector of just these propositions
     */
    public PropositionVector getProps(int[] indices) {
        PropositionVector prop = new PropositionVector();
        try {
            for (int i = 0; i < indices.length; i++) {
                prop.addElement(getPropAt(indices[i]));
            }
        } catch (Exception e) {
            Debug.printStackTrace(e);
        }
        return prop;
    }

    /**
     * Get the labels for all my propositions
     * @return A String with all proposition labels
     */
    public String getLabelsText() {
        StringBuffer labels = new StringBuffer();
        Proposition prop;
        for (Enumeration e = this.elements(); e.hasMoreElements();) {
            prop = (Proposition) e.nextElement();
            labels.append(prop.getLabel());
            if (e.hasMoreElements()) {
            	labels.append(", ");
            }
        }
        return labels.toString();
    }

    /**
     * Concatenate a proposition vector to the end of me, creating
     * a new vector that is returned
     * @param p2 The vector of propositions to add to me
     * @return The new vector with concatinated values
     */
    public PropositionVector concatenate(PropositionVector p2) {
        PropositionVector p = new PropositionVector();
        for (Enumeration e = this.elements(); e.hasMoreElements();) {
            p.addElement((Proposition) e.nextElement());
        }
        for (Enumeration e = p2.elements(); e.hasMoreElements();) {
            p.addElement((Proposition) e.nextElement());
        }
        return p;
    }

    /**
     * Set the activations of all my propositions
     * @param f The value to set for all my propositions
     */
    public void setActivations(float f) {
        for (Enumeration e = this.elements(); e.hasMoreElements();) {
            ((Proposition) e.nextElement()).setActivation(f);
        }
    }

    /**
     * Initialize the weights of all my propositions
     */
    public void initWeights() {
        for (Enumeration e = this.elements(); e.hasMoreElements();) {
            ((Proposition) e.nextElement()).initWeights();
        }
    }
}

