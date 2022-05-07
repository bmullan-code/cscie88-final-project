mvn clean package -DskipTests=true
# create an image
pack build mbtastreamingclient --path . --builder  gcr.io/buildpacks/builder:v1
# tag it
docker tag mbtastreamingclient:latest barrymullan/mbtastreamingclient:latest
# push it
docker push barrymullan/mbtastreamingclient:latest


