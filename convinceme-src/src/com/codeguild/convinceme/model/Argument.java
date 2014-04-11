package com.codeguild.convinceme.model;

import org.dom4j.Element;
import org.dom4j.DocumentFactory;

import java.util.Enumeration;
import java.util.List;
import java.util.Iterator;

import com.codeguild.convinceme.utils.Debug;

/**
 * <p>Description: Serializable argument. Made all data members
 * public instead of using getters and setters since serialization
 * writes methods as well, and don't want the overhead </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Codeguild</p>
 * @author Patti Schank
 */
public class Argument extends Object {

    public PropositionVector mHypotheses, mData;
    public LinkVector mExplanations, mContradictions;
    public final int mVersion = 1;
    public int mHypID = 0;
    public int mDataID = 0;
    public String mNotes;

    // for XML
    public static final String ARGUMENT = "ARGUMENT";
    public static final String NOTES = "NOTES";
    public static final String TRUE = "TRUE";

    public Argument() {
        mHypotheses = new PropositionVector();
        mData = new PropositionVector();
        mExplanations = new LinkVector();
        mContradictions = new LinkVector();
        mHypID = 0;
        mDataID = 0;
        mNotes = "";
    }

    /**
     * @return a proposition given a the proposition label/id
     */
    public Proposition getProposition(String label) {
        Proposition result = null;
        boolean found = false;

        Enumeration e = mHypotheses.elements();
        while (e.hasMoreElements() && !found) {
            result = (Proposition) e.nextElement();
            if (label.equalsIgnoreCase(result.getLabel())) {
                found = true;
            }
        }
        e = mData.elements();
        while (e.hasMoreElements() && !found) {
            result = (Proposition) e.nextElement();
            if (label.equalsIgnoreCase(result.getLabel())) {
                found = true;
            }
        }
        return result;
    }

    /**
     * @return All hypotheses in the current argument
     */
    public PropositionVector getHypotheses() {
        return mHypotheses;
    }

    /**
     * @return All data in the current argument
     */
    public PropositionVector getData() {
        return mData;
    }

    /**
     * @return a unique ID for a new hypothesis
     */
    public String getUniqueHypID() {
        int max = 0;
        Enumeration e = mHypotheses.elements();
        while (e.hasMoreElements()) {
            Proposition p = (Proposition) e.nextElement();
            String digit = p.getLabel().substring(1);
            try {
                max = Math.max(max, Integer.parseInt(digit));
            } catch (Exception ex) {
                max = max + 1;
            }
        }
        int next = max + 1;
        return "H" + next;
    }

    /**
     * @return a unique ID for a new hypothesis
     */
    public String getUniqueDataID() {
        int max = 0;
        Enumeration e = mData.elements();
        while (e.hasMoreElements()) {
            Proposition p = (Proposition) e.nextElement();
            String digit = p.getLabel().substring(1);
            try {
                max = Math.max(max, Integer.parseInt(digit));
            } catch (Exception ex) {
                max = max + 1;
            }
        }
        int next = max + 1;
        return "E" + next;    }

    /**
     * @return All explanations in the current argument
     */
    public LinkVector getExps() {
        return mExplanations;
    }

    /**
     * @return All contradictions in the current argument
     */
    public LinkVector getConts() {
        return mContradictions;
    }

    /**
     * Add a new hypothesis to the argument
     * @param hyp Proposition to add
     */
    public void addHypothesis(Proposition hyp) {
        if (!mHypotheses.contains(hyp)) {
            mHypotheses.addElement(hyp);
        }
        // make sure it's not in data if it was reclassified
        deleteData(hyp);
    }

    /**
     * Add a new data to the argument
     * @param data Proposition to add
     */
    public void addData(Proposition data) {
        if (!mData.contains(data)) {
            mData.addElement(data);
        }
        // make sure it's not in hypotheses if it was reclassified
        deleteHypothesis(data);
    }

