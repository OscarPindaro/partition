package com.openfaas.function.kubernetesApiWrapper;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * this class is a wrapper for the kubernetes-client java API.
 * No object needs to be instantiated, since all it's methods are static.
 */
public class KubeApi {

    /**
     * This object handles the REST calls of the node object.
     */
    private static CoreV1Api coreApi;

    /**
     * This method is used to call the API from outside the cluster
     * @param kubeConfigPath the path of the config file. Usually at $HOME/.kube/config.
     * @throws FileNotFoundException If the path of the file it not correct
     * @throws IOException
     */
    public static void setUpApi(String kubeConfigPath) throws FileNotFoundException, IOException {
        // loading the out-of-cluster config, a kubeconfig from file-system
        ApiClient client =
                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(client);

        coreApi = new CoreV1Api();
    }

    /**
     * This method builds the client so that it can call the kubernetes API inside a pod.
     * @throws IOException
     */
    public static void setUpApi() throws IOException{
        // loading the in-cluster config, including:
        //   1. service-account CA
        //   2. service-account bearer-token
        //   3. service-account namespace
        //   4. master endpoints(ip, port) from pre-set environment variables
        ApiClient client = null;
        try{
            client = ClientBuilder.cluster().build();
        }
        catch(Exception e){
            throw new IOException(e.getMessage() + ". Problems while building the client.");
        }
        // set the global default api-client to the in-cluster one from above
        try
        {
            Configuration.setDefaultApiClient(client);
        }
        catch(Exception e){
            throw new IOException(e.getMessage() + ".\n Error while adding the client to the configuration");
        }
        coreApi = new CoreV1Api(client);
    }

    public static List<V1Node> getNodeList() throws ApiException {
        List<V1Node> list = coreApi.listNode(null, null, null, null,
                null, null, null, null,
                null).getItems();
        return list;
    }

    public static void updateNode(V1Node node) throws ApiException{
        coreApi.replaceNode(node.getMetadata().getName(), node, null, null);
    }


}
