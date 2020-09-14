FROM docker.io/jenkins/jenkins

LABEL Description="This container will setup Jenkins with Docker, install plugins, run Groovy Init Scripts and configure with Config-as-Code"

USER root
RUN apt-get update -y && \
    apt-get install -y awscli jq gettext-base tree vim zip git maven emacs sshpass

USER jenkins

COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh $(cat /usr/share/jenkins/plugins.txt | tr '\n' ' ')

COPY init_scripts/src/main/groovy/ /usr/share/jenkins/ref/init.groovy.d/

COPY jenkins2.sh /usr/local/bin/jenkins2.sh
ENV CASC_JENKINS_CONFIG=$JENKINS_HOME/jenkins.yaml
#COPY --chown=jenkins:jenkins jenkins.yaml $JENKINS_HOME/jenkins.yaml
COPY --chown=jenkins:jenkins jenkins_home/ $JENKINS_HOME/

ENTRYPOINT ["tini", "--", "/usr/local/bin/jenkins2.sh"]
