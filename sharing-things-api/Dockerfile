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

ENV DB_HOST localhost
ENV DB_PORT 15432
ENV DB_NAME sharing_things
ENV DB_USER nonprod_user
ENV DB_PASSWORD unsecure_password
ENV OAUTH_URL http://localhost:8080/auth


CMD ["exec java -jar $VERTICLE_FILE -Doauth2.keycloak.auth-server-url=$OAUTH_URL -Ddatasource.host=$DB_HOST -Ddatasource.port=$DB_PORT -Ddatasource.database=$DB_NAME -Ddatasource.use=$DB_USER -Ddatasource.password=$DB_PASSWORD"]
