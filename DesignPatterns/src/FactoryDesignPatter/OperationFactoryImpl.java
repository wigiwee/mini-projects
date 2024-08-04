package FactoryDesignPatter;

import FactoryDesignPatter.Operations.AddOperation;
import FactoryDesignPatter.Operations.DivisionOperation;
import FactoryDesignPatter.Operations.MultiplyOperation;
import FactoryDesignPatter.Operations.SubtractOperation;

public class OperationFactoryImpl implements OperationFactory{

    @Override
    public Operation getInstance(String operationType) throws InvalidOperation {

        if(operationType.equalsIgnoreCase("+")){
            return new AddOperation();
        }else if(operationType.equalsIgnoreCase("-")){
            return new SubtractOperation();
        }else if(operationType.equalsIgnoreCase("*")){
            return new MultiplyOperation();
        }else if(operationType.equalsIgnoreCase("/")){
            return new DivisionOperation();
        }else {
            throw new InvalidOperation("Invalid Operation");
        }
    }
}
