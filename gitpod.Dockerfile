# Base-Image
FROM gitpod/workspace-full

# Nutzer, von dem die folgenden Befehle ausgeführt werden.
USER gitpod

# Abfrage verfügbaren Java-Versionen: sdkman list java
# Amazon-Java "Corretto"
RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 21.0.2-amzn  && \
    sdk default java 21.0.2-amzn "

# Programm "kafkacat" (gibt es nur unter Linux) installieren
# (die Beispielanwendung im Repo hat aber nichts mit Kafka zu tun)
RUN sudo apt-get install -y kafkacat 
