FROM java:8-jre

ENV VERTICLE_FILE sharing-things-api-1.0-SNAPSHOT-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8090

# Copy your fat jar to the container
COPY target/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]

ARG DB_HOST
ARG OAUTH_HOST

CMD ["exec java -jar $VERTICLE_FILE -Doauth2.keycloak.auth-server-url=http://$OAUTH_HOST:8080/auth -Ddatasource.host=$DB_HOST"]
