## Docker, Kubernetes,Helm Learning via Simple AngularUI and Spring Boot Java apps

Tools installation such as WSL, docker, docker-compose, kubectl, kind won't be covered in this example. Refer to the offical tools document for installation (as it's also environment specific)

### prerequisites

* prerequisite for step 1-2
  - docker environment under linux has been setup (preferrably on WSL2 under Windows 10, Linux VM also works)
* prerequisite for step 3
  - docker-compose has been installed  (preferrably on WSL2 under Windows 10, Linux VM also works)  
* prerequisite for step 4
  - kind docker image is installed under docker engine and running
  - kubectl is installed
* prerequisite for step 5
  - Helm is installed

### Step 1: build & run employee-manager-api docker image
```
git clone https://github.com/cgtennis/kubernetes-example-angular-spring-boot.git
```
change directory to `employee-manager-api`
```sh
cd employee-manager-api
# clean up previously built image before build
docker rmi -f employee-manager-api
docker build --no-cache -t employee-manager-api .
```
verify the image
```sh
docker images
```
run docker image as daemon  (after --name, it's cogit ntainer name, then image name/tag)
```sh
docker run -d -p 8080:8080 --name employee-manager-api employee-manager-api
docker ps
```
To verify from a brower, enter `http://localhost:8080/employee/all` in chrome, it should return an empty list `[]`, which is expected.

### Step 2: build & run employee-manager-ui docker image
change directory to `employee-manager-ui`
```sh
cd employee-manager-ui
# clean up previously built image before build
docker rmi -f employee-manager-ui
# set environment variable for API base URL (http://localhost:8080) and pass into angular UI docker build process 
export API_BASE_URL=http://localhost:8080
docker build --no-cache --build-arg API_BASE_URL=$API_BASE_URL -t employee-manager-ui .
```
* if running api (step 1) on a remote server say 192.168.1.5:8080, need to add build-arg for ui docker build (which will be built into javascript)
referring to this article about [How to passing environment variables to Angular](https://dzone.com/articles/using-environment-variable-with-angular)
```
export API_BASE_URL=http://192.168.1.5:8080
docker build --no-cache --build-arg API_BASE_URL=$API_BASE_URL -t employee-manager-ui .
```
verify the image
```sh
docker images
```
run docker image as daemon  (4200 is localhost port, 80 is the port from the image's nginx)
```sh
docker run -d -p 4200:80 --name employee-manager-ui employee-manager-ui
docker ps
```
To verify from a brower, enter `http://localhost:4200` in chrome, you should be able to add employee, search and delete
* Experiment - TODO
  - stop the docker containers 
  - modify the port of api to 7777
  - rebuild ui and pass 7777 to docker build process
  - re-verify everything


### Step 3: use docker-compose to pull the images from docker hub and run them
- these images are pushed to docker account 'cgtennis', two repositories already created in docker hub
  - employee-manager-ui
  - employee-manager-api
* Do NOT do it yourself for docker push, as you don't have write permission to my account
```sh (
docker login
# push the locally built image "employee-manager-ui:latest" to docker hub
# push UI
docker tag employee-manager-ui:latest  cgtennis/employee-manager-ui:latest
docker push cgtennis/employee-manager-ui:latest
# push the locally built image "employee-manager-api:latest" to docker hub
# push API
docker tag employee-manager-api:latest  cgtennis/employee-manager-api:latest
docker push cgtennis/employee-manager-api:latest
```

Let's clean up local docker images before running docker compose
```sh
docker stop employee-manager-ui
docker rm employee-manager-ui
docker rmi employee-manager-ui
docker stop employee-manager-api
docker rm employee-manager-api
docker rmi employee-manager-api
docker rmi cgtennis/employee-manager-ui:latest
docker rmi cgtennis/employee-manager-api:latest
docker image prune
docker images   # to double check there are no left-over employee-manager related images
```
to start containers defined in `docker-compose/`
```
cd docker-compose
docker-compose up -d
```
to stop containers defined in docker-compose
```
docker-compose down
```
containeres are stopped and removed. Now to remove images
```
docker rmi cgtennis/employee-manager-ui:latest
docker rmi cgtennis/employee-manager-api:latest
```

another way to clean up all containers & images
```sh
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker rmi $(docker images -q)
```


### Step 4: Kubectl, use yaml files to create/deploy kubernetes pods/deployments, services, ConfigMap and Secret
We can download `kubectl` from the [Official Docs](https://kubernetes.io/docs/tasks/tools/) 

we will be using [kind](https://kind.sigs.k8s.io/) to create our test cluster.
Kind (running under docker) is an light-weight tool for running test clusters locally, more light-weight than Minikube
Also it's eaiser for Kind to create multiple clusters.
[kind quick start](https://kind.sigs.k8s.io/docs/user/quick-start/)

we will run two clusters side by side so we can demonstrate cluster access (I've already have `kind` installed)

```
kind create cluster --name dev --image kindest/node:v1.23.5
kind create cluster --name prod --image kindest/node:v1.23.5
```
use kubectl to get contexts, clusters, and switch current context, etc
(A `context in Kubernetes` represents a Kubernetes cluster, a user, and a namespace combination.)
```
kubectl config   #list sub-command of config 
kubectl config get-contexts  # get all clusters
kubectl config use-context kind-dev  # set current context to kind-dev instead of `set-context` 
```
See cluster up and running in `kind-dev`:
```
kubectl get nodes
NAME                STATUS   ROLES                  AGE   VERSION
dev-control-plane   Ready    control-plane,master   10m   v1.23.5
```

By default, kind create 1 control-plane and 1 work-node per cluster. We can adjustment number of control-plane or number of work-node.
To create a kind cluster with one control-plane node and two worker nodes, you can use the following configuration file (kind-config.yaml):
```
kind delete cluster --name dev
kind create cluster --name dev --image kindest/node:v1.23.5 --config ./kind/kind-config-2-worker
# examine two nodes
kubectl get nodes -A -o wide
kubectl get pods -A -o wide
```
For simplicity, we're still using one cluster. However we'll need to map a cluster port to a host port
```
kind delete cluster --name dev
kind create cluster --name dev --image kindest/node:v1.23.5 --config ./kind/kind-config-port-30000.yaml
```



* Working with Kubernetes resources
Now that we have cluster access, next we can read resources from the cluster
with the `kubectl get` command.

* Namespaces
Most kubernetes resources are namespace scoped:
By default, `kubectl` commands will run against the `default` namespace
```
kubectl get namespaces
```
create a namespace with the `kubectl create` command
```
kubectl create ns employee-manager
```
* Working with YAML
The most common and recommended way to create resources in Kubernetes is with the `kubectl apply` command. </br>
This command takes in declarative `YAML` files. Now let's apploy the deployments of the two employee-manager images
```
$ kubectl -n employee-manager apply -f ./kubernetes/kubectl-yaml/deployments.yaml
deployment.apps/employee-manager-api created
deployment.apps/employee-manager-ui created
```
Examine the two employee-manager pods that's created.
```
kubectl get pods -A -o wide
```
we will find both the `employee-manager-api` and `employee-manager-ui` are in the list. `-A` means all namespaces, `-o wide` flag to display additional information

Now, to apply the two services
```
$ kubectl -n employee-manager apply -f ./kubernetes/kubectl-yaml/services.yaml
```
Examine the two services that's created.
```
kubectl get services -A -o wide
```

We can checkout our site with the `port-forward` command (ClusterIP is internal, there is no external IP for both services)
Open Terminal #1
```
kubectl port-forward -n employee-manager service/employee-manager-api-service 8080:8080
```
Use a browser to check http://localhost:8080/employee/all

Open Terminal #2
```
kubectl port-forward -n employee-manager service/employee-manager-ui-service 4200:4200
```
Use a brower to check http://localhost:4200

(we keep the API service localhost 8080 port-forwarding running)
Now we terminate the 4200 port-forwarding and try to use NodePort

By default, the service type is ClusterIP (which is internal IP inside cluster). We can change it to Node Port which is external IP (but limited range 30000-32767)
```
# dry run to validate yaml syntax
$ kubectl -n employee-manager apply -f ./kubernetes/kubectl-yaml/services-node-port.yaml --dry-run=client
# if validation is good, apply the service
$ kubectl -n employee-manager apply -f ./kubernetes/kubectl-yaml/services-node-port.yaml
```
This will open up the UI service in the pods to the hosting node (i.e. EC2 in AWS) 
Let's check `NodePort` for the UI service
```
kubectl get services -n employee-manager -o wide   # Examine the 30000+ port
kubectl get nodes -o wide    # from here we get <node's ip address>, we should be able to ping it (i.e. EC2 in AWS)
kubectl get pods -n employee-manager -o wide  # check the UI is assigned to which node 
kubectl describe svc -n employee-manager employee-manager-ui-service   # double check the service
``` 
Somehow, I can't open http://<node's ip address>:30000 in a brower. 
However, I can `curl http://<node's ip address>:30000` from the wsl terminal
There is why: Kubernetes on Windows Docker Desktop by default runs its components in WSL2 (Windows subsystem for Linux), it's separate virtual machine with its own IP address and localhost. This is the reason why service is not reachable on localhost from host OS (in this case Windows).

Let's try Load Balancer service type
```
# dry run to validate yaml syntax
$ kubectl -n employee-manager apply -f ./kubernetes/kubectl-yaml/services-load-balancer.yaml --dry-run=client
# if validation is good, apply the service
$ kubectl -n employee-manager apply -f ./kubernetes/kubectl-yaml/services-load-balancer.yaml
```
Now External IP for the UI service shows pending. The external IP provisioning process happens asynchronously. This IP assignment process is dependent on the Kubernetes hosting environment (the cloud provider). 
```
kubectl get services -n employee-manager -o wide  
```

* References: List resources in a namespace
```
kubectl get pods
kubectl get deployments
kubectl get services
kubectl get configmaps
kubectl get secrets
kubectl get ingress
```

* Clean up

```
kind delete cluster --name dev
kind delete cluster --name prod

```

### Step 5: use Helm charts to deploy kubernetes pods/deployments, services, ConfigMap and Secret



### Step 6: use Helm charts with values.yaml for multiple environment