package com.ot.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.ot.demo.model.Employee;
import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.TracingKafkaProducer;
import io.opentracing.contrib.spring.web.client.TracingRestTemplateInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping(value = "/emp")
@Slf4j
public class EmployeeController {

    private MongoCollection<Document> collection;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private Tracer tracer;

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    public EmployeeController(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection("employee");
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Employee AddEmployee(@RequestBody Employee employee) throws Exception {
        Document document = Document.parse(objectMapper.writeValueAsString(employee) );

        collection.insertOne(document);

        System.out.println(employee.getEmployeeName());

        IndexResponse indexResponse = esClient
                .prepareIndex("ot-demo", "ot-demo-doc", employee.getEmployeeId())
                .setSource(objectMapper.writeValueAsString(employee), XContentType.JSON)
                .get();

        System.out.println(indexResponse.status().getStatus());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new TracingRestTemplateInterceptor(tracer)));
        String fooResourceUrl
                = "http://localhost:22000/salary/123";
        ResponseEntity<String> response
                = restTemplate.getForEntity(fooResourceUrl, String.class);
        TracingKafkaProducer<String, String> tracingProducer = new TracingKafkaProducer<>(kafkaProducer,
                tracer);

        ProducerRecord<String, String> record = new ProducerRecord<>("ot-demo-test-topic", "Hello");

        tracingProducer.send(record);
        log.info(response.toString());



        return employee;
    }
}
