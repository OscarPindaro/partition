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
import com.openfaas.function.partition.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Handler implements com.openfaas.model.IHandler {

    public IResponse Handle(IRequest req) {

        Response res = new Response();
        /***** instantiate kubernetes api *****/
        try{
            KubeApi.setUpApi();
        }
        catch (Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            res.setBody("Exception: " + e +"\nMessage: " +e.getMessage() + "\nStacktrace:\n" + sw.toString());
        }

        /***** creation of the parser ******/
        int numLines = countLines(req.getBody());
        if (numLines == -1){
            res.setBody("Errore num lines " + numLines);
            return res;
        }

        InputParser parser = new InputParser(req.getBody(), 4);
        parser.parseFile();

        SLPA slpa;
        try{

            slpa = new SLPA(parser.getDelayMatrix(), 0.6f);
            //res.setBody(slpa.topologyNodes.get(0).getNodeID() + "\n" + slpa.topologyNodes.get(0).getKubeNode().toString());
        }
        catch(ApiException api){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            api.printStackTrace(pw);
            res.setBody("Exception: " + api +"\nMessage: " +api.getMessage() + "\nStacktrace:\n" + sw.toString());
            System.out.println("rifiutato");
        }
        catch(RuntimeExceptio run){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            api.printStackTrace(pw);
            res.setBody("Exception: " + run +"\nMessage: " +run.getMessage() + "\nStacktrace:\n" + sw.toString());
            System.out.println("empty node list");
        }
        catch (Exception ex){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            api.printStackTrace(pw);
            res.setBody("Exception: " + ex +"\nMessage: " +ex.getMessage() + "\nStacktrace:\n" + sw.toString());
            System.out.println("other exception");

        }

        return res;

    }



    private static int countLines(String str){
        if(str == null){
            return -1;
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
