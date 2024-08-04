package FactoryDesignPatter.Operations;

import FactoryDesignPatter.Operation;

public class AddOperation implements Operation {

    @Override
    public double calculate(double a, double b) {
        return a + b;
    }
}






