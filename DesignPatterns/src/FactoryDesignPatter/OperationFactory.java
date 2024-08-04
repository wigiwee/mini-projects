package FactoryDesignPatter;

public interface OperationFactory {
    Operation getInstance(String operationType) throws InvalidOperation;
}
