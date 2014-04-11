package com.codeguild.convinceme.model;

import org.dom4j.Element;
import org.dom4j.DocumentFactory;

import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import com.codeguild.convinceme.utils.Debug;
/**
 * <p>Description: A serializable competition .</p>
 */
public class Competition extends Object  {

    public static final int EXPLAIN = 0;
    public static final int CONTRADICT = 1;
    public static final int COMPETE = 2;

    public static final String EXPLAIN_TEXT = "explains";
    public static final String JOINT_EXPLAIN_TEXT = "jointly explain";
    public static final String CONTRADICT_TEXT = "contradicts";
    public static final String JOINT_CONTRADICT_TEXT = "jointly contradict";
    
    private Link mExplanationOne = new Link(Link.EXPLAIN);
    private Link mExplanationTwo = new Link(Link.EXPLAIN);
    private float mWeight = 0;

    public Competition(Link exp1, Link exp2) {
        mExplanationOne = exp1;
        mExplanationTwo = exp2;
    }

    public Link getExplanationOne() {
    	return mExplanationOne;
    }

    public Link getExplanationTwo() {
    	return mExplanationTwo;
    }
    
    /**
     * Set the weights of this link. If there are multiple propositions
     * in the link, it establishes links between pairs
     * @param w The weight to set
     * @param divWeight if true, divide the weight between
     */
    
    //Convenience class so I don't have to re-write what's below too badly.
    public void setWeights(float w, boolean divWeight) {
    	setWeights(w, divWeight, ECHOSimulation.SIMPLICITY);
    }
    
    public void setWeights(float w, boolean divWeight, float simplicity_impact) {
  	  	PropositionVector exp1_explainers = mExplanationOne.getExplainers();
  	  	PropositionVector exp2_explainers = mExplanationTwop.getExplainers();
    	float divided_weight = w;
        Proposition last = (Proposition) mProps.lastElement();

        if(divWeight) {
        	divided_weight = (float) w / (float)Math.pow(n-1, simplicity_impact);
        }
        
        for (int i = 0; i < mProps.size() - 1; i++) {
            // add symmetric weights
        	getPropAt(i).addWeight(last, divided_weight);
        	last.addWeight(getPropAt(i), divided_weight);

            // if there are multiple propositions in the link,
            // establish links between pairs
        	/*
            for (int j = i + 1; j < mProps.size() - 1; j++) {
                if(mType == Competition.EXPLAIN) {
                	getPropAt(i).addWeight(getPropAt(j), w);
                	getPropAt(j).addWeight(getPropAt(i), w);
                }
            }
            */
        }
        mWeight = w;
    }

    public float getWeight() {
    	return mWeight;
    }
 }

