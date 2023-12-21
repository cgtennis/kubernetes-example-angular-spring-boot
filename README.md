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