package com.algorithmia.algorithmHandler;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Optional;

public class AlgorithmHandler<INPUT, STATE, OUTPUT extends  Serializable> {

    @FunctionalInterface
    public interface BifunctionWithException<INPUT, STATE, OUTPUT> {
        OUTPUT apply(INPUT t, STATE j) throws Throwable;
    }

    @FunctionalInterface
    public interface FunctionWithException<INPUT, OUTPUT>{
        OUTPUT apply(INPUT t) throws Throwable;
    }

    @FunctionalInterface
    public interface SupplierWithException<STATE> {
        STATE apply() throws Throwable;
    }


    private BifunctionWithException<INPUT, STATE, OUTPUT> applyWState;
    private FunctionWithException<INPUT, OUTPUT> apply;
    private Class<INPUT> inputClass;
    private SupplierWithException<STATE> loadFunc = ()-> {return null;};
    private STATE state;


    private void Load() throws Throwable{
        state = this.loadFunc.apply();
        System.out.println("PIPE_INIT_COMPLETE");
        System.out.flush();
    }

    private void ExecuteWithoutState(RequestHandler<INPUT> in, ResponseHandler out) throws Throwable {
        Optional<INPUT> req = in.GetNextRequest();
        while(req.isPresent()){
            OUTPUT output = this.apply.apply(req.get());
            out.writeToPipe(output);
            req = in.GetNextRequest();
        }
    }

    private void ExecuteWithState(RequestHandler<INPUT> in, ResponseHandler out) throws Throwable{
        Optional<INPUT> req = in.GetNextRequest();
        while(req.isPresent()){
            OUTPUT output = this.applyWState.apply(req.get(), state);
            out.writeToPipe(output);
            req = in.GetNextRequest();
        }
    }


    public AlgorithmHandler(BifunctionWithException<INPUT, STATE, OUTPUT> applyWState, SupplierWithException<STATE> loadFunc, Class<INPUT> inputClass){
        this.applyWState = applyWState;
        this.loadFunc = loadFunc;
        this.inputClass = inputClass;
    }

    public AlgorithmHandler(BifunctionWithException<INPUT, STATE, OUTPUT> applyWState, Class<INPUT> inputClass){
        this.applyWState = applyWState;
        this.inputClass = inputClass;
    }

    public AlgorithmHandler(FunctionWithException<INPUT, OUTPUT> apply, Class<INPUT> inputClass){
        this.apply = apply;
        this.inputClass = inputClass;
    }


    public void setLoad(SupplierWithException<STATE> func){
        loadFunc = func;
    }
    public void run() throws IOException {
        RequestHandler<INPUT> in = new RequestHandler<>(this.inputClass);
        ResponseHandler out = new ResponseHandler();
        try {

            if(this.applyWState != null) {
                Load();
                ExecuteWithState(in, out);
            } else {
                ExecuteWithoutState(in, out);
            }

    } catch (Throwable e){
        out.writeErrorToPipe(e);
        }
    }

}
