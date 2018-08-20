docker-compose -f docker-compose-all.yaml down
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
docker rmi  $(docker images -a | grep dev-  | awk '{print $3 }')
