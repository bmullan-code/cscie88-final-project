mvn clean package -DskipTests=true
# create an image
pack build mbtakafkaconsumer --path . --builder  gcr.io/buildpacks/builder:v1
# tag it
docker tag mbtakafkaconsumer:latest barrymullan/mbtakafkaconsumer:latest
# push it
docker push barrymullan/mbtakafkaconsumer:latest


