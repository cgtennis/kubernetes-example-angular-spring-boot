### build & run employee-manager-api docker image
change directory to `employee-manager-api`
```sh
cd employee-manager-api
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
