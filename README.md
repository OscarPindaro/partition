# partition
This repo contains the implementation of the Partition module of PAPS.
It follows the structure required by openfaas by its building procedure.
There is no main in this project, and in order to test its functionalities you need to have a Kubernetes cluster with OpenFaas deployed on it (how to deploy openfaas: https://docs.openfaas.com/deployment/kubernetes/ ).

In order to deploy this code, the command faas-cli up -f file.yml has to be launched from terminale.

The code inside the container calls the Kubernetes API, but since its inside the cluster, it doesn't have the permissions to make REST calls and modify kubernetes objects.
In order to solve this:

kubectl create -f NodeRole 

kubectl create [binding-nome] --clusterrole=[nameInNodeRole] --serviceaccount=[partitionUser]

In our case, the user should be openfaas-fn:deafult (you can read it in the exception rased by the container with error code 503)
