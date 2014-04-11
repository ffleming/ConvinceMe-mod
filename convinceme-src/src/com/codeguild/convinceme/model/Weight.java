package com.codeguild.convinceme.model;

/**
 * <p>Description: Weight of a proposition.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: CodeGuild, Inc. </p>
 * @author Patti Schank
 */
public class Weight extends Object {

    private Proposition mProp;
    private float mWeight = 0;

    public Weight(Proposition p, float w) {
        mProp = p;
        mWeight = w;
    }

    public void setWeight(float f) {
        mWeight = f;
    }

    public void setProposition(Proposition p) {
        mProp = p;
    }

    public float getWeight() {
        return mWeight;
    }

    public Proposition getProposition() {
        return mProp;
    }

}

