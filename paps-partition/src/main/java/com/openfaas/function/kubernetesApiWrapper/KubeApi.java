package com.openfaas.function.kubernetesApiWrapper;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class KubeApi {

    private static CoreV1Api coreApi;

    private static AppsV1Api appsApi;

    //TODO controllare che sia il setup per nodi all'interno di kubernetes
    public static void setUpApi(String kubeConfigPath) throws FileNotFoundException, IOException {
        // loading the out-of-cluster config, a kubeconfig from file-system
        ApiClient client =
                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(client);

        coreApi = new CoreV1Api();
        appsApi= new AppsV1Api();
    }

    public static void setUpApi() throws IOException{
        // loading the in-cluster config, including:
        //   1. service-account CA
        //   2. service-account bearer-token
        //   3. service-account namespace
        //   4. master endpoints(ip, port) from pre-set environment variables
        ApiClient client = ClientBuilder.cluster().build();
        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(client);

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
