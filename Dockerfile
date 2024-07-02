FROM eclipse-temurin:21-jdk-jammy
LABEL maintainer="letto.at"
LABEL description="Demo-Plugin Uhr based on ubuntu"

ENV DEBIAN_FRONTEND=noninteractive

# Linux Grundeinrichtung
RUN apt-get -y update && \
    apt-get -y upgrade && \
    apt-get -y dist-upgrade && \
    apt-get -y autoremove && \
    ln -fs /usr/share/zoneinfo/Europe/Vienna /etc/localtime && \
    apt-get install -y apt-utils curl wget tzdata lsb-release locales && \
    dpkg-reconfigure --frontend noninteractive tzdata && \
    apt-get install -y nano less dos2unix && \
    apt-get install -y htop iputils-ping && \
    localedef -i en_US -f UTF-8 en_US.UTF-8

# Bereinige das Image
RUN apt-get clean && \
    apt-get autoclean  && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 5080 7080 8080

RUN mkdir /scripts -p
COPY target/pluginuhr-1.2.jar pluginuhr.jar
COPY scripts/*.sh     /scripts/
COPY src/main/resources/revision.txt revision.txt
RUN dos2unix /scripts/*.sh

RUN chmod 755 /scripts/*.sh

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 CMD bash /scripts/healthcheck.sh

ENTRYPOINT ["/scripts/start.sh"]