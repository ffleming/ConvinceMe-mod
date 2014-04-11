package com.codeguild.convinceme.model;

import org.dom4j.Element;
import org.dom4j.DocumentFactory;

import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;
import com.codeguild.convinceme.utils.Debug;
/**
 * <p>Description: A serializable link .</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class Link extends Object  {

    public static final int EXPLAIN = 0;
    public static final int CONTRADICT = 1;
    public static final int COMPETE = 2;

    public static final String EXPLAIN_TEXT = "explains";
    public static final String JOINT_EXPLAIN_TEXT = "jointly explain";
    public static final String CONTRADICT_TEXT = "contradicts";
    public static final String JOINT_CONTRADICT_TEXT = "jointly contradict";
    
    private PropositionVector mProps = new PropositionVector();
    private int mType = EXPLAIN;
    private float mWeight = 0;

    // for XML
    public static final String EXPLANATION_TYPE = "EXPLAINS";
    public static final String CONTRADICTION_TYPE = "CONTRADICTS";
    public static final String CONTRADICTED = "CONTRADICTED";
    public static final String EXPLAINED = "EXPLAINED";
    public static final String EXPLAINERS = "EXPLAINERS";

    public Link(PropositionVector props) {
        mProps = props;
    }

    public Link(int linkType) {
        mType = linkType;
    }

    public Link(PropositionVector props, int linkType) {
        super();
        mType = linkType;
        mProps = props;
    }

    /**
     * Set the type of Link this is (Contradition or Explanation)
     * @param type The type
     */
    public void setType(int type) {
        mType = type;
    }

    /**
     * Set the propositions for this link
     * @param pv The proposition vector
     */
    public void setProps(PropositionVector pv) {
        mProps = pv;
    }

    /**
     * Get the proposition at position i of this Link
     * @param pos The position
     * @return A proposition
     */
    public Proposition getPropAt(int pos) {
        return mProps.getPropAt(pos);
    }

    /**
     * @return The propositions in this Link
     */
    public PropositionVector getProps() {
        return mProps;
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
    	//Good, doesn't care if we have contradictions or explanations, and so divides inhibition if divWeight is true
    	
    	//This always returns 2 since this is a 2 part link. Oy.
    	int n = mProps.size();
    	float divided_weight = w;
        Proposition last = (Proposition) mProps.lastElement();

        // divide weight among links?
        //This doesn't allow SimplicityImpact to be set. Let's do it right!
/*        if (divWeight) {
            w = (float) w / (n - 1);
        }
 */
        /* if there are 2 propositions, n is 2
         * 		and so we divide w by (2-1)^x, which is 1^x, which is 1
         * if there are three propositions, n is 3
         * 		and so we divide w by 2^x
         * if simplicity_impact is 0, then there's no impact and we divide by (n-1)^0, which is 1 every time
         */
        if(divWeight) {
        	divided_weight = (float) w / (float)Math.pow(n-1, simplicity_impact);
        	/*float w2 = (float) w / (n - 1);
        	Debug.println("Simplicity: " + simplicity_impact);
            Debug.println("Co-explainers: " + (n-1));
            Debug.println("w: " + w);
            Debug.println(w == w2);*/
        }
        
        for (int i = 0; i < mProps.size() - 1; i++) {
            // add symmetric weights
//            getPropAt(i).addWeight(last, w);
//            last.addWeight(getPropAt(i), w);
        	getPropAt(i).addWeight(last, divided_weight);
        	last.addWeight(getPropAt(i), divided_weight);

            // if there are multiple propositions in the link,
            // establish links between pairs
            for (int j = i + 1; j < mProps.size() - 1; j++) {
                if(mType == Link.EXPLAIN) {
                	getPropAt(i).addWeight(getPropAt(j), w);
                	getPropAt(j).addWeight(getPropAt(i), w);
                }
            }
                  
        }
        /*
        Proposition root = null;
        PropositionVector collection = null;
        if(mType == Link.EXPLAIN) {
        	root = getExplained();
        	collection = getExplainers();
        	for( int i = 0; i < collection.size(); i++) {
        		getPropAt(i).addWeight(last, w);
                last.addWeight(getPropAt(i), w);
        	}
        	
        } else {
        	root = getContradicted();
        	collection = getJointContradictions();
        }
        */
        mWeight = w;
    }

    public float getWeight() {
    	return mWeight;
    }
    
    public boolean isSamePair(Link l) {
    	PropositionVector v1 = this.getProps();
    	PropositionVector v2 = l.getProps();
    	
    	assert (v1.size() !=2 || v2.size() != 2): "Links must have exactly two elements";
    	
    	String[] v1_labels = {v1.getPropAt(0).getLabel(), v1.getPropAt(1).getLabel()};
    	String[] v2_labels = {v2.getPropAt(0).getLabel(), v2.getPropAt(1).getLabel()};
    	Arrays.sort(v1_labels);
    	Arrays.sort(v2_labels);
    	return (v1_labels[0] == v2_labels[0]) && (v1_labels[1] == v2_labels[1]);
    	
    }
    
    /**
     * Get the sources of this link. E.g., if
     * P1 P2 explains P3 then it would return P1 and P2
     * @return A vector of explaining propositions
     */
    public PropositionVector getExplainers() {
        PropositionVector prop = new PropositionVector();
        int i = 0;
        for (Enumeration e = mProps.elements(); e.hasMoreElements() && (i < mProps.size() - 1); i++) {
            prop.addElement((Proposition) e.nextElement());
        }
        return prop;
    }
    
    public PropositionVector getJointContradictions() {
        PropositionVector joint_cons = new PropositionVector();
        int i = 0;
        for (Enumeration e = mProps.elements();
             e.hasMoreElements() && (i < mProps.size() - 1); i++) {
            joint_cons.addElement((Proposition) e.nextElement());
        }
        return joint_cons;
    }

    /**
     * Get the target of this link, e.g., if the link is
     * P1 explains P2 then it would return P2
     * @return The last proposi
     */
    public Proposition getExplained() {
        return (Proposition) mProps.lastElement();
    }

    //Same, for contradictions
    public Proposition getContradicted() {
        return (Proposition) mProps.lastElement();
    }
    
    /**
     * Get the text encoding of this Link, e.g.
     * P1 explains P2
     * @return The text encoding
     */
    public String getText() {
        StringBuffer text = new StringBuffer();
        int i = 0;
        for (Enumeration e = mProps.elements();
             e.hasMoreElements() && (i < mProps.size() - 1); i++) {
            text.append(((Proposition) e.nextElement()).getLabel());
            text.append(" ");
        }
        if (isExplanation()) {
            if (isJointExplanation()) {
            	text.append(JOINT_EXPLAIN_TEXT);
            } else {
            	text.append(EXPLAIN_TEXT);
            }
        } else {
        	if (isJointContradiction()) {
        		text.append(JOINT_CONTRADICT_TEXT);
        	} else {
        		text.append(CONTRADICT_TEXT);
        	}
        }
        text.append(" ");
        text.append(((Proposition) mProps.lastElement()).getLabel());
        return text.toString();
    }

    /**
     * @return true if this link is a contradiction link
     */
    public boolean isContradiction() {
        return (mType == CONTRADICT);
    }

    /**
     * @return true if this link is a explanation link
     */
    public boolean isExplanation() {
        return (mType == EXPLAIN);
    }

    /**
     * @return true if this link is a join explanation link
     */
    public boolean isJointExplanation() {
        return (isExplanation() && (mProps.size() > 2));
    }
    
    public boolean isJointContradiction() {
        return (isContradiction() && (mProps.size() > 2));
    }

    /**
     * @return XML for this proposition
     */
    public Element getXML() {
        String type = EXPLANATION_TYPE;
        if (isContradiction()) {
            type = CONTRADICTION_TYPE;
        }
        Element root = DocumentFactory.getInstance().createElement(type);
        if (isContradiction()) {
        	/*
            Proposition start = (Proposition) mProps.firstElement();
            type = Proposition.HYPOTHESIS_TYPE;
            if (start.isData()) {
                type = Proposition.DATA_TYPE;
            }
            Element cont = root.addElement(type);
            cont.addAttribute(Proposition.ID, start.getLabel());

            Proposition end = (Proposition) mProps.lastElement();
            type = Proposition.HYPOTHESIS_TYPE;
            if (end.isData()) {
                type = Proposition.DATA_TYPE;
            }
            cont = root.addElement(type);
            cont.addAttribute(Proposition.ID, end.getLabel());
            */
        	Proposition contradicted = getContradicted();
            type = Proposition.HYPOTHESIS_TYPE;
            if (contradicted.isData()) {
            	type = Proposition.DATA_TYPE;
            }
            Element contradictedRoot = root.addElement(type);
            contradictedRoot.addAttribute(Proposition.ID, contradicted.getLabel());
            contradictedRoot.addAttribute(CONTRADICTED, Argument.TRUE);
            
            PropositionVector contradictors = getJointContradictions();
            Enumeration e = contradictors.elements();
            while (e.hasMoreElements()){
                Proposition p = (Proposition)e.nextElement();
                type = Proposition.HYPOTHESIS_TYPE;
                if (p.isData()) {
                    type = Proposition.DATA_TYPE;
                }
                Element contradictorCollection = root.addElement(type);
                contradictorCollection.addAttribute(Proposition.ID, p.getLabel());
            }
            
        } else {
            Proposition explained = getExplained();
            type = Proposition.HYPOTHESIS_TYPE;
            if (explained.isData()) {
                type = Proposition.DATA_TYPE;
            }
            Element explainedRoot = root.addElement(type);
            explainedRoot.addAttribute(Proposition.ID,  explained.getLabel());
            explainedRoot.addAttribute(EXPLAINED, Argument.TRUE);

            PropositionVector explainers = getExplainers();
            Enumeration e = explainers.elements();
            while (e.hasMoreElements()){
                Proposition p = (Proposition)e.nextElement();
                type = Proposition.HYPOTHESIS_TYPE;
                if (p.isData()) {
                    type = Proposition.DATA_TYPE;
                }
                Element explainerCollection = root.addElement(type);
                explainerCollection.addAttribute(Proposition.ID, p.getLabel());
            }
        }

        return root;
    }

    /**
     * @return a proposition from XML
     */
    public static Link readXML(Element root, Argument arg) {
        Link link;
        PropositionVector propVector = new PropositionVector();
        boolean isExp = true;
        if (root.getName().equalsIgnoreCase(CONTRADICTION_TYPE)) {
            isExp = false;
        }
        if (isExp) {
            link = new Link(EXPLAIN);
            Proposition explained = null;
            List props = root.elements();
            for (Iterator iter = props.iterator(); iter.hasNext();) {
                Element element = (Element) iter.next();
                String label = element.attributeValue(Proposition.ID);
                Proposition prop = arg.getProposition(label);
                if (element.attributeValue(EXPLAINED) != null) {
                    explained = prop;
                } else {
                    propVector.addElement(prop);
                }
            }
            // add explained last
            propVector.addElement(explained);
        } else {
            // contradictions
        	// ORIGINAL CODE 
        	/*
            link = new Link(CONTRADICT);
            List props = root.elements();
            for (Iterator iter = props.iterator(); iter.hasNext();) {
                Element element = (Element) iter.next();
                String label = element.attributeValue(Proposition.ID);
                Proposition prop = arg.getProposition(label);
                propVector.addElement(prop);
            }*/
        	
            //MY CODE
            link = new Link(CONTRADICT);
            Proposition contradicted = null;
            List props = root.elements();
            for (Iterator iter = props.iterator(); iter.hasNext();) {
            	Element element = (Element) iter.next();
                String label = element.attributeValue(Proposition.ID);
                Proposition prop = arg.getProposition(label);
            	if (element.attributeValue(CONTRADICTED) != null) {
                    contradicted = prop;
                } else {
                    propVector.addElement(prop);
                }
            }
            propVector.addElement(contradicted);
            
        }

        link.setProps(propVector);
        return link;
    }
 }

