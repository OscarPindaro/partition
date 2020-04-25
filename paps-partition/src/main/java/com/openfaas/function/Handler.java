package com.openfaas.function;

import com.openfaas.function.kubernetesApiWrapper.KubeApi;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Handler implements com.openfaas.model.IHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        try{
            KubeApi.setUpApi();
            List<V1Node> list = KubeApi.getNodeList();
            res.setBody(list.toString());
        }
        catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            res.setBody("Exception: " + e +"\nMessage: " +e.getMessage() + "\nStacktrace:\n" + sw.toString()  + "\nrifiutato");
            System.out.println("rifiutato");


        }

	    return res;
    }
}
