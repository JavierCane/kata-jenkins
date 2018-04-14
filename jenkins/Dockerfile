FROM jenkins/jenkins:lts

LABEL maintainer="Laurentiu Marcut"

USER root

RUN apt-get update -y && \
    apt-get install -y \
	    zip \
    	jq \
    	gettext-base

RUN wget https://download.docker.com/linux/static/stable/x86_64/docker-17.09.0-ce.tgz && \
	tar xzvf docker-17.09.0-ce.tgz && \
	cp docker/* /usr/bin/

RUN curl -L https://github.com/docker/compose/releases/download/1.17.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose && \
    rm /bin/sh && ln -s /bin/bash /bin/sh