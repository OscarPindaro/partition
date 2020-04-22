package com.openfaas.function;

import com.openfaas.function.kubernetesApiWrapper.KubeApi;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class Handler implements com.openfaas.model.IHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        try{
            res.setBody(KubeApi.getNodeList().toString());
        }
        catch (Exception e){
            res.setBody("cavolo");
        }

	    return res;
    }
}
