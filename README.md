# ms78-singleJdbc

### Prepare mysql docker containers
### Pull image from docker.hub
docker pull mysql
docker run -p 3306:3306 --name some-mysql-1 -e MYSQL_ROOT_PASSWORD=sa -d mysql
docker run -p 3307:3306 --name some-mysql-2 -e MYSQL_ROOT_PASSWORD=sa -d mysql

### Fill DB with data
docker exec -it some-mysql-1 mysql -u root -psa  -e "$(cat ./DB_data.sql)"
docker exec -it some-mysql-2 mysql -u root -psa  -e "$(cat ./DB_data.sql)"

### Init oc
eval $(minishift oc-env)

### Remove project ms78 if already exist
# oc delete ns ms78

### Login OS as admin
oc login -u admin -p admin

### Create new project
oc new-project ms78

### If need internal OS docker registry (instead of remote) to load container
eval $(minishift docker-env)
docker login -u $(oc whoami) -p $(oc whoami -t) $(minishift openshift registry)

### Create config maps with link to DB
oc apply -f ms78_configmap-mysql-1.yml
oc apply -f ms78_configmap-mysql-2.yml
### Print config maps
oc get cm

### Create two external services to both mysql DBs
# In "subsets.addresses.ip" use your kvm2 driver ip. To see it execute next command:
minishift ssh "route -n | grep ^0.0.0.0 | awk '{ print \$2 }'"
### Add IP into both ext-service yamls. Then execute:
oc apply -f ms78_mysql-1-ext-service.yaml
oc apply -f ms78_mysql-2-ext-service.yaml
### Print services
oc get svc

### Deploy MS
mvn clean package
docker build -t ms78/jdbc-app .
docker tag ms78/jdbc-app $(minishift openshift registry)/ms78/jdbc-app
docker push $(minishift openshift registry)/ms78/jdbc-app
oc delete deployment jdbc-app-1 -n ms78
oc delete deployment jdbc-app-2 -n ms78
oc apply -f ms78_deployment-mysql-1.yml -n ms78
oc apply -f ms78_deployment-mysql-2.yml -n ms78
oc get pods

### Create and expose service for our MS to make it accessible for rest call
# This service links both previously created created MSs
oc apply -f ms78_service.yml -n ms78
oc expose service ms78-jdbc-app


### Confugure Istio Ingress to access from external to minishift
# In our case ingress is used for load balancing http traffic, retrying and circuit breaking
# We use "http-ms78.example.com" value for http "host" header for routing
oc apply -f ms78-ingress-gateway.yaml
# http.retries are configured in VS
oc apply -f ms78-virtualService.yaml

### Circuit breaker is configured in Destination rule
oc apply -f ms78-destinationRule.yaml

### Configure Istio ingress GW
# As the is no external LoadBalancer in minishift, we are going to use NodePort port to access Ingress
#export INGRESS_HOST=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
export INGRESS_HOST=$(minishift ip)
export INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
export SECURE_INGRESS_PORT=$(oc -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].port}')
echo $INGRESS_HOST:$INGRESS_PORT

### Now all resources are created and configured. Lets make http call. You have to see no errors in response and in pods console
curl -I -HHost:http-ms78.example.com http://$INGRESS_HOST:$INGRESS_PORT/cities

### Now you can stop one of mysql docker containers and check, that you steal receive http 200 responses despite the fact that one of our MS doesn't work
# Don't forget call this commad in other terminal, because now you are logged in internal OS registry
docker stop some-mysql-1