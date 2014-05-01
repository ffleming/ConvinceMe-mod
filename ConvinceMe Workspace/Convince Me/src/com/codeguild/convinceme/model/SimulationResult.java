/**
 * 
 */
package com.codeguild.convinceme.model;

import java.util.Enumeration;

/**
 * @author Forrest
 *
 */
public class SimulationResult {
	private String display_string;
	private float satisfied_weight;
	private float unsatisfied_weight;
	private PropositionVector accepted_propositions;
	private PropositionVector rejected_propositions;
	private LinkVector satisfied_links;
	private LinkVector unsatisfied_links;
	private float fleming_measure;
	private float size_insensitive_fleming_measure;
	private float satisfied_over_total_measure;
	
	public SimulationResult(PropositionVector accepted, PropositionVector rejected, LinkVector satisfied, LinkVector unsatisfied, float sat_w, float unsat_w) {
		accepted_propositions = accepted;
		rejected_propositions = rejected;
		satisfied_links = satisfied;
		unsatisfied_links = unsatisfied;
		satisfied_weight = sat_w;
		unsatisfied_weight = unsat_w;
		fleming_measure = satisfied_weight - unsatisfied_weight;
		size_insensitive_fleming_measure = fleming_measure / (satisfied_weight + unsatisfied_weight);
		satisfied_over_total_measure = satisfied_weight / (satisfied_weight + unsatisfied_weight);
	}
	
	public String getDisplayString() {
        display_string += "\nPropositions Accepted: ";
        for (Enumeration e = accepted_propositions.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
        	display_string += " " + cur.getLabel();
        }
        
        display_string += "\nPropositions Rejected: ";
        for (Enumeration e = rejected_propositions.elements(); e.hasMoreElements(); ) {
        	Proposition cur = (Proposition)e.nextElement();
           	display_string += " " + cur.getLabel();
        }
        display_string += "\n";
        display_string += satisfied_links.size() + " satisfied links, weighing " + satisfied_weight + "\n";
        display_string += unsatisfied_links.size() + " unsatisfied links, weighing " + unsatisfied_weight + "\n";
        display_string += "Size-sensitive coherence measure: " + fleming_measure + "\n";
        display_string += "Size-insensitive coherence measure (satisfied / total): " + satisfied_over_total_measure + "\n";
        display_string += "Size-insensitive coherence measure ((satisfied - unsatisfied) / total): " + size_insensitive_fleming_measure + "\n";
		return display_string;
	}
	
}
