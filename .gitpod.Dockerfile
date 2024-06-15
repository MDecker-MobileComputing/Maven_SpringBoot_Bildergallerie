# Diese Datei wird nur für einen neuen Gitpod-Workspace wirksam

# Base-Image
FROM gitpod/workspace-full

# Nutzer, von dem die folgenden Befehle ausgeführt werden.
USER gitpod

# Abfrage verfügbaren Java-Versionen: sdkman list java
# Amazon-Java "Corretto"
RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 17.0.9-amzn  && \
    sdk default java 17.0.9-amzn "

# Programm "kafkacat" (gibt es nur unter Linux) installieren
# (die Beispielanwendung im Repo hat aber nichts mit Kafka zu tun)
RUN sudo apt-get install -y kafkacat 
			
# Wir können auch globale NPM-Pakete installieren            
RUN npm install -g http-server
