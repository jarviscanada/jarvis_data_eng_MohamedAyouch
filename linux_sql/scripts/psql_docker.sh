#! /bin/sh

#cmd arguments
cmd=$1
db_username=$2
db_password=$3

# checks if docker is started, if not starts it
sudo systemctl status docker || systemctl start docker

#checks container status and store it in container_status
docker container inspect jrvs-psql
container_status=$?

#use switch for the 3 cmd options
case $cmd in
  create)

  #checks if a container already exists
  if [ $container_status -eq 0 ]; then
    echo "Container already exists"
    exit 1
  fi

  #checks if the number of arguments is 3
  if [ $# -ne 3 ]; then
    echo 'Create requires username and password'
    exit 1
  fi

  # create the container and run it using the postgres image
  docker volume create pgdata

  docker run --name jrvs-psql -e POSTGRES_PASSWORD=$db_password -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres:9.6-alpine

  exit $?
  ;;

  start|stop)

#check if there's a container created
  if [ $container_status -ne 0 ]; then
    echo "Container not created"
    exit 1
  fi

#stops or starts the container
  docker container $cmd jrvs-psql
  exit $?
  ;;

#Handle if the commands passed are not start,stop or create
  *)
  echo "Illegal command"
  echo "Commands: start|stop|create"
  exit 1
  ;;

esac



