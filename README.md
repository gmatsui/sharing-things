# sharing-things
Common stuff app or Sharing Things App (initiative)

The aim of the application is share those things that usually one person use few times and the rest of the time the thing is unused so we can share the ownership of the thing to another persons and share any cost that came for mantain, repair or even update the thing.
For example:
You and your friends can bought an action camera  and each members of the groups could use the camera in different time (one could use in the own vacation, another in some festival)

Also you could have different groups for different things.

At the end you would enjoy having access to multiple things (action camera, tent) spending less money.

Definitions:
    Contributor.
    Contributor groups.
    Asset.
    Contact

# Requirement
FR 
1. Given an Contributor when access the application then he can see the list of Assets from the Contributor Groups that he belong.
2. Given an Contributor and a new asset and a groups of contacts to shared the object and a group name to identify the Contributor groups when the Contributor request the creation of the contributor groups then the Contributor has a new item and belong to a Contributor group
3. Given an Contributor (A) that require an shared asset and other Contributor (B) from the same Contributor Groups has the Asset when the Contributor (A) request the Asset then the other Contributor (B) receive a notification of the request.
4. Given an Contributor and given a Contributor Groups and given a Contact when the Contributor include the Contact then the Contact belong in Contributor and can see the asset in the group. 
5. Given an Contributor and given a new object to be shared when the Contributor add the new Asset then the other Contributor in the Contributor group can see the new Asset.
6. Given an Contributor that require an Asset in the future when reserved the Asset for an specific range date then the Asset is reserved for that period of time.
7. Given an Contributor that like to reserve an Asset when check the Asset calendar then should see the future reservation of the Asset.
...


NFR

1. Given 50 concurrent users when the users request every 5 seconds then the application should be stable.
2. Given an anonymous user when the user request the secured endpoint then should be an error messages.
3. Given a Dev Ops when search logs information should have all the log centralized in one point.
4. Given a Dev Ops when an application is crashed should be automatically notify in order to take actions. 


#Proposed design

![Alt text](doc/sharing-things.jpg?raw=true "Title")


#Run locally

Starting the environment
```
> cd sharing-things-api
> docker-compose -f docker-compose-environment up
> ./redeploy.sh
```

Configuring Keycloak
* Access to http://localhost:8080/auth/
* Admin console (user: admin/password: Test123456)
* import the file /sharing-things-api/realm-export.json

#Run in CI
TODO
