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
    
    */
    
    /*
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
    public static final float THETA = 0.040f;
    public static final float START_VALUE = 0.010f;
    public static final float STOP_VALUE = 0.001f;
    public static final int MAX_ITERATIONS = 200;
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
        mLog.append("Running simulation with parameters: excitation = ");
        mLog.append(excitation);
        mLog.append(", inhibition = ");
        mLog.append(inhibition);
        mLog.append(", data excitation = ");
        mLog.append(dataexcit);
        mLog.append(", decay = ");
        mLog.append(decay);
        mLog.append("\n");
        //mLog.append(mEncoding.getText());

        //Before we start, calculate competing explanations.  Ugh I don't want to write this BUT I WILL.
        if( mCalculateCompetitors) {
        	calculateCompetitors();
        }
        
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

        //Maybe I should add a special thing about competitors...I think I probably will.  Have to figure out how, though.  Right now it
        //seems like the best bet is to have a whole new type of Link, and have it contain two proposition vectors.  Actually that sounds
        // great and easy to implement.
        
        float change = 99, net = 0, temp, nextact, thisact, thischange;
        Proposition current_proposition;

        Enumeration ej, ei;
        Vector wv;
        Weight w;


        mLog.append("Simulation weights:\n");
        // report link weights
		for (ej = props.elements(); ej.hasMoreElements();) {
			current_proposition = (Proposition)ej.nextElement();
			wv = current_proposition.getWeights();
   	   		for (ei = wv.elements(); ei.hasMoreElements();) {
				w = (Weight)ei.nextElement();
				mLog.append("\t" + current_proposition.getLabel() + " to " + w.getProposition().getLabel() + ": " + w.getWeight() + "\n" );
			}
		}

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
                if (net > 0) {
                    temp = net * (MAXACT - thisact);
                } else {
                    temp = net * (thisact - MINACT);
                }
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

        //Don't delete weights yet silly

        // log simulation results
        mLog.append("Simulation finished. Iterations = ");
        mLog.append(String.valueOf(mIter));
        mLog.append(" (max iterations = ");
        mLog.append(MAX_ITERATIONS);
        mLog.append(")\n");

        String display_string = "";
        
        HashMap h = new HashMap();
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
        
        /*
        mLog.append("Simulation weights:\n");
        
        //Report weights
		
        for (ej = props.elements(); ej.hasMoreElements();) {
			current_proposition = (Proposition)ej.nextElement();
			wv = current_proposition.getWeights();
   	   		for (ei = wv.elements(); ei.hasMoreElements();) {
				w = (Weight)ei.nextElement();
				mLog.append("\t" + current_proposition.getLabel() + " to " + w.getProposition().getLabel() + ": " + w.getWeight() + "\n" );
			}
		}
        */
        		
		// Make lists of satisfied/unsatisfied pairwise dispositions
        // For each proposition...
		for (ej = props.elements(); ej.hasMoreElements();) {
			current_proposition = (Proposition)ej.nextElement();
			wv = current_proposition.getWeights();
			//...Look at the weights it has.  For each of these weights, current_proposition is FROM and w.getProposition() is TO
			for (ei = wv.elements(); ei.hasMoreElements();) {
				w = (Weight)ei.nextElement();
				
				//If it's self-linked, ignore
				if(w.getProposition() == current_proposition) {
					continue;
				}
				
				// pv is a link with the two relevant props
				PropositionVector pv = new PropositionVector(current_proposition).concatenate(new PropositionVector(w.getProposition()));
				Link l = new Link(pv);
				
				//Go through entire list of satisfied and unsatisfied links/dispositions. If we find any duplicates, set is_dup to true
				boolean is_dup = false;
				for(Enumeration e = satisfied.concatenate(unsatisfied).elements(); e.hasMoreElements(); ) {
					Link cur_link = (Link) e.nextElement();
					if (cur_link.isSamePair(l)) {
						is_dup = true;
						//Debug.println(l.getPropAt(0).getLabel() + "->" + l.getPropAt(1).getLabel() + " is the same as " + cur_link.getPropAt(0).getLabel() + "->" + cur_link.getPropAt(1).getLabel());
					}
				}
				//If it's a dupe, pop on out of this loop before we do any adding to accepted/rejected lists
				if(is_dup) {
					continue;
				}
				
				//Figure out if it's a contradiction or explanation
				if (w.getWeight() > 0) { //explanation
					l.setType(Link.EXPLAIN);
					//Both accepted or both rejected means satisfied
					if ( (accepted.contains(current_proposition) && accepted.contains(w.getProposition()) || ( rejected.contains(current_proposition) && rejected.contains(w.getProposition()) ) ) ) {						
						satisfied.add(l);
						satisfied_weight += Math.abs(w.getWeight());
						//Debug.println("Added [" + l.getText() + "] to satisfied");
					} else if ( (accepted.contains(current_proposition) && rejected.contains(w.getProposition()) || rejected.contains(current_proposition) && accepted.contains(w.getProposition()))) {
						unsatisfied.add(l);
						unsatisfied_weight += Math.abs(w.getWeight());
						//Debug.println("Added [" + l.getText() + "] to unsatisfied");
					}
				}
				if (w.getWeight() < 0) { //contradiction
					l.setType(Link.CONTRADICT);
					//Both accepted or both rejected means unsatisfied
					if ( (accepted.contains(current_proposition) && accepted.contains(w.getProposition()) || rejected.contains(current_proposition) && rejected.contains(w.getProposition()))) {
						unsatisfied.add(l);
						unsatisfied_weight += Math.abs(w.getWeight());
						//Debug.println("Added [" + l.getText() + "] to unsatisfied");
					} else if ( (accepted.contains(current_proposition) && rejected.contains(w.getProposition()) || rejected.contains(current_proposition) && accepted.contains(w.getProposition()))) {
						satisfied.add(l);
						satisfied_weight += Math.abs(w.getWeight());
						//Debug.println("Added [" + l.getText() + "] to satisfied");
					}
				}
			}
		}
		//Get the output ready
		
        display_string += "\nPropositions Accepted: ";
        for (Enumeration e = accepted.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
        	display_string += " " + cur.getLabel() + " (" + cur.getActivationText() + "); ";
        }
        
        display_string += "\nPropositions Rejected: ";
        for (Enumeration e = rejected.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
           	display_string += " " + cur.getLabel() + " (" + cur.getActivationText() + "); ";
        }
        
        display_string += "\n\nPairwise coherence measure: \n";

        display_string += "Satisfied links:\n ";
        for (Enumeration e = satisfied.elements(); e.hasMoreElements(); ) {
        	Link cur = (Link)e.nextElement();
        	display_string += "\t" + cur.getText() + "\n";
        }
        display_string += "Satisfied weight: " + satisfied_weight +"\n";
        display_string += "\nUnsatisfied links:\n ";
        for (Enumeration e = unsatisfied.elements(); e.hasMoreElements(); ) {
        	Link cur = (Link)e.nextElement();
        	display_string += "\t" + cur.getText() + "\n";
        }
        display_string += "Unsatisfied weight: " + unsatisfied_weight +"\n";
        display_string += "\n";
        display_string += "Coherence measure: " + (satisfied_weight - unsatisfied_weight) + "\n";
        
        
        

        /*
        //Clear list to tally with the other coherence measure!
        satisfied.removeAllElements();
        unsatisfied.removeAllElements();
        satisfied_weight = 0;
        unsatisfied_weight = 0;
        
        //If an explanation is either wholly in the rejected list or wholly in the accepted list
        for (Enumeration e = getExplanations().elements(); e.hasMoreElements(); ) {
        	Link cur_explanation = (Link)e.nextElement();
        	//Check if all propositions in each explanation are either accepted or rejected.  If so, add to satisfied list. Otherwise add to unsatisfied
        	if(accepted.containsAll(cur_explanation.getProps()) || rejected.containsAll(cur_explanation.getProps())) {
        		satisfied.add(cur_explanation);
        	} else {
        		unsatisfied.add(cur_explanation);
        	}
        }
        //Same thing for contradictions
        for (Enumeration e = getContradictions().elements(); e.hasMoreElements(); ) {
        	Link cur_contradiction = (Link)e.nextElement();
        	
        	//If we accept what's contradicted, we must reject everything that it contradicts
        	if( accepted.contains(cur_contradiction.getContradicted()) ) {
        		if( rejected.containsAll(cur_contradiction.getJointContradictions()) ) {
        			satisfied.add(cur_contradiction);
        		} else {
        			unsatisfied.add(cur_contradiction);
        		}
        	} else if( rejected.contains(cur_contradiction.getContradicted()) ) {
        		if( accepted.containsAll(cur_contradiction.getJointContradictions()) ) {
        			satisfied.add(cur_contradiction);
        		} else {
        			unsatisfied.add(cur_contradiction);
        		}
        	}
        
        }
        
        display_string += "\n Systemic (per-explanation/contradiction) coherence measure:\n";

        display_string += "\n\tSatisfied:\n ";
        for (Enumeration e = satisfied.elements(); e.hasMoreElements(); ) {
        	Link cur = (Link)e.nextElement();
        	float to_add = (Math.abs(cur.getWeight()) * (cur.getProps().size()-1));
        	satisfied_weight += to_add;
        	display_string += "\t\t" + cur.getText() + " (" + to_add + ")\n";
        }
        
        display_string += "\n\tUnsatisfied:\n ";
        for (Enumeration e = unsatisfied.elements(); e.hasMoreElements(); ) {
        	Link cur = (Link)e.nextElement();
        	float to_add = (Math.abs(cur.getWeight()) * (cur.getProps().size()-1));
        	unsatisfied_weight += to_add;
        	display_string += "\t\t" + cur.getText() + " (" + to_add + ")\n";
        }

		display_string += "Satisfied weight: " + satisfied_weight +"\n";
		display_string += "Unsatisfied weight: " + unsatisfied_weight +"\n";
        display_string += "Coherence measure: " + (satisfied_weight - unsatisfied_weight) + "\n";
        
        /*
        for (Enumeration e = props.elements(); e.hasMoreElements(); ) {
        	current_proposition = (Proposition)e.nextElement();
        	display_string = display_string + current_proposition.getLabel() + "\t" + current_proposition.getActivationText() + "\n";
        }
        */
        
        props.initWeights(); // delete weights, not needed now
        mLog.append(display_string);
        return display_string;
        /*
        String s = "";
        for (Enumeration j = props.elements(); j.hasMoreElements();) {
            propj = (Proposition) j.nextElement();
            s = s + propj.getLabel() + "      " + propj.getActivationText() + "           " +
                    propj.getRatingText() + "\n";
        }

        mLog.append("      Activation    Rating\n");
        mLog.append(s);

        String correlationText = getCorrelationText(props);
        mLog.append("Correlation between ratings and activations: ");
        mLog.append(getCorrelationText(props));
        return correlationText;
        */
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
    
    public void calculateCompetitors() {
    	Debug.println("Calculating competititors");
    	/* First, get all the hypotheses.  Then, get the hypotheses that explain the same thing.
    	 * Then, check if there's a contradictory link between any of these explanations.
    	 * If there are, build contradictory links.
    	 */
    	LinkVector explanations = mArgument.mExplanations;
    	for(Enumeration ej = explanations.elements(); ej.hasMoreElements(); ) {
    		Link current_explanation = (Link) ej.nextElement();
    		Proposition explained_prop = current_explanation.getExplained();
    		boolean competes = false;
    		for(Enumeration ei = explanations.elements(); ei.hasMoreElements(); ) {
    			Link potential_competing_exp = (Link) ei.nextElement();
    			if(current_explanation.equals(potential_competing_exp)) {
    				//They're the same - it doesn't compete with itself so keep on keepin' on
    				continue;
    			}
    			Proposition other_explained_prop = potential_competing_exp.getExplained();
    			
    			if (explained_prop.equals(other_explained_prop)) {
    				//They explain the same thing! Check to see if they're incompatible
    				Debug.println("Does (" + current_explanation.getExplainers().getLabelsText() + ")->" + current_explanation.getExplained().getLabel() + " compete with (" + potential_competing_exp.getExplainers().getLabelsText() + ")->" + potential_competing_exp.getExplained().getLabel() + "?");
    				competes = areCompetitors(current_explanation, potential_competing_exp);
    				Debug.println(competes ? "Yes" : "No");
    				
    				if (competes) {
    					//OK, the two explanations compete. So make inhibitory links between each pair.
    					setCompetitionInhibitors(current_explanation, potential_competing_exp);
    				}
    				
    			}
    		}
    	}
    }
   
    public void setCompetitionInhibitors(Link exp1, Link exp2) {
    	Enumeration e1 = exp1.getExplainers().elements();
    	Enumeration e2 = exp2.getExplainers().elements();
    	
    	//Let's see what happens if we just say that e1 contradicts e2...
    	//This sets up a joint contradiction between the totality of e1 on the one hand and each individual e2 proposition on the other
    	
    	for( e1 = exp1.getExplainers().elements() ; e1.hasMoreElements(); ) {
    		Proposition cur_explainer = (Proposition) e1.nextElement();
    		PropositionVector cont_vector = exp2.getExplainers();
    		cont_vector.add(cur_explainer);
    		Link newContradiction = new Link(cont_vector, Link.CONTRADICT);
    		mArgument.addContradiction(newContradiction);
    	}
    	/*
    	//This sets up a 2-place contradiction between each element. INTENSE.
    	for( e1 = exp1.getExplainers().elements() ; e1.hasMoreElements(); ) {
    		Proposition cur_e1_prop = (Proposition) e1.nextElement();
    		for( e2 = exp2.getExplainers().elements() ; e2.hasMoreElements(); ) {
    	    	Proposition cur_e2_prop = (Proposition) e2.nextElement();
    	    	PropositionVector cont_vector = new PropositionVector(cur_e1_prop);
    	       	cont_vector.add(cur_e2_prop);
    	       	Link newContradiction = new Link(cont_vector, Link.CONTRADICT);
    	       	mArgument.addContradiction(newContradiction);
    	    }
    	}
    	*/
    }
    
    public boolean areCompetitors (Link exp1, Link exp2) {
    	LinkVector all_contradictions = mArgument.mContradictions;
    	Enumeration e1 = exp1.getExplainers().elements();
    	Enumeration e2 = exp2.getExplainers().elements();
    	Enumeration c = all_contradictions.elements();
    	
    	//I think this is unneeded, but w/e
    	if ( exp1.getExplainers().contains(exp2.getExplained())) {
    		return false;
    	}
    	if ( exp2.getExplainers().contains(exp1.getExplained())) {
    		return false;
    	}
    	
    	//For every explainer in exp1
    	for( e1 = exp1.getExplainers().elements() ; e1.hasMoreElements(); ) {
    		Proposition cur_prop = (Proposition) e1.nextElement();

    		//Check to see if it's in a contradiction with any explainer in exp2
    		//Do this by iterating through each contradiction and checking to see that if the CONTRADICTED of the contradiction is one of exp1's explainers
    		for (c = all_contradictions.elements(); c.hasMoreElements() ; ) {
    			Link cur_contradiction = (Link) c.nextElement();
    			
    			//Check if the contradiction's CONTRADICTED is an explainer of exp1
    			if(cur_contradiction.getContradicted() == cur_prop) {
    				//If any of exp2's explainers is in the JOINT CONTRADICTIONS part of the current contradiction, then we have a contradiction 
    				for(e2 = exp2.getExplainers().elements(); e2.hasMoreElements() ; ) {
    					Proposition exp2_explainer_prop = (Proposition) e2.nextElement();
    					if (cur_contradiction.getJointContradictions().contains(exp2_explainer_prop)) {
    						return true;
    					}
    				}
    			} else if (cur_contradiction.getJointContradictions().contains(cur_prop)) {
    				//The opposite of the above: the current exp1 explainer is in the CONTRADICTING section of the current contradiction
    				//Check to see if any exp2's explainers are in the CONTRADICTED section
    				for(e2 = exp2.getExplainers().elements(); e2.hasMoreElements() ; ) {
    					Proposition exp2_explainer_prop = (Proposition) e2.nextElement();
    					if (cur_contradiction.getContradicted() == exp2_explainer_prop) {
    						return true;
    					}
    				}
    				
    			}
    		}
    	}
    	
    	return true;
    }
    
}
