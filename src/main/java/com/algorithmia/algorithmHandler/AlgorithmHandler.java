package com.algorithmia.algorithmHandler;

import java.io.Serializable;
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


    private Optional<BifunctionWithException<INPUT, STATE, OUTPUT>> applyWState = Optional.empty();
    private Optional<FunctionWithException<INPUT, OUTPUT>> apply = Optional.empty();
    private Class<INPUT> inputClass;
    private Optional<SupplierWithException<STATE>> loadFunc = Optional.empty();
    private STATE state;


    private void Load(){
        try{
            if(this.loadFunc.isPresent()) {
                state = this.loadFunc.get().apply();
                System.out.println("PIPE_INIT_COMPLETE");
                System.out.flush();
            }
        } catch (Throwable e){ throw new RuntimeException(e);}
    }

    private void ExecuteWithoutState(RequestHandler<INPUT> in, ResponseHandler out, FunctionWithException<INPUT, OUTPUT> func) {
        try {
            Optional<INPUT> req = in.GetNextRequest();
            while (req.isPresent()) {
                OUTPUT output = func.apply(req.get());
                out.writeToPipe(output);
                req = in.GetNextRequest();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void ExecuteWithState(RequestHandler<INPUT> in, ResponseHandler out, BifunctionWithException<INPUT, STATE, OUTPUT> func) {
        try {
            Optional<INPUT> req = in.GetNextRequest();
            while (req.isPresent()) {
                OUTPUT output = func.apply(req.get(), state);
                out.writeToPipe(output);
                req = in.GetNextRequest();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void Execute(RequestHandler<INPUT> in, ResponseHandler out){
        if(this.applyWState.isPresent() && this.loadFunc.isPresent()){
            Load();
            ExecuteWithState(in, out, this.applyWState.get());
        } else if (this.apply.isPresent()) {
            ExecuteWithoutState(in, out, this.apply.get());
        } else {
            throw new RuntimeException("If using an apply function with state, a load function must be defined as well.");
        }
    }


    public AlgorithmHandler(BifunctionWithException<INPUT, STATE, OUTPUT> applyWState, SupplierWithException<STATE> loadFunc, Class<INPUT> inputClass){
        this.applyWState = Optional.of(applyWState);
        this.loadFunc = Optional.of(loadFunc);
        this.inputClass = inputClass;
    }

    public AlgorithmHandler(BifunctionWithException<INPUT, STATE, OUTPUT> applyWState, Class<INPUT> inputClass){
        this.applyWState = Optional.of(applyWState);
        this.inputClass = inputClass;
    }

    public AlgorithmHandler(FunctionWithException<INPUT, OUTPUT> apply, Class<INPUT> inputClass){
        this.apply = Optional.of(apply);
        this.inputClass = inputClass;
    }


    public void setLoad(SupplierWithException<STATE> func){
        loadFunc = Optional.of(func);
    }

    public void run() {
        RequestHandler<INPUT> in = new RequestHandler<>(this.inputClass);
        ResponseHandler out = new ResponseHandler();
        try {
            Execute(in, out);
    } catch (RuntimeException e){
        out.writeErrorToPipe(e);
        }
    }

}
