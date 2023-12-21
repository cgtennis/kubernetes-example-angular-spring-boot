## Docker, Kubernetes,Helm Learning via Simple AngularUI and Spring Boot Java apps

### prerequisite:
- docker environment under linux has been setup (preferrably on WSL2 under Windows 10, Linux VM also works)
- docker, kubectl, helm command all setup (you don't need to run `sudo docker`)


### Step 1: build & run employee-manager-api docker image
change directory to `employee-manager-api`
```sh
cd employee-manager-api
# clean up previously built image before build
docker rmi -f employee-manager-api
docker build -t employee-manager-api .
```
verify the image
```sh
docker images
```
run docker image as daemon  (after --name, it's container name, then image name/tag)
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
docker build -t employee-manager-ui .
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


### Step 3: use docker compose to pull the images from docker hub and run them
- these images are pushed to docker account 'cgtennis', two repositories already created in docker hub
  - employee-manager-ui
  - employee-manager-api
```sh
docker Login
# assuming locally built image: employee-manager-ui:latest
# push UI
docker tag employee-manager-ui:latest  cgtennis/employee-manager-ui:latest
docker push cgtennis/employee-manager-ui:latest
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
