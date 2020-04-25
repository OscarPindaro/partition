package com.openfaas.function;

import com.openfaas.function.kubernetesApiWrapper.KubeApi;
import com.openfaas.function.partition.InputParser;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Handler implements com.openfaas.model.IHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        setUpApi(res);


        InputParser parser = new InputParser(req.getBody(), countLines(req.getBody()));
        parser.parseFile();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        for (String node: parser.getNodes()) {
            pw.println(node);
            float[] delays = parser.getDelayMatrix()[parser.getNodes().indexOf(node)];
            for (int i = 0; i < delays.length; i++) {
                pw.println(delays[i]);
            }
        }

        res.setBody(sw.toString());

	    return res;
    }



    private static int countLines(String str){
        if(str == null){
            throw new NullPointerException("Null String passed as argument");
        }
        int numLines = 0;
        for(int i=0; i < str.length(); i++){
            if(str.charAt(i) == '\n'){
                numLines++;
            }
        }
        return numLines;
    }

    private void setUpApi(IResponse res){
        try{
            KubeApi.setUpApi();
        }
        catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            res.setBody("Exception: " + e +"\nMessage: " +e.getMessage() + "\nStacktrace:\n" + sw.toString());
            System.out.println("rifiutato");
        }
    }
}
