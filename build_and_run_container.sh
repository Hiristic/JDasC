#!/usr/bin/env bash
docker stop jenkins
docker rm jenkins
docker build . -t jenkins
docker run -it \
    --env CONF_ALLOW_RUNS_ON_MASTER=true \
    --name jenkins \
    --volume /var/run/docker.sock:/var/run/docker.sock \
    --volume ${PWD}/jenkins.yaml:/var/jenkins_home/jenkins.yaml \
    -p 8080:8080 -p 50000:50000 \
    jenkins
