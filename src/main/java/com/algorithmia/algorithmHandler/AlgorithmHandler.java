package com.algorithmia.algorithmHandler;

import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class AlgorithmHandler<INPUT, OUTPUT, STATE> {

    private ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState;
    private ReflectionHelper.DebuggableFunction<INPUT, OUTPUT> apply;
    private Supplier<STATE> loadFunc;

    private Class algorithmClass;
    private STATE state;


    public AlgorithmHandler(Class algorithmClass, ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState, Supplier<STATE> loadFunc) {
        this.applyWState = applyWState;
        this.loadFunc = loadFunc;
        this.algorithmClass = algorithmClass;
    }

    public AlgorithmHandler(Class algorithmClass, ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState) {
        this.applyWState = applyWState;
        this.algorithmClass = algorithmClass;
    }

    public AlgorithmHandler(Class algorithmClass, ReflectionHelper.DebuggableFunction<INPUT, OUTPUT> apply) {
        this.apply = apply;
        this.algorithmClass = algorithmClass;
    }

    private void Load() {
        if (this.loadFunc != null) {
            state = this.loadFunc.get();
            System.out.println("PIPE_INIT_COMPLETE");
            System.out.flush();
        }
    }

    private void ExecuteWithoutState(RequestHandler<INPUT> in, ResponseHandler out, Function<INPUT, OUTPUT> func) {
        INPUT req = in.GetNextRequest();
        while (req != null) {
            OUTPUT output = func.apply(req);
            out.writeToPipe(output);
            req = in.GetNextRequest();
        }
    }

    private void ExecuteWithState(RequestHandler<INPUT> in, ResponseHandler out, BiFunction<INPUT, STATE, OUTPUT> func) {
        INPUT req = in.GetNextRequest();
        while (req != null) {
            OUTPUT output = func.apply(req, state);
            out.writeToPipe(output);
            req = in.GetNextRequest();
        }
    }

    private void Execute(RequestHandler<INPUT> in, ResponseHandler out) {
        if (this.applyWState != null && this.loadFunc != null) {
            Load();
            ExecuteWithState(in, out, this.applyWState);
        } else if (this.apply != null) {
            ExecuteWithoutState(in, out, this.apply);
        } else {
            throw new RuntimeException("If using an load function with state, a load function must be defined as well.");
        }
    }

    private Class<INPUT> GetInputClass() {
        String methodName;
        if (this.applyWState != null) {
            methodName = ReflectionHelper.getMethodName(this.applyWState);
        } else if (this.apply != null) {
            methodName = ReflectionHelper.getMethodName(this.apply);
        } else {
            throw new RuntimeException("Either Apply(T t) or Apply(T t S s) must be provided to the constructor.");
        }
        Method[] methods = this.algorithmClass.getMethods();
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


    public void run() {
        Class<INPUT> inputClass = GetInputClass();
        RequestHandler<INPUT> in = new RequestHandler<>(inputClass);
        ResponseHandler out = new ResponseHandler();
        try {
            Execute(in, out);
        } catch (RuntimeException e) {
            out.writeErrorToPipe(e);
        }
    }

}
