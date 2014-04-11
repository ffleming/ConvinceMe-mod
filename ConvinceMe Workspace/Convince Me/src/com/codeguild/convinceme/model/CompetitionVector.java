package com.codeguild.convinceme.model;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <p>Description: A serializable vector that holds competitions</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
/*
public class CompetitionVector extends Vector implements Serializable {

    public CompetitionVector(Competition c) {
        addElement(c);
    }

    //
    // Concatenate a competition vector to the end of me
    // @param cv The CompetitionVector to concatenate
    // @return A new CompetitionVector with this concatenated
    //
    public CompetitionVector concatenate(CompetitionVector cv) {
        CompetetionVector ret = new LinkVector();
        for (Enumeration e = this.elements(); e.hasMoreElements();) {
            ret.addElement((Competition) e.nextElement());
        }
        for (Enumeration e = cv.elements(); e.hasMoreElements();) {
            ret.addElement((Competition) e.nextElement());
        }
        return ret;
    }
    
    //
    // Set the weight of all the the links
    // @param w The weight to set
    // @param divWeight True if the weight should be divided among multiple links
    //
    public void setWeights(float w, boolean divWeight) {
    	for (Enumeration e = this.elements(); e.hasMoreElements();) {
            ((Competition) e.nextElement()).setWeights(w, divWeight);
        }
    }
}
*/