    /**
     * Delete a hypothesis from the argument
     * @param hyp Proposition to delete
     */
    public void deleteHypothesis(Proposition hyp) {
        if (mHypotheses.contains(hyp)) {
            mHypotheses.removeElement(hyp);
        }
    }

    /**
     * Delete hypotheses from the argument
     * @param indexes indices of Proposition to delete
     */
    public void deleteHypotheses(int[] indexes) {
        for (int i = 0; i < indexes.length; i++) {
            try {
                mHypotheses.removeElementAt(indexes[i]);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Delete data from the argument
     * @param data Proposition to delete
     */
    public void deleteData(Proposition data) {
        if (mData.contains(data)) {
            mData.removeElement(data);
        }
    }

    /**
     * Delete data from the argument
     * @param indexes indices of Proposition to delete
     */
    public void deleteData(int[] indexes) {
        for (int i = 0; i < indexes.length; i++) {
            try {
                mData.removeElementAt(indexes[i]);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Add an explanation to the current argument
     * @param exp Explanation link to add
     */
    public void addExplanation(Link exp) {
        mExplanations.addElement(exp);
    }

    /**
     * Add a contradiction to the current argument
     * @param cont Explanation link to add
     */
    public void addContradiction(Link cont) {
        mContradictions.addElement(cont);
    }

    /**
     * Delete explanations from the argument
     * @param indexes indices of Proposition to delete
     */
    public void deleteExplanations(int[] indexes) {
        for (int i = 0; i < indexes.length; i++) {
            try {
                mExplanations.removeElementAt(indexes[i]);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * Delete a contradiction from the current argument
     * @param cont Explanation link to add
     */
    public void deleteContradiction(Link cont) {
        if (mContradictions.contains(cont)) {
            mContradictions.removeElement(cont);
        }
    }

    /**
     * Delete contradictions from the argument
     * @param indexes indices of Proposition to delete
     */
    public void deleteContradictions(int[] indexes) {
        for (int i = 0; i < indexes.length; i++) {
            try {
                mContradictions.removeElementAt(indexes[i]);
            } catch (Exception e) {
                Debug.printStackTrace(e);
            }
        }
    }

    /**
     * @return XML for this argument
     */
    public Element getXML() {
        Element root = DocumentFactory.getInstance().createElement(ARGUMENT);
        Enumeration e = mHypotheses.elements();
        while (e.hasMoreElements()) {
            Proposition p = (Proposition) e.nextElement();
            root.add(p.getXML());
        }
        e = mData.elements();
        while (e.hasMoreElements()) {
            Proposition p = (Proposition) e.nextElement();
            root.add(p.getXML());
        }
        e = mExplanations.elements();
        while (e.hasMoreElements()) {
            Link l = (Link) e.nextElement();
            root.add(l.getXML());
        }
        e = mContradictions.elements();
        while (e.hasMoreElements()) {
            Link l = (Link) e.nextElement();
            root.add(l.getXML());
        }
        return root;
    }

    /**
     * @return an argument from XML
     */
    public static Argument readXML(Element root) {
        Argument argument = new Argument();

        // add hypotheses
        List list = root.elements(Proposition.HYPOTHESIS_TYPE);
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            Proposition prop = Proposition.readXML(element);
            argument.addHypothesis(prop);
        }

        // add data
        list = root.elements(Proposition.DATA_TYPE);
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            Proposition prop = Proposition.readXML(element);
            argument.addData(prop);
        }

        // add explanations
        list = root.elements(Link.EXPLANATION_TYPE);
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            Link link = Link.readXML(element, argument);
            argument.addExplanation(link);
        }

        // add contradictions
        list = root.elements(Link.CONTRADICTION_TYPE);
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            Link link = Link.readXML(element, argument);
            argument.addContradiction(link);
            //Debug.println(link.getText());
        }

        return argument;
    }
 }
