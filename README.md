# Fully automated deployment and configuration of Jenkins 

Collection of Init scripts, configurations and executable Jenkinsfiles loaded automatically into a dockerized Jenkins.

![](JDasC.png)

## Why

This solution was developed to adress some of Jenkins shortcommings seen in different organisations. Jenkins is easy to manually install and configure using the GUI but there are many drawbacks of setting upp servers in this way. 

### Testability:
There is no way to test how upgrading the Jenkins server or any plugin will affect other jobs. The only way is to live deploy and hope that it will be possible to rollback if the tests fail. Rollback rarely works, most issues arrise from interplugin dependencies.
With this solution it is simple to deploy an identical staging server and verify that everything works as expected before deploying it to the production server.

### Scalabilityt:
There is no way to setup an identical server on another site. The only way is to install it and compare the configuration xml files. Checking the same boxes and copy/pasting the configuration options.
This leads to companies having one of these situations: 
* One mega master Jenkins server that contains an enormous amount of jobs that is to big to manage and imposible to adapt to specific project needs.
* A plethora of unique Jenkins instances that are manualy setup and need to be mainatined separately which creates a very large configuration/maintenance overhead cost. This is a common scenario in organizations that work in several different sites/labs/domains.

### Maintainability:
Easier to maintain when environment, configuration, plugins and jobs are stored in SCM. Everything is described in detail with code, version handled, tested and must pass a review process before being admitted.
It is possible to mass manage Jenkins instances by generating them from the same codebase and only apply the different configurations that are project/server specific. 
Best practice is to create one master master server to rule them all. 

### Flexibility:
One implementation or solution doesnt fit all different projects in a organization. This implementation has flexability built into the system.

### Security:
For companies that have strict restrictions on project isolation the safest way to isolate projects is to build them on separate Jenkins servers. 

### Redundency:
Requirements for backup and redundency are not needed. If a Jenkins instance goes down then it is simple to deploy a new Jenkins instance on a new barebone server.

### Development environment:
[Jenkinsfile](https://jenkins.io/doc/book/pipeline/jenkinsfile/) documentation lacks examples when it comes to working with jenkinsfiles in a project.
There is a need of working with job configurations that is versitile and universal. It should be possible to read and modify configutrations from GUI but there must also be possible tu use a IDE tool and modify the code localy. In order to achieve this all code needs to be stored in SCM and all permanent changes need to be checked in and versioned. 
This repository contains one job that will generate Jenkins jobs for all jenkinsfiles found in your repository. In order to force all Jenkins code to be stored in SCM all changes not checked in will automaticaly be overwritten after each new commit in the repository.

### Code reuse:
In order to enable code reuse between different Jenkins instances Jenkins Global Pipeline Library should be configured and used across all instances.

## How it works

A Docker image is built according to the steps in Dockerfile.
Docker compose will build and start the container with arguments located in docker-compose.yaml file.

Steps are: 

* A docker image containing Jenkins is pulled from Docker hub.
* All plugins are downladed and installed to the jenkins instance.
* Jenkins2.sh will apply basic configuration and call jenkins.sh script to apply aditional configurations to Jenkins processes.
* Docker compose will map in resources needed for jenkins to boot.
* Jenkins Groovy init scripts will be executed and create initial jobs on the Jenkins server if they are not already created and mapped.
* Jenkins configuration as code will read jenkins.yaml configuration file and apply all configuration to both Jenkins and all the plugins.
* Jenkins process will finish its boot procedure and is ready to for use.


### Run

This will pull and start latest docker images

    docker-compose pull
    docker-compose up
   
If you have problem with mounting `/var/run/docker.sock` then remove it from `docker-compose.yml` but you won't be able to run jobs which use docker as an agent.

Wait for Jenkins to boot up. Authentication is disabled. Open a browser and go to:

    localhost:8080
    
If you don't see any jobs run Generate_jobs_from_code job to genarate jobs from all jenkinsfiles.

To stop running Docker contianer press `CTRL+C` in terminal.  

To remove all containers with all of its data run:

    docker-compoes down

---

## Updating Jenkins

If you wish to update jenkins for some reason then:

1. Update jenkins version in `Dockerfile`
2. Rebuild docker image and start a new jenkins container.
3. Manually update jenkins plugins using the `Install or update Jenkins plugins` guide.

### Install or update Jenkins plugins

If you just want to test new plugins without committing them to git then stop at step 2.

1. Start jenkins container.
2. Manually install or update plugins through the UI.
* Restart jenkins to verify it's still working.
* Copy output of the following command to `plugins.txt` file (located in this repository):

        curl -s http://localhost:8080/pluginManager/api/json?depth=1 \
          | jq -r '.plugins[] | "\(.shortName):\(.version)"' \
          | sort


2. Alternatively: Write the name and version of the desired plugin to plugins.txt file and it will be downloaded and installed automatically.

3. Rebuild docker image and start a new containers to verify new plugins have been installed:

        docker-compose down
        docker-compose build
        docker-compose up
          
### Clean reboot
To completeley clean and rebuild everything run this command:

        docker-compose down && docker-compose build --no-cache && docker-compose up --force-recreate