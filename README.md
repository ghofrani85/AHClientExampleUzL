# Improved Arrowhead Framework Skeleton
This is an improved version of the current existing Arrowhead Framework Skeleton. It should help to easily communicate
 with the core services. Furthermore, it should standardize the source code for the communication and make it clearer. 
 
## What changed?
I added a new module to the existing Skeleton which include some classes. They support the communication with the
 core services. All necessary variables can you predefine in the application.properties from the module. During the
  runtime you can change all variables with setters.
 
#### Classes EventHandler, Orchestrator, ServiceRegistry
These classes provide an easier access to the core modules of Arrowhead Framework. Control parameters for the
 communication can predefine in the application.properties or overwrite with suitable methods. In addition to the
  current skeleton the Service Registry can register service which are not part of the running system. If the
   SystemAddress (server.address) is 0.0.0.0 then the ServiceRegistry will register all available IP-Addresses which
    are not link local and up of the current system.

#### Class Constants
Use this class to share constants or generally applicable variables with all modules.

#### Class Miscellaneous
In this class you can find methods which you can generally use. Among other things, it has methods for the output of
 objects in the Pretty JSON and the coding of strings with the help of a hash algorithm.

## How to use
Use the injection interface ``@Autowired`` to bind the variable to the core service class.

#### Example
```Java
public class MyClass {
    @Autowired
    private ServiceRegistry serviceRegistry;

    public void registerMyService() {
        Map<String, String> metadata = new HashMap<>();

        //Checking the availability of necessary core systems
        checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);

        if (sslEnabled && tokenSecurityFilterEnabled) {
            checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			
    
            //Initialize Arrowhead Context
            arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
        
            setTokenSecurityFilter();
        }


        metadata.put("country", "germany");
        metadata.put("location", "Dresden");
        metadata.put("organisation", "HTW Dresden");
        metadata.put("department", "production");
        metadata.put("system", "MES");
        metadata.put("replica", "3");

        serviceRegistry.registerService("mes-data", "/mes", HttpMethod.GET, metadata);
    }
}
```

## Configuration properties for Arrowhead Client
Here you can see the configuration properties which you can use to customize the core service instances. The
 following scopes exist:
 * arrowhead.client.consumer: Consumer
 * arrowhead.client.systems: all
 * arrowhead.client.orchestration: services which need the orchestrator
 * arrowhead.client.subscriber: subscriber
 * arrowhead.client.subscriber: publisher
 * arrowhead.client.register.systems: systems/services which should register independent from running client (you can
  specify multiple systems and services in application.properties)


| Property | Description | Data Type | Default value* | Required |
| --- | --- | --- | --- | --- |
| arrowhead.client.system.name | System name | String | | yes |
| arrowhead.client.system.interface.secure | secure interface name | String | | yes |
| arrowhead.client.system.interface.insecure | insecure interface name | String | | yes |
| arrowhead.client.orchestration.min-version | minimum version of requested services | Integer | 0 | no |
| arrowhead.client.orchestration.max-version | maximum version of a requested service | Integer | 100 | no |
| arrowhead.client.orchestration.enable-version | enable search with version | Boolean | false | no |
| arrowhead.client.orchestration.max-retry | maximum of trials after orchestration request canceled | Integer | 0 | no |
| arrowhead.client.orchestration.retry-wait | time in seconds to wait after orchestration fails to try again | Integer | 1 | no |
| arrowhead.client.orchestration.matchmaking | Orchestration Flag; Interface name must match | Boolean | false | no |
| arrowhead.client.orchestration.override-store | Orchestration Flag; Override Orchestration Store | Boolean | true | no |
| arrowhead.client.orchestration.metadata-search | Orchestration Flag; search services with metadata | Boolean | true | no |
| arrowhead.client.orchestration.enable-inter-cloud | Orchestration Flag; search in other Arrowhead Clouds | Boolean | false | no |
| arrowhead.client.orchestration.enable-qos | Orchestration Flag; activate QoS | Boolean | false | no |
| arrowhead.client.orchestration.external-service-request | Orchestration Flag; request external services | Boolean | false | no |
| arrowhead.client.orchestration.only-preferred | Orchestration Flag; return only preferred services | Boolean | false | no |
| arrowhead.client.orchestration.trigger-inter-cloud | Orchestration Flag; trigger other clouds for requests | Boolean | false | no |
| arrowhead.client.orchestration.ping-providers | Orchestration Flag; ping providers and only return active services | Boolean | true | no |
| arrowhead.client.register.systems.{SYSTEM}.address | external systems IP or hostname | String | | yes |
| arrowhead.client.register.systems.{SYSTEM}.port | external systems port | int | | yes |
| arrowhead.client.register.systems.{SYSTEM}.tokensecurityfilter-enabled | is tokensecurityfilter enabled on external system | boolean | false | no |
| arrowhead.client.register.systems.{SYSTEM}.ssl-enabled | is ssl enabled | boolean | false | no |
| arrowhead.client.register.systems.{SYSTEM}.services.{SERVICE}.interface-insecure | insecure interface name of service | String | HTTP-INSECURE-JSON | no |
| arrowhead.client.register.systems.{SYSTEM}.services.{SERVICE}.interface-secure | secure interface name of service | String | HTTP-SECURE-JSON | no |
| arrowhead.client.register.systems.{SYSTEM}.services.{SERVICE}.service-uri | uri of service | String | | yes |
| arrowhead.client.register.systems.{SYSTEM}.services.{SERVICE}.version | version of service | int | 1 | no |
| arrowhead.client.register.systems.{SYSTEM}.services.{SERVICE}.http-method | http method of the service | String | GET | no |
| arrowhead.client.register.systems.{SYSTEM}.services.{SERVICE}.metadata.x=y | metadata of service (x = Key; y = Value) | Map | null | no |
| arrowhead.client.subscriber.base-notification-uri | root URI for RestController; Events will published after subscription to this root (base-notification-uri/eventname) | String | NULL | no |
| arrowhead.client.subscriber.events.* | events to subscribe (arrowhead.client.subscriber.events.x=y); x stands for an event (case-insensitive); y is the uri of the restcontroller mapping method (case-insensitive) | String | | yes |
| arrowhead.client.publisher.events.* | events to publish (arrowhead.client.publisher.events.x=y); x is the key identifier in map; y is the name of an event | String | | yes |
| server.address | IP/Hostname of current system (0.0.0.0 will replaced with all available IP´s) | String | | yes |
| server.port | Port of the service on the system | Integer | | yes |

\* empty means necessary\
{SYSTEM} - insert your system name (corresponds client_system_name/arrowhead.client.system.name)\
{SERVICE} - service definition of the external service (corresponds service-definition)

## Sponsors
![Sachsen](assets/sponsors/Saxony.gif "Sachsen")
![BMBF](assets/sponsors/BMBF.jpg "BMBF")\
Diese Maßnahme wird mitfinanziert mit Steuermitteln auf Grundlage des vom Sächsischen Landtag beschlossenen Haushaltes.

![Ecsel JU](assets/sponsors/Ecsel_JU.jpg "Ecsel JU")
![Europa](assets/sponsors/Europe.png "Europa")\
This project has received funding from the ECSEL Joint Undertaking (JU) under grant agreement No 826452. The JU receives support from the European Union’s Horizon 2020 research and innovation programme and Sweden, Austria, Spain, Poland, Germany, Italy, Czech Republic, Netherlands, Belgium, Latvia, Romania, France, Hungary, Portugal, Finland, Turkey, Norway, Switzerland.

