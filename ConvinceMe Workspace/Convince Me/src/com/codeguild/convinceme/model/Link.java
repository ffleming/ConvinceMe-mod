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

    //For multiple competing with multiple
    /* TODO
    
    private int index_of_first_b_competitor;
    
    */
    
    
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
    //I don't like this - shouldn't mess around in ECHOSimulation for this.
    public void setWeights(float w, boolean divWeight) {
    	//if divweight is false, simplicity impact is 0.  It's a joke! But really we could use anything, since it won't be used at all.
    	assert divWeight == false;
    	setWeights(w, divWeight, 0);
    }
    
    public void setWeights(float w, boolean divWeight, float simplicity_impact) {
    	//Good, doesn't care if we have contradictions or explanations, and so divides inhibition if divWeight is true
    	
    	int n = mProps.size();
    	float divided_weight = w;
        Proposition last = (Proposition) mProps.lastElement();

        if(divWeight) {
        	divided_weight = (float) w / (float)Math.pow(n-1, simplicity_impact);
        }
        
        for (int i = 0; i < mProps.size() - 1; i++) {
        	getPropAt(i).addWeight(last, divided_weight);
        	last.addWeight(getPropAt(i), divided_weight);

            // if there are multiple propositions in the explanation,
            // establish links between pairs
        	//Do this by adding links between the first n-1 propositions of the explanation
            for (int j = i + 1; j < mProps.size() - 1; j++) {
                if(mType == Link.EXPLAIN) {
                	getPropAt(i).addWeight(getPropAt(j), w);
                	getPropAt(j).addWeight(getPropAt(i), w);
                }
            }
                  
        }
       
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
    /**
     * Get the contradictory links in this element
     * e.g. if P1 P2 jointly contradict P3 then it would return P1 and P2
     * @return A vector of contradicting propositions
     */
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
     * Get the target of this link, e.g., if the link is P1 explains P2 then it would return P2
     * @return The last proposi
     */
    public Proposition getExplained() {
        return (Proposition) mProps.lastElement();
    }

    /**
     * Get the target of this link, e.g., if the link is P1 contradicts P2 then it would return P2
     * @return The last proposition
     */
    public Proposition getContradicted() {
        return (Proposition) mProps.lastElement();
    }
    
    /**
     * Get the text encoding of this Link, e.g. 'P1 explains P2'
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

