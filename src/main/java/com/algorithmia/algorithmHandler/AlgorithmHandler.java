package com.algorithmia.algorithmHandler;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class AlgorithmHandler<INPUT, OUTPUT, STATE> {

    private Optional<ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT>> applyWState = Optional.empty();
    private Optional<ReflectionHelper.DebuggableFunction<INPUT, OUTPUT>> apply = Optional.empty();
    private Optional<Supplier<STATE>> loadFunc = Optional.empty();

    private Class algorithmClass;
    private STATE state;


    private void Load() {
        if (this.loadFunc.isPresent()) {
            state = this.loadFunc.get().get();
            System.out.println("PIPE_INIT_COMPLETE");
            System.out.flush();
        }
    }

    private void ExecuteWithoutState(RequestHandler<INPUT> in, ResponseHandler out, Function<INPUT, OUTPUT> func) {
        Optional<INPUT> req = in.GetNextRequest();
        while (req.isPresent()) {
            OUTPUT output = func.apply(req.get());
            out.writeToPipe(output);
            req = in.GetNextRequest();
        }
    }

    private void ExecuteWithState(RequestHandler<INPUT> in, ResponseHandler out, BiFunction<INPUT, STATE, OUTPUT> func) {
        Optional<INPUT> req = in.GetNextRequest();
        while (req.isPresent()) {
            OUTPUT output = func.apply(req.get(), state);
            out.writeToPipe(output);
            req = in.GetNextRequest();
        }
    }

    private void Execute(RequestHandler<INPUT> in, ResponseHandler out) {
        if (this.applyWState.isPresent() && this.loadFunc.isPresent()) {
            Load();
            ExecuteWithState(in, out, this.applyWState.get());
        } else if (this.apply.isPresent()) {
            ExecuteWithoutState(in, out, this.apply.get());
        } else {
            throw new RuntimeException("If using an load function with state, a load function must be defined as well.");
        }
    }

    private Class<INPUT> GetInputClass() {
        Optional<String> methodName;
        if (this.applyWState.isPresent()) {
            methodName = ReflectionHelper.getMethodName(this.applyWState.get());
        } else {
            methodName = ReflectionHelper.getMethodName(this.apply.get());
        }
        if (methodName.isPresent()) {
            Method[] methods = this.algorithmClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName.get())) {
                    Class<?>[] parameters = method.getParameterTypes();
                    return (Class<INPUT>) parameters[0];
                }
            }
            throw new RuntimeException("Unable to find the method reference called " + methodName.get() + " in the provided class.");
        } else {
            throw new RuntimeException("Unable to find the originating definition for method reference");
        }
    }

    public AlgorithmHandler(Class algorithmClass, ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState, Supplier<STATE> loadFunc) {
        this.applyWState = Optional.of(applyWState);
        this.loadFunc = Optional.of(loadFunc);
        this.algorithmClass = algorithmClass;
    }

    public AlgorithmHandler(Class algorithmClass, ReflectionHelper.DebuggableBifunction<INPUT, STATE, OUTPUT> applyWState) {
        this.applyWState = Optional.of(applyWState);
        this.algorithmClass = algorithmClass;
    }

    public AlgorithmHandler(Class algorithmClass, ReflectionHelper.DebuggableFunction<INPUT, OUTPUT> apply) {
        this.apply = Optional.of(apply);
        this.algorithmClass = algorithmClass;
    }


    public void setLoad(Supplier<STATE> func) {
        loadFunc = Optional.of(func);
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
