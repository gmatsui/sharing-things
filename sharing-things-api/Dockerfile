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

ARG DB_HOST=localhost
ARG DB_PORT=15432
ARG DB_NAME=sharing_things
ARG DB_USER=nonprod_user
ARG DB_PASSWORD=unsecure_password
ARG OAUTH_URL=http://localhost:8080/auth
ARG GERARD_ARG

ENV DB_HOST=$DB_HOST
ENV DB_PORT=$DB_PORT
ENV DB_NAME=$DB_NAME
ENV DB_USER=$DB_USER
ENV DB_PASSWORD=$DB_PASSWORD
ENV OAUTH_URL=$OAUTH_URL
ENV GERARD_ENV=$GERARD_ARG


CMD ["exec java -jar $VERTICLE_FILE -Doauth2.keycloak.auth-server-url=$OAUTH_URL -Ddatasource.host=$DB_HOST -Ddatasource.port=$DB_PORT -Ddatasource.database=$DB_NAME -Ddatasource.use=$DB_USER -Ddatasource.password=$DB_PASSWORD -Dtest=$GERARD_ENV"]
