package com.algorithmia.algorithm;

import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class Handler<INPUT, OUTPUT, STATE> {

    private ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState;
    private ReflectionHelper.DebuggableFunction<INPUT, OUTPUT> apply;
    private Supplier<STATE> loadFunc;

    private STATE state;
    private RequestHandler<INPUT> in;
    private ResponseHandler out = new ResponseHandler();


    public Handler(Class algorithmClass, ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState, Supplier<STATE> loadFunc) {
        this.applyWState = applyWState;
        this.loadFunc = loadFunc;
        Class<INPUT> inputClass = getInputClass(algorithmClass);
        this.in = new RequestHandler<>(inputClass);
    }

    public Handler(Class algorithmClass, ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState) {
        this.applyWState = applyWState;
        Class<INPUT> inputClass = getInputClass(algorithmClass);
        this.in = new RequestHandler<>(inputClass);
    }

    public Handler(Class algorithmClass, ReflectionHelper.DebuggableFunction<INPUT, OUTPUT> apply) {
        this.apply = apply;
        Class<INPUT> inputClass = getInputClass(algorithmClass);
        this.in = new RequestHandler<>(inputClass);
    }

    private void load() {
        if (this.loadFunc != null) {
            state = this.loadFunc.get();
            System.out.println("PIPE_INIT_COMPLETE");
            System.out.flush();
        }
    }

    private void executeWithoutState(Function<INPUT, OUTPUT> func) {
        INPUT req = in.getNextRequest();
        while (req != null) {
            OUTPUT output = func.apply(req);
            out.writeToPipe(output);
            req = in.getNextRequest();
        }
    }

    private void executeWithState(BiFunction<INPUT, STATE, OUTPUT> func) {
        INPUT req = in.getNextRequest();
        while (req != null) {
            OUTPUT output = func.apply(req, state);
            out.writeToPipe(output);
            req = in.getNextRequest();
        }
    }

    private void execute() {
        if (this.applyWState != null && this.loadFunc != null) {
            load();
            executeWithState( this.applyWState);
        } else if (this.apply != null) {
            executeWithoutState(this.apply);
        } else {
            throw new RuntimeException("If using an load function with state, a load function must be defined as well.");
        }
    }

    private Class<INPUT> getInputClass(Class algoClass) {
        String methodName;
        if (this.applyWState != null) {
            methodName = ReflectionHelper.getMethodName(this.applyWState);
        } else if (this.apply != null) {
            methodName = ReflectionHelper.getMethodName(this.apply);
        } else {
            throw new RuntimeException("Either Apply(INPUT t) or Apply(INPUT t STATE s) must be defined.");
        }
        Method[] methods = algoClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] parameters = method.getParameterTypes();
                return (Class<INPUT>) parameters[0];
            }
        }
        throw new RuntimeException("Unable to find the method reference called " + methodName + " in the provided class.");
    }

    public void setLoad(Supplier<STATE> func) {
        loadFunc = func;
    }

    public void serve() {
        try {
            execute();
        } catch (RuntimeException e) {
            out.writeErrorToPipe(e);
        }
    }

}
