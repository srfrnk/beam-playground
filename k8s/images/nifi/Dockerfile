FROM openjdk:8

RUN apt-get update -yy && apt-get upgrade -yy
RUN apt-get install -yy curl

RUN curl -o nifi.tar.gz -L https://www-us.apache.org/dist/nifi/1.9.2/nifi-1.9.2-bin.tar.gz
RUN tar -xzf nifi.tar.gz &&\
    rm nifi.tar.gz &&\
    mv nifi-* nifi

WORKDIR /nifi

CMD [ "bash","-c","./bin/nifi.sh run" ]
EXPOSE 8080

ARG VERSION
LABEL VERSION=${VERSION}
ENV IMAGE_VERSION=${VERSION}
