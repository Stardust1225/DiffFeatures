import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.reference.CtParameterReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;

public class OneMethod {
    private CtMethod method1, method2;
    private ArrayList<String> content1, content2;
    private double[] result;

    public OneMethod(CtMethod ctMethod1, CtMethod ctMethod2, ArrayList<String> filecontent1, ArrayList<String> filecontent2) {
        this.method1 = ctMethod1;
        this.method2 = ctMethod2;
        this.content1 = filecontent1;
        this.content2 = filecontent2;
    }

    public double[] build() throws Exception {
        if (method1.getBody() == null || method2.getBody() == null)
            return null;

        result = new double[DiffFeatures.FeatureNumber];
        for (int i = 0; i < DiffFeatures.FeatureNumber; i++)
            result[i] = 0;
        try {
            ArrayList<HashSet> result1 = oneMethod(method1, content1, 1);
            ArrayList<HashSet> result2 = oneMethod(method2, content2, -1);

            for (int i = 0; i <= 1; i++) {
                HashSet<Object[]> set1 = result1.get(i), set2 = result2.get(i);
                for (Object s : set2)
                    if (!set1.contains(s))
                        result[1 + i * 3]++;
                for (Object s : set1)
                    if (!set2.contains(s))
                        result[2 + i * 3]++;
            }

            for (int i = 2; i <= 14; i++) {
                HashSet<Object[]> set1 = result1.get(i), set2 = result2.get(i);
                for (Object s : set2)
                    if (!set2.contains(s))
                        result[4 + i * 3]++;
                for (Object s : set1)
                    if (!set2.contains(s))
                        result[5 + i * 3]++;
            }

            for (int i = 15; i < result1.size(); i++) {
                HashSet<Object[]> set1 = result1.get(i), set2 = result2.get(i);
                for (Object s : set2)
                    if (!set2.contains(s))
                        result[6 + i * 3]++;
                for (Object s : set1)
                    if (!set2.contains(s))
                        result[7 + i * 3]++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<HashSet> oneMethod(CtMethod ctMethod, ArrayList<String> filecontent, int kind) {

        HashSet<CtElement> assignSet = new HashSet<>();
        HashSet<CtElement> declareSet = new HashSet<>();
        HashSet<CtElement> staticVariableSet = new HashSet<>();
        HashSet<String> localVariableSet = new HashSet<>();
        HashSet<String> basicVariableSet = new HashSet<>();
        HashSet<String> fieldVariableSet = new HashSet<>();
        HashSet<String> arrayVariableSet = new HashSet<>();
        HashSet<String> parameterSet = new HashSet<>();
        HashSet<String> classSet = new HashSet<>();
        HashSet<CtElement> invocationSet = new HashSet<>();
        HashSet<CtElement> staticInvocationSet = new HashSet<>();
        HashSet<String> operatorSet = new HashSet<>();
        HashSet<CtElement> increaseSet = new HashSet<>();
        HashSet<CtElement> decreaseSet = new HashSet<>();
        HashSet<CtElement> returnSet = new HashSet<>();
        HashSet<CtElement> ifSet = new HashSet<>();
        HashSet<CtElement> trySet = new HashSet<>();
        HashSet<CtElement> catchSet = new HashSet<>();
        HashSet<CtElement> finallySet = new HashSet<>();
        HashSet<CtElement> switchSet = new HashSet<>();
        HashSet<CtElement> caseSet = new HashSet<>();
        HashSet<CtElement> forSet = new HashSet<>();
        HashSet<CtElement> whileSet = new HashSet<>();
        HashSet<CtElement> doWhileSet = new HashSet<>();
        HashSet<CtElement> forEachSet = new HashSet<>();
        HashSet<CtElement> literalSet = new HashSet<>();
        HashSet<CtElement> assertSet = new HashSet<>();
        HashSet<CtElement> exceptionSet = new HashSet<>();
        HashSet<CtElement> innerClassSet = new HashSet<>();
        HashSet<String> oprandSet = new HashSet<>();

        ctMethod.getElements(new Filter<CtElement>() {
            @Override
            public boolean matches(CtElement ctElement) {

                //System.out.println(ctElement.toString()+"  "+ctElement.getClass());
                Class ctElementClass = ctElement.getClass();

                if (ctElementClass.equals(CtLocalVariableImpl.class)) {
                    result[0] += kind;
                    declareSet.add(ctElement);
                    localVariableSet.add(((CtLocalVariable) ctElement).getSimpleName());
                    if (((CtLocalVariable) ctElement).isStatic()) {
                        result[12] += kind;
                        staticVariableSet.add(ctElement);
                    }
                } else if (ctElementClass.equals(CtAssignmentImpl.class)) {
                    result[3] += kind;
                    assignSet.add(ctElement);
                } else if (CtVariableAccessImpl.class.isAssignableFrom(ctElementClass)) {
                    CtVariableReference variableReference = ((CtVariableAccess) ctElement).getVariable();
                    String name = variableReference.getSimpleName();

                    if (localVariableSet.contains(name))
                        result[9] += kind;
                    if (staticVariableSet.contains(name))
                        result[12] += kind;

                    CtTypeReference typeReference = variableReference.getType();
                    if (typeReference != null) {
                        if (typeReference.isPrimitive()) {
                            result[15] += kind;
                            basicVariableSet.add(name);
                        } else {
                            result[27] += kind;
                            classSet.add(name);
                        }
                    }

                    if (CtFieldAccessImpl.class.isAssignableFrom(ctElementClass)) {
                        fieldVariableSet.add(name);
                        result[18] += kind;
                    }

                    if (CtArrayAccessImpl.class.isAssignableFrom(ctElementClass)) {
                        arrayVariableSet.add(name);
                        result[24] += kind;
                    }
                } else if (ctElementClass.equals(CtParameterImpl.class))
                    parameterSet.add(((CtParameter) ctElement).getSimpleName());

                else if (ctElementClass.equals(CtParameterReferenceImpl.class))
                    result[21]++;

                else if (ctElementClass.equals(CtInvocationImpl.class)) {
                    result[30] += kind;
                    invocationSet.add(ctElement);

                    if (((CtInvocation) ctElement).getExecutable().isStatic()) {
                        staticInvocationSet.add(ctElement);
                        result[33] += kind;
                    }
                } else if (ctElementClass.equals(CtBinaryOperatorImpl.class)) {
                    String name = ((CtBinaryOperator) ctElement).getKind().toString();
                    oprandSet.add(((CtBinaryOperator) ctElement).getLeftHandOperand().toString());
                    operatorSet.add(name);
                    result[36] += kind;
                    result[39] += kind;
                } else if (ctElementClass.equals(CtUnaryOperatorImpl.class)) {
                    result[39] += kind;
                    CtUnaryOperator unaryOperator = (CtUnaryOperator) ctElement;
                    if (unaryOperator.getKind().toString().equals("POSTDEC") || unaryOperator.getKind().toString().equals("PREDEC")) {
                        result[53] += kind;
                        increaseSet.add(ctElement);
                    } else {
                        result[54] += kind;
                        decreaseSet.add(ctElement);
                    }
                    oprandSet.add(unaryOperator.getOperand().toString());
                } else if (ctElementClass.equals(CtIfImpl.class)) {
                    result[65] += kind;
                    ifSet.add(ctElement);
                } else if (ctElementClass.equals(CtTryImpl.class)) {
                    result[68] += kind;
                    trySet.add(ctElement);
                } else if (ctElementClass.equals(CtSwitchImpl.class)) {
                    result[77] += kind;
                    switchSet.add(ctElement);
                } else if (ctElementClass.equals(CtForImpl.class)) {
                    result[83] += kind;
                    forSet.add(ctElement);
                } else if (ctElementClass.equals(CtWhileImpl.class)) {
                    result[86] += kind;
                    whileSet.add(ctElement);
                } else if (ctElementClass.equals(CtDoImpl.class)) {
                    result[89] += kind;
                    doWhileSet.add(ctElement);
                } else if (ctElementClass.equals(CtForEachImpl.class)) {
                    result[92] += kind;
                    forEachSet.add(ctElement);
                } else if (ctElementClass.equals(CtBreakImpl.class))
                    result[48] += kind;

                else if (ctElementClass.equals(CtReturnImpl.class)) {
                    result[42] += kind;
                    returnSet.add(ctElement);
                } else if (ctElementClass.equals(CtContinueImpl.class))
                    result[49] += kind;

                else if (ctElementClass.equals(CtLiteralImpl.class)) {
                    result[59] += kind;
                    literalSet.add(ctElement);
                } else if (ctElementClass.equals(CtAssertImpl.class)) {
                    result[56] += kind;
                    assertSet.add(ctElement);
                } else if (ctElementClass.equals(CtClassImpl.class)) {
                    result[62] += kind;
                    innerClassSet.add(ctElement);
                } else if (ctElementClass.equals(CtCatchImpl.class)) {
                    result[71] += kind;
                    catchSet.add(ctElement);
                } else if (ctElementClass.equals(CtCatchVariableImpl.class)) {
                    result[45] += kind;
                    exceptionSet.add(ctElement);
                } else if (ctElementClass.equals(CtCaseImpl.class)) {
                    result[80] += kind;
                    caseSet.add(ctElement);
                }
                return false;
            }
        });

        int startLine = ctMethod.getBody().getPosition().getLine(), endLine = ctMethod.getBody().getPosition().getEndLine();
        result[6] += (endLine - startLine + 1) * kind;
        double max = 0, average = 0;
        for (int i = startLine-1; i <=endLine-1; i++) {
            if (max < filecontent.get(i).length())
                max = filecontent.get(i).length();
            average += filecontent.get(i).length();
        }
        average = average / (endLine - startLine + 1);
        result[7] += max * kind;
        result[8] += average * kind;

        ArrayList<HashSet> result = new ArrayList<>();
        result.add(declareSet);
        result.add(assignSet);
        result.add(localVariableSet);
        result.add(staticVariableSet);
        result.add(basicVariableSet);
        result.add(fieldVariableSet);
        result.add(parameterSet);
        result.add(arrayVariableSet);
        result.add(classSet);
        result.add(invocationSet);
        result.add(staticInvocationSet);
        result.add(operatorSet);
        result.add(oprandSet);
        result.add(returnSet);
        result.add(exceptionSet);
        result.add(increaseSet);
        result.add(decreaseSet);
        result.add(assertSet);
        result.add(literalSet);
        result.add(innerClassSet);
        result.add(ifSet);
        result.add(trySet);
        result.add(catchSet);
        result.add(finallySet);
        result.add(switchSet);
        result.add(caseSet);
        result.add(forSet);
        result.add(whileSet);
        result.add(doWhileSet);
        result.add(forEachSet);
        return result;
    }
}

