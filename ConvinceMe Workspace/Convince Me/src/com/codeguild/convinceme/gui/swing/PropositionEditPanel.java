package com.codeguild.convinceme.gui.swing;

import com.codeguild.convinceme.model.Proposition;
import com.codeguild.convinceme.model.ECHOSimulation;
import com.codeguild.convinceme.utils.Debug;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * <p>Description: Dialog that prompts for proposition informationn</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild</p>
 * @author Patti Schank
 */
public class PropositionEditPanel extends JPanel {

    private Proposition mProposition;
    private boolean mIsHypothesis;
    private MainFrame mConfig;
    private JTextField mTextField;
    private JSlider mRatingSlider, mReliabilitySlider;
    private JCheckBox mFact, mMemory, mOpinion, mSomeDisagree;
    private JRadioButton mHypothesis, mData;
    private ButtonGroup mType;

    public PropositionEditPanel(MainFrame config, Proposition p) {
        this(config, p, true);
    }

    public PropositionEditPanel(MainFrame config, Proposition prop, boolean isHyp) {
        mProposition = prop;
        mIsHypothesis = isHyp;
        mConfig = config;

        // lay out interface
        mTextField = new JTextField(30);
        mTextField.setEditable(true);

        mType = new ButtonGroup();
        mHypothesis = new JRadioButton("Hypothesis");
        mData = new JRadioButton("Data");
        mType.add(mHypothesis);
        mType.add(mData);

        mFact = new JCheckBox("Acknowledged fact or statistic");
        mMemory = new JCheckBox("Observation or memory");
        mOpinion = new JCheckBox("One possible inference, opinion, or view");
        mSomeDisagree = new JCheckBox("Some reasonable people might disagree");

        /*
        mRatingSlider = new JSlider(JSlider.HORIZONTAL, 1,
                                    ECHOSimulation.MAX_BELIEVABILITY,
                                    (ECHOSimulation.MAX_BELIEVABILITY + 1) / 2);
        mRatingSlider.setMajorTickSpacing(1);
        mRatingSlider.setPaintTicks(true);
        mRatingSlider.setPaintLabels(true);
        mRatingSlider.setSnapToTicks(true);
		*/
        
        mReliabilitySlider = new JSlider(JSlider.HORIZONTAL, 1,
                                            ECHOSimulation.MAX_RELIABILITY,
                                            ECHOSimulation.MAX_RELIABILITY);
        mReliabilitySlider.setMajorTickSpacing(1);
        mReliabilitySlider.setPaintTicks(true);
        mReliabilitySlider.setPaintLabels(true);
        mReliabilitySlider.setSnapToTicks(true);

        JPanel ratingsPanel = new JPanel();
        /*mRatingSlider.setBorder(new TitledBorder("Believability (1 = low, "
                                                + ECHOSimulation.MAX_BELIEVABILITY
                                                + " = high)"));
        ratingsPanel.setLayout(new BorderLayout(5, 5));
        ratingsPanel.add(mRatingSlider, BorderLayout.NORTH);*/
        
        if (!mIsHypothesis) {
            mReliabilitySlider.setBorder(new TitledBorder("Reliability of evidence (1 = low, " + ECHOSimulation.MAX_RELIABILITY + " = high)"));
            ratingsPanel.add(mReliabilitySlider, BorderLayout.CENTER);
        }
        
        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BorderLayout(5, 5));
        if (mIsHypothesis) {
            propPanel.add(new JLabel("Hypothesis:"), BorderLayout.NORTH);
        } else {
            propPanel.add(new JLabel("Evidence:"), BorderLayout.NORTH);
        }
        propPanel.add(mTextField, BorderLayout.CENTER);

        JPanel checkPanel = new JPanel();
        checkPanel.setLayout(new GridLayout(4, 1));
        checkPanel.add(mFact);
        checkPanel.add(mMemory);
        checkPanel.add(mOpinion);
        checkPanel.add(mSomeDisagree);
        checkPanel.setBorder(new TitledBorder("Check all that apply"));

        JPanel valuesPanel = new JPanel();
        valuesPanel.setLayout(new BorderLayout(5, 5));
        valuesPanel.add(propPanel, BorderLayout.NORTH);
        valuesPanel.add(checkPanel, BorderLayout.CENTER);
        valuesPanel.add(ratingsPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout(2, 2));
        add(valuesPanel, BorderLayout.CENTER);
        setSize(getPreferredSize());

        // load values into the panel
        loadValues();
    }

    public Dimension getPreferredSize() {
        return new Dimension(350, 350);
    }

    public void addHypothesis(Proposition p) {
        mConfig.getArg().addHypothesis(p);
        mConfig.updatePanels();
        mConfig.log("Adding hypothesis " + p.getText());
    }

    public void addData(Proposition p) {
        mConfig.getArg().addData(p);
        mConfig.updatePanels();
        mConfig.log("Adding data " + p.getText());
    }

    /**
     * Load initial values into the panel
     */
    private void loadValues() {
        mHypothesis.setSelected(mIsHypothesis);
        mData.setSelected(!mIsHypothesis);

        mFact.setSelected(mProposition.isFact());
        mMemory.setSelected(mProposition.isMemory());
        mOpinion.setSelected(mProposition.isOpinion());
        mSomeDisagree.setSelected(mProposition.isDisagreeable());

        mTextField.setText(mProposition.getPropText());
        if (mProposition.hasReliability()) {
            mReliabilitySlider.setValue(mProposition.getReliability());
        }

        if (mProposition.isData()) {
            mHypothesis.setSelected(false);
            mData.setSelected(true);
        } else if (mProposition.isHypothesis()) {
            mHypothesis.setSelected(true);
            mData.setSelected(false);
        }
    }

    /**
     * Show this pane in a dialog, and save values if not cancelled
     */
    public void showDialog() {
        loadValues();
        String title;
        if (mIsHypothesis) {
            title = "Edit Hypothesis";
        } else {
            title = "Edit Evidence";
        }
        int response = JOptionPane.showConfirmDialog(null,
                this,
                title,
                JOptionPane.OK_CANCEL_OPTION);

        if (response == JOptionPane.CANCEL_OPTION) {
            Debug.println("User cancelled proposition edit.");
        } else {
            Debug.println("Saving proposition settings.");
            saveValues();
        }
    }

    /**
     * Save the values for this proposition
     */
    private void saveValues() {
        mProposition.setText(mTextField.getText());
        mProposition.setMemory(mMemory.isSelected());
        mProposition.setFact(mFact.isSelected());
        mProposition.setOpinion(mOpinion.isSelected());
        mProposition.setDisagree(mSomeDisagree.isSelected());
        mProposition.setReliability(mReliabilitySlider.getValue());

        if (mHypothesis.isSelected()) {
            mProposition.setType(Proposition.HYPOTHESIS, mConfig.getArg());
            addHypothesis(mProposition);
        } else if (mData.isSelected()) {
            mProposition.setType(Proposition.DATA, mConfig.getArg());
            addData(mProposition);
        }
    }
}