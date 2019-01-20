# ot-demo
Observability demo


##### This is a demo for Tracing with Open Tracing and Jaeger.

## Tech Stack
        Spring Boot version 2+ with Gradle
        MongoDB 
        Elasticsearch 
        Open Tracing
        Jaeger

### Steps to run

    #1 docker run \
         --rm \
         --name jaeger \
         -p6831:6831/udp \
         -p16686:16686 \
         -p14268:14268 \
         jaegertracing/all-in-one:1.8
    
    #2 Run OTDemo.java in Intelli J or Eclipse
    
    #3. curl -X POST \
          http://localhost:21000/emp/add \
          -H 'Content-Type: application/json' \
          -d '{
        	"employee_id": "123",
        	"employee_name": "Chandresh Pancholi",
        	"dept" : "Engineering",
        	"manager" : "Ivan"
        }'
    
    #3. Open http://localhost:16686
    
    #4. Select ot-demo from service in Jaeger UI
    
    

