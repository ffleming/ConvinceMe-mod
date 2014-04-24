package com.codeguild.convinceme.model;

import java.util.Enumeration;
import java.util.Vector;
import java.util.HashMap;
import com.codeguild.convinceme.utils.Debug;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

/**
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class ECHOSimulation {

    public static final float VALERR = -999.0f; // error code from getval()

    /*
     * ConvinceMe default values
    public static final float DATA_EXCITATION = 0.055f;
    public static final float EXCITATION = 0.030f;
    public static final float INHIBITION = 0.060f;
    public static final float THETA = 0.040f;
    public static final float START_VALUE = 0.010f;
    public static final float STOP_VALUE = 0.001f;
    public static final int MAX_ITERATIONS = 200;
    public static final float ANALOGY_IMPACT = 1.000f;
    public static final float MAXACT = 1.00f;
    public static final float MINACT = -1.00f;
    public static final float SIMPLICITY = 1.00f;
     * JavaECHO values
    public static final float DATA_EXCITATION = 0.040f;	
    public static final float EXCITATION = 0.040f;
    public static final float INHIBITION = 0.060f;
    public static final float THETA = 0.050f;
    public static final float START_VALUE = 0.010f;
    public static final float STOP_VALUE = 0.001f;
    public static final int MAX_ITERATIONS = 200;
    public static final float ANALOGY_IMPACT = 1.000f;
    public static final float MAXACT = 1.00f;
    public static final float MINACT = -1.00f;
    public static final float SIMPLICITY = 1.00f;
*/
    public static final float DATA_EXCITATION = 0.055f;
    public static final float EXCITATION = 0.030f;
    public static final float INHIBITION = 0.060f;
    public static final float COMPETITION = 0.060f;
    public static final float THETA = 0.040f;
    public static final float START_VALUE = 0.010f;
    public static final float STOP_VALUE = 0.001f;
    public static final int MAX_ITERATIONS = 500;
    public static final float ANALOGY_IMPACT = 1.000f;
    public static final float MAXACT = 1.00f;
    public static final float MINACT = -1.00f;
    public static final float SIMPLICITY = 1.00f;

    
    public static final int MAX_BELIEVABILITY = 9;
    public static final int MAX_RELIABILITY = 3;

    public static final boolean CALCULATE_COMPETITORS = true;
    
    private Proposition mSeu = new Proposition();;
    private int mIter = 0;

    private Encoding mEncoding;
    private StringBuffer mLog;

    private float mDataExcitation = DATA_EXCITATION;
    private float mExcitation = EXCITATION;
    private float mInhibition = INHIBITION;
    private float mCompetition = COMPETITION;
    private float mDecay = THETA;
    private float mSimplicityImpact = SIMPLICITY;
    private boolean mCalculateCompetitors = CALCULATE_COMPETITORS;
    
    private Argument mArgument;

    public ECHOSimulation(float excitation, float inhibition, float dataexcit, float decay,
                          Argument argument) {
        mExcitation = excitation;
        mInhibition = inhibition;
        mDataExcitation = dataexcit;
        mDecay = decay;
        mArgument = argument;
    }

    /**
     * Convenience method if you want to use the default parameter values
     */
    public ECHOSimulation(Argument argument) {
        this(EXCITATION, INHIBITION, DATA_EXCITATION, THETA, argument);
    }

    public Encoding getEncoding() {
        return mEncoding;
    }

    public PropositionVector getHyps() {
        return mArgument.mHypotheses;
    }

    public PropositionVector getData() {
        return mArgument.mData;
    }

    public LinkVector getExplanations() {
        return mArgument.mExplanations;
    }

    public LinkVector getContradictions() {
        return mArgument.mContradictions;
    }

    public String getLog() {
        return mLog.toString();
    }

    /**
     * Run the echo simulation with default parameter settings
     * @return String result that describes correlation (for more information, getLog())
     */
    public String runECHO() {
        return runECHO(mExcitation, mInhibition, mDataExcitation, mDecay);
    }
    
    public String runExhaustive() {
    	String display_string = "";
    	mLog = new StringBuffer();
        LinkVector explanationLinks = getExplanations();
        LinkVector contradictionLinks = getContradictions();
        Vector solutions = new Vector();
        
        explanationLinks.setWeights(mExcitation, true);  // divide excitation if joint explanation
        contradictionLinks.setWeights(-mInhibition, true);  // divide inhibition if joint contradiction
        
        if( mCalculateCompetitors) {
        	calculateCompetitors();
        }
        
        mLog.append("Running exhaustive simulation\n");
    	
    	Object[] props = mArgument.getHypotheses().concatenate(mArgument.getData()).toArray();
    	Float[] values = new Float[(int) Math.pow(2, props.length)];
    	int maximum_index = 0;
    	float maximum_value = 0f;
    	int maximum_evidence = 0;
    	
    	for (int i = 0; i < (int) Math.pow(2, props.length); i++) {
    		String binary_string =  int_to_binary(i, props.length);
    		PropositionVector accepted = new PropositionVector();
	        PropositionVector rejected = new PropositionVector();
	        for (int j = 0; j < binary_string.length(); j++) {
                if (binary_string.charAt(j) == '1') {
                	accepted.add((Proposition) props[j]);
                } else {
                	rejected.add((Proposition)props[j]);
                }
            }
	        Object[] sat_result = getSatisfied(accepted, rejected);
	        LinkVector satisfied = (LinkVector) sat_result[0];
	        Float satisfied_weight = (Float) sat_result[1];
	        
	        Object[] unsat_result = getUnsatisfied(accepted, rejected);
	        LinkVector unsatisfied = (LinkVector) unsat_result[0];
	        Float unsatisfied_weight = (Float) unsat_result[1];
	        
	        Float coherence_measure = satisfied_weight - unsatisfied_weight;
	        
	        int evidence_count = 0;
	        for (Enumeration e = mArgument.getData().elements(); e.hasMoreElements();  ) {
	        	Proposition cur_prop = (Proposition) e.nextElement();
	        	if (accepted.contains(cur_prop)){
	        		evidence_count++;
	        	}
	        }
	        
    	    if (coherence_measure >= maximum_value && evidence_count >= maximum_evidence) {
            	maximum_index = i;
            	maximum_value = coherence_measure;
            	maximum_evidence = evidence_count;
            }
    	}  	
    	
    	String max_string =  int_to_binary(maximum_index, props.length);
		PropositionVector accepted = new PropositionVector();
        PropositionVector rejected = new PropositionVector();
        for (int j = 0; j < max_string.length(); j++) {
            if (max_string.charAt(j) == '1') {
            	accepted.add((Proposition) props[j]);
            } else {
            	rejected.add((Proposition)props[j]);
            }
        }
        
        Object[] sat_result = getSatisfied(accepted, rejected);
        LinkVector satisfied = (LinkVector) sat_result[0];
        Float satisfied_weight = (Float) sat_result[1];
        
        Object[] unsat_result = getUnsatisfied(accepted, rejected);
        LinkVector unsatisfied = (LinkVector) unsat_result[0];
        Float unsatisfied_weight = (Float) unsat_result[1];
        
        
    	display_string = display_string + "\nThe subset at " + max_string + " gives us " + maximum_value;
        display_string += "\nPropositions Accepted: ";
        for (Enumeration e = accepted.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
        	display_string += " " + cur.getLabel();
        }
        
        display_string += "\nPropositions Rejected: ";
        for (Enumeration e = rejected.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
           	display_string += " " + cur.getLabel();
        }
        display_string += "\n";
        display_string += satisfied.size() + " satisfied links, weighing " + satisfied_weight + "\n";
        display_string += unsatisfied.size() + " unsatisfied links, weighing " + unsatisfied_weight + "\n";
        display_string += "Size-insensitive coherence measure: " + (satisfied_weight - unsatisfied_weight) + "\n";
        display_string += "Size-sensitive coherence measure (satisfied / total): " + (satisfied_weight) / (satisfied_weight + unsatisfied_weight)+ "\n";
        display_string += "Size-sensitive coherence measure ((satisfied - unsatisfied) / total): " + (satisfied_weight - unsatisfied_weight) / (satisfied_weight + unsatisfied_weight)+ "\n";
    	
    	mLog.append(display_string);
    	return display_string;
    }

    private String int_to_binary(int number, int length) {
    	String bin_string = Integer.toBinaryString(number);
    	String ret = bin_string;
    	for (int i = bin_string.length(); i < length; i++) {
            ret = "0" + ret;
        }
    	return ret;
    }
    
    /**
     * Run echo simulation given parameters
     * @param excitation
     * @param inhibition
     * @param dataexcit
     * @param decay
     * @return String result that describes correlation (for more information, getLog())
     */
    public String runECHO(float excitation, float inhibition, float dataexcit, float decay) {
        mExcitation = excitation;
        mInhibition = inhibition;
        mDataExcitation = dataexcit;
        mDecay = decay;

        mEncoding = new Encoding(mArgument);

        mLog = new StringBuffer();
        mLog.append("Running connectionist simulation with parameters: excitation = ");
        mLog.append(excitation);
        mLog.append(", inhibition = ");
        mLog.append(inhibition);
        mLog.append(", data excitation = ");
        mLog.append(dataexcit);
        mLog.append(", decay = ");
        mLog.append(decay);
        mLog.append(", competition = ");
        mLog.append(mCompetition); 
        mLog.append("\n");
        //mLog.append(mEncoding.getText());
        
        // initialize proposition activations
        PropositionVector props = getHyps().concatenate(getData());
        props.setActivations(START_VALUE);
        props.initWeights();

        // create SEU links to data
        // SEU is special evidence unit - gives evidence/data plausibility since we saw it
        mSeu.setActivation(1);
        LinkVector seuLinks = new LinkVector();
        Proposition dataProp;
        PropositionVector p;

        // Data is Evidence in the UI
        // Connect (invisible to UI) SEUs to Data/Evidence nodes to give that data plausibility of its own
        // We don't just use the Data/Evidence nodes themselves as entry points because we want it to be
        // possible to reject evidence
        for (Enumeration j = getData().elements(); j.hasMoreElements();) {
            dataProp = (Proposition) j.nextElement();
            p = new PropositionVector(dataProp);
            p.addElement(mSeu);
            Link seuLink = new Link(p, Link.EXPLAIN);
            
            // seu Links are special; set weights separately here, dividing
            // data excitation by the reliability of the evidence
            float relFactor = (float)dataProp.getReliability()/(float)MAX_RELIABILITY;
            seuLink.setWeights(mDataExcitation * relFactor, false);
            seuLinks.addElement(seuLink);
        }

        // set weights, pass parameter values, and flag whether to
        // divide parameter weigh among links or not. note that seu links
        // have already been set, weighted by reliability, above
        LinkVector explanationLinks = getExplanations();
        LinkVector contradictionLinks = getContradictions();
        
        explanationLinks.setWeights(mExcitation, true);  // divide excitation if joint explanation
        
        // Normally there'd be no reason to divide inhibition.  Enabled multiple contradictions to make this possible
        contradictionLinks.setWeights(-mInhibition, true);  // divide inhibition if joint contradiction
        
        if( mCalculateCompetitors) {
        	calculateCompetitors();
        }
        
        //Maybe I should add a special thing about competitors...I think I probably will.  Have to figure out how, though.  Right now it
        //seems like the best bet is to have a whole new type of Link, and have it contain two proposition vectors.  Actually that sounds
        // great and easy to implement.
        
        float change = 99, net = 0, temp, nextact, thisact, thischange;
        Proposition current_proposition;

        Enumeration ej, ei;
        Vector wv;
        Weight w;

        // run simulation and set activations
        for (mIter = 1; (mIter < MAX_ITERATIONS) && (change > STOP_VALUE); mIter++) {
            change = 0.0f;
			//Debug.println("Iteration: " + mIter);
            for (ej = props.elements(); ej.hasMoreElements();) {
            	current_proposition = (Proposition) ej.nextElement();
                wv = current_proposition.getWeights();
                net = 0;
                for (ei = wv.elements(); ei.hasMoreElements();) {
                    w = (Weight) ei.nextElement();
                    net = net + (w.getWeight() * (w.getProposition().getActivation()));
                }
                thisact = current_proposition.getActivation();
                
                //net is the sum of (edge weight * activation) for each node we're connected to
               
                if (net > 0) {
                    temp = net * (MAXACT - thisact);
                } else {
                    temp = net * (thisact - MINACT);
                }
                //Next activation is current activation (taking decay into account), plus temp
                // temp is (1-absolute value of thisact) * net
                
                //So if there are 4 nodes around me connected at weight .03 and they all have activation .5,
                // then net is 4 * .03 * .5 = .06
                //If thisact is .4, then next act will be
                nextact = (thisact * (1.0f - mDecay)) + temp;
                
				//Debug.println(propj.getLabel() + " " + thisact + " net: " + net + " temp: " + temp);
                current_proposition.setNextActivation(nextact);
                change = (Math.max(Math.abs(thisact - nextact), change));
            }
            for (ej = props.elements(); ej.hasMoreElements();) {
            	current_proposition = (Proposition) ej.nextElement();
            	current_proposition.setActivation(current_proposition.getNextActivation());
            }
        }

        // log simulation results
        mLog.append("Simulation finished. Iterations = ");
        mLog.append(String.valueOf(mIter));
        mLog.append(" (max iterations = ");
        mLog.append(MAX_ITERATIONS);
        mLog.append(")\n");

        String display_string = "";
        
        PropositionVector accepted = new PropositionVector();
        PropositionVector rejected = new PropositionVector();
        LinkVector satisfied = new LinkVector();
        LinkVector unsatisfied = new LinkVector();
        float satisfied_weight = 0;
        float unsatisfied_weight = 0;
        
        //Make accepted and rejected lists according to activation of propositions
        for (Enumeration e = props.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
        	if(cur.getActivation() > 0) {
        		accepted.add(cur);
        	} else if (cur.getActivation() < 0) {
        		rejected.add(cur);
        	}
        }
               		
		// Make lists of satisfied/unsatisfied pairwise dispositions
        // For each proposition...
        Object[] sat_result = getSatisfied(accepted, rejected);
        satisfied = (LinkVector) sat_result[0];
        satisfied_weight = (Float) sat_result[1];
        
        Object[] unsat_result = getUnsatisfied(accepted, rejected);
        unsatisfied = (LinkVector) unsat_result[0];
        unsatisfied_weight = (Float) unsat_result[1];
        
		//Get the output ready
		
        display_string += "\nPropositions Accepted: ";
        float total = 0f;
        for (Enumeration e = accepted.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
        	total = total + cur.getActivation();
        	display_string += " " + cur.getLabel() + " (" + cur.getActivationText() + "); ";
        }
        display_string += "\nMean activation:" + total / (float)accepted.size();
        total = 0;
        display_string += "\nPropositions Rejected: ";
        for (Enumeration e = rejected.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
        	total = total + cur.getActivation();
           	display_string += " " + cur.getLabel() + " (" + cur.getActivationText() + "); ";
        }
        display_string += "\nMean activation:" + total / (float)rejected.size() + "\n";
        
        display_string += satisfied.size() + " satisfied links, weighing " + satisfied_weight + "\n";
        /*
		for (Enumeration e = satisfied.elements(); e.hasMoreElements(); ) {
           	Link cur = (Link)e.nextElement();
        	display_string += "\t" + cur.getText() + "\n";
        } */
        
        display_string += unsatisfied.size() + " unsatisfied links, weighing " + unsatisfied_weight + "\n";

        /*
        for (Enumeration e = unsatisfied.elements(); e.hasMoreElements(); ) {
        	Link cur = (Link)e.nextElement();
        	display_string += "\t" + cur.getText() + "\n";
        }
        */
        
        display_string += "Size-insensitive coherence measure: " + (satisfied_weight - unsatisfied_weight) + "\n";
        display_string += "Size-sensitive coherence measure (satisfied / total): " + (satisfied_weight) / (satisfied_weight + unsatisfied_weight)+ "\n";
        display_string += "Size-sensitive coherence measure ((satisfied - unsatisfied) / total): " + (satisfied_weight - unsatisfied_weight) / (satisfied_weight + unsatisfied_weight)+ "\n";
                
        props.initWeights(); // delete weights, not needed now
        mLog.append(display_string);
        return display_string;
    }

    /**
     * Get the text description of the correlation between simulation
     * results and belief ratings
     * @param pv the proposition vector
     * @return The text description of the correlation
     */
    public String getCorrelationText(PropositionVector pv) {
        String result;
        double corr = getCorrelation(pv);
        if ((corr < -1) || (corr > 1)) {
            result = "Not enough ratings, or no variation in ratings.";
        } else {
            result = String.valueOf(Math.round(corr * 100.0) / 100.0);
        }
        return result;
    }

    /**
     * Get the correlation between simulation results and belief ratings
     * @param pv the proposition vector
     * @return The correlation
     */
    public double getCorrelation(PropositionVector pv) {
        double sumx1 = 0, sumx2 = 0, sumy1 = 0, sumy2 = 0, sumxy = 0,
                total = 0, r, a, numerator, d1, d2, corr;
        Proposition p;
        for (Enumeration e = pv.elements(); e.hasMoreElements();) {
            p = (Proposition) e.nextElement();
            r = (double) p.getRating();
            a = (double) p.getActivation();
            if (p.isValid((float) r) && p.isValid((float) a)) {
                sumx1 += r;
                sumy1 += a;
                sumx2 += r * r;
                sumy2 += a * a;
                sumxy += r * a;
                total += 1;
            }
        }
        numerator = (total * sumxy) - (sumx1 * sumy1);
        d1 = Math.sqrt((total * sumx2) - (sumx1 * sumx1));
        d2 = Math.sqrt((total * sumy2) - (sumy1 * sumy1));
        corr = numerator / (d1 * d2);
        return corr;
    }
    
    
    //Calculates the competitors for the entire simulation.  Should be moved to Argument class.
    public void calculateCompetitors() {
    	LinkVector explanations = mArgument.mExplanations;
    	for(int i=0; i < explanations.size(); i++) {
    		Link current_explanation = (Link) explanations.elementAt(i);
    		Proposition explained_prop = current_explanation.getExplained();
    		boolean competes = false;
    		
    		for(int j = i; j < explanations.size(); j++) {
    			Link potential_competing_exp = (Link) explanations.elementAt(j);
    			if(current_explanation.equals(potential_competing_exp)) {
    				//They're the same - it doesn't compete with itself so keep on keepin' on
    				continue;
    			}

    			
    			Proposition other_explained_prop = potential_competing_exp.getExplained();
    			
    			if (explained_prop.equals(other_explained_prop)) {
    				//They explain the same thing! Check to see if they're incompatible
    				competes = areCompetitors(current_explanation, potential_competing_exp);
    				Debug.println("Does (" + current_explanation.getExplainers().getLabelsText() + ")->" + current_explanation.getExplained().getLabel() + " compete with (" + potential_competing_exp.getExplainers().getLabelsText() + ")->" + potential_competing_exp.getExplained().getLabel() + "?\n\t\t " + (competes ? "Yes" : "No"));    				
    				if (competes) {
    					//OK, the two explanations compete. So make inhibitory links between each pair.
    					Debug.println("Setting (" + current_explanation.getExplainers().getLabelsText() + ")->" + current_explanation.getExplained().getLabel() + " as competing with (" + potential_competing_exp.getExplainers().getLabelsText() + ")->" + potential_competing_exp.getExplained().getLabel());
    					setCompetitionInhibitors(current_explanation, potential_competing_exp);
    				}
    				
    			}
    		}
    	}
    }
   
    public void setCompetitionInhibitors(Link exp1, Link exp2) {
    	Enumeration e1 = exp1.getExplainers().elements();
    	Enumeration e2 = exp2.getExplainers().elements();

    	//Divide competition weight equally among all of these links.
    	float divided_weight = -mCompetition / (exp1.getExplainers().size() * exp2.getExplainers().size() );
    	
    	//Set up weights between all exp1's explainers with all exp2's explainers.
    	for( e1 = exp1.getExplainers().elements(); e1.hasMoreElements(); ) {
    		Proposition cur_prop = (Proposition) e1.nextElement();
    		for( e2 = exp2.getExplainers().elements(); e2.hasMoreElements(); ) {
    			Proposition other_prop = (Proposition) e2.nextElement();
    			cur_prop.addWeight(other_prop, divided_weight);
    			other_prop.addWeight(cur_prop, divided_weight);
    		}
    			
    	}
    }
    
    public boolean areCompetitors (Link exp1, Link exp2) {
    	if (exp1.equals(exp2)) {
    		return false;
    	}
    	if ( exp1.getExplainers().contains(exp2.getExplained())) {
    		return false;
    	}
    	if ( exp2.getExplainers().contains(exp1.getExplained())) {
    		return false;
    	}
    	
    	return true;
    }
    
    //This looks weird and all, but these two operate over WEIGHTS, not EXPLANATIONS.  This is because we want the links internal to competitions,
    // joint explanations, etc. to be reported as satisfied or unsatisfied.  
    private Object[] getSatisfied(PropositionVector accepted, PropositionVector rejected) {
    	PropositionVector props = mArgument.mHypotheses;
    	LinkVector satisfied = new LinkVector();
    	float satisfied_weight = 0f;
    	
    	for (Enumeration ej = props.elements(); ej.hasMoreElements();) {
			Proposition current_proposition = (Proposition)ej.nextElement();
			Vector wv = current_proposition.getWeights();
			//...Look at the weights it has.  For each of these weights, current_proposition is FROM and w.getProposition() is TO
			for (Enumeration ei = wv.elements(); ei.hasMoreElements();) {
				Weight w = (Weight)ei.nextElement();
				
				//If it's self-linked, ignore
				if(w.getProposition() == current_proposition) {
					continue;
				}
				
				// pv is a link with the two relevant props
				PropositionVector pv = new PropositionVector(current_proposition).concatenate(new PropositionVector(w.getProposition()));
				Link l = new Link(pv);
				
				//Go through entire list of satisfied and unsatisfied links/dispositions. If we find any duplicates, set is_dup to true
				//That is, if the link we're looking at is already in the satisfied or unsatisfied list, no need to add it again
				if(satisfied.contains(l)) {
					continue;
				}
				
				//Figure out if it's a contradiction or explanation
				if (w.getWeight() > 0) { //explanation
					l.setType(Link.EXPLAIN);
					//Both accepted or both rejected means satisfied
					if ( (accepted.contains(current_proposition) && accepted.contains(w.getProposition()) || ( rejected.contains(current_proposition) && rejected.contains(w.getProposition()) ) ) ) {						
						satisfied.add(l);
						satisfied_weight += Math.abs(w.getWeight());
					} 
				}
				if (w.getWeight() < 0) { //contradiction or competition
					l.setType(Link.CONTRADICT);
					//Both accepted or both rejected means unsatisfied
					if ( (accepted.contains(current_proposition) && rejected.contains(w.getProposition()) || rejected.contains(current_proposition) && accepted.contains(w.getProposition()))) {
						satisfied.add(l);
						satisfied_weight += Math.abs(w.getWeight());
					}
				}
			}
		}
    	Object[] ret = {satisfied, satisfied_weight};
    	return ret;
    }

    private Object[] getUnsatisfied(PropositionVector accepted, PropositionVector rejected) {
    	PropositionVector props = mArgument.mHypotheses;
    	LinkVector unsatisfied = new LinkVector();
    	float unsatisfied_weight = 0f;
    	
    	for (Enumeration ej = props.elements(); ej.hasMoreElements();) {
			Proposition current_proposition = (Proposition)ej.nextElement();
			Vector wv = current_proposition.getWeights();
			//...Look at the weights it has.  For each of these weights, current_proposition is FROM and w.getProposition() is TO
			for (Enumeration ei = wv.elements(); ei.hasMoreElements();) {
				Weight w = (Weight)ei.nextElement();
				
				//If it's self-linked, ignore
				if(w.getProposition() == current_proposition) {
					continue;
				}
				
				// pv is a link with the two relevant props
				PropositionVector pv = new PropositionVector(current_proposition).concatenate(new PropositionVector(w.getProposition()));
				Link l = new Link(pv);
				
				//Go through entire list of satisfied and unsatisfied links/dispositions. If we find any duplicates, set is_dup to true
				//That is, if the link we're looking at is already in the satisfied or unsatisfied list, no need to add it again
				if(unsatisfied.contains(l)) {
					continue;
				}
				
				//Figure out if it's a contradiction or explanation
				if (w.getWeight() > 0) { //explanation
					l.setType(Link.EXPLAIN);
					//Both accepted or both rejected means satisfied
					if ( (accepted.contains(current_proposition) && rejected.contains(w.getProposition()) || rejected.contains(current_proposition) && accepted.contains(w.getProposition()))) {
						unsatisfied.add(l);
						unsatisfied_weight += Math.abs(w.getWeight());
					}
				}
				if (w.getWeight() < 0) { //contradiction or competition
					l.setType(Link.CONTRADICT);
					//Both accepted or both rejected means unsatisfied
					if ( (accepted.contains(current_proposition) && accepted.contains(w.getProposition()) || rejected.contains(current_proposition) && rejected.contains(w.getProposition()))) {
						unsatisfied.add(l);
						unsatisfied_weight += Math.abs(w.getWeight());
					}
				}
			}
		}
    	Object[] ret = {unsatisfied, unsatisfied_weight};
    	return ret;
    }
    
}
