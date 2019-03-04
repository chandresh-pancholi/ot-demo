package com.ot.demo.bean;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.ot.demo.config.AppConfig;
import io.opentracing.Tracer;
import io.opentracing.contrib.elasticsearch6.TracingPreBuiltTransportClient;
import io.opentracing.contrib.mongo.TracingMongoClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Singleton;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Properties;

@Component
@Slf4j
public class OTDemoBeans {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private Tracer tracer;

    @Bean
    @Primary
    @Singleton
    public MongoClient mongoClient() {
        MongoCredential credential = MongoCredential.createCredential(appConfig.getMongoDBUsername(), appConfig.getMongoDBName(), appConfig.getMongoDBPassword().toCharArray());


        MongoClientOptions clientOptions = new MongoClientOptions.Builder()
                .minConnectionsPerHost(appConfig.getMongoMinConnectionPerHost())
                .connectionsPerHost(appConfig.getMongMaxConnectionPerHost())
                .threadsAllowedToBlockForConnectionMultiplier(10)
                .build();

        MongoClient mongoClient = new TracingMongoClient(tracer, new ServerAddress(appConfig.getMongoDBHost() , appConfig.getMongoDBPort()), clientOptions );

        return mongoClient;

    }

    @Bean
    @Autowired
    @Singleton
    public MongoDatabase mongoDBConnection(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(appConfig.getMongoDBName());

        return database;
    }

    @Bean
    public KafkaProducer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getKafkaBrokers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, appConfig.getKafkaProducerClientId());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());

        return new KafkaProducer<>(props);
    }


    @Bean
    @Singleton
    public TransportClient elasticsearchClient() throws Exception {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch_Chandresh").build();

        TransportClient transportClient = new TracingPreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        if (!isIndexExist(transportClient, "ot-demo")) {
            transportClient.admin().indices()
                    .prepareCreate("ot-demo")
                    .setSettings(
                            Settings.builder()
                                    .put("index.number_of_shards", 1)
                                    .put("index.number_of_replicas", 1)
                    )
                    .get();
        }

        return transportClient;
    }

    private boolean isIndexExist(TransportClient transportClient, String index) {
        IndexMetaData indexMetaData = transportClient.admin().cluster()
                .state(Requests.clusterStateRequest())
                .actionGet()
                .getState()
                .getMetaData()
                .index(index);

        return (indexMetaData != null);
    }
}
