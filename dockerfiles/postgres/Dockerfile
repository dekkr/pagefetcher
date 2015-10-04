FROM ubuntu

MAINTAINER docker@dekkr.nl

# auto validate license
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections

# update repos
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886
RUN apt-get update

# install java
RUN apt-get install oracle-java8-installer -y
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# install supervisor
RUN apt-get update && apt-get install -y supervisor

RUN apt-get clean

# Get pagefetcher
RUN wget -P /root/ https://bintray.com/artifact/download/dekkr/downloads/pagefetcher/pagefetcher-assembly-0.4.0.jar

RUN mv /root/pagefetcher-assembly*.jar /root/pagefetcher-assembly.jar

COPY logback.xml /root/logback.xml

COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

COPY application.conf /root/application.conf

EXPOSE 8080

# Run all parts using a single supervisor command
CMD ["/usr/bin/supervisord","--configuration=/etc/supervisor/conf.d/supervisord.conf"]
