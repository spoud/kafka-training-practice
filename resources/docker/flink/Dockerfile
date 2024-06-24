FROM flink:1.19.0


RUN wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-kafka/3.2.0-1.19/flink-connector-kafka-3.2.0-1.19.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-kafka
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-avro-confluent-registry/1.19.1/flink-sql-avro-confluent-registry-1.19.1.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-avro-confluent-registry
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-protobuf/1.19.1/flink-protobuf-1.19.1.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-protobuf
    wget -P https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-protobuf/1.19.1/flink-sql-protobuf-1.19.1.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-protobuf
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/kafka/kafka-clients/3.7.0/kafka-clients-3.7.0.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/kafka/kafka-clients
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-json/1.19.1/flink-json-1.19.1.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-json
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-csv/1.19.1/flink-csv-1.19.1.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-csv
    wget -P /opt/flink/lib/ https://github.com/knaufk/flink-faker/releases/download/v0.5.3/flink-faker-0.5.3.jar;
    # https://github.com/knaufk/flink-faker/releases/

RUN echo 'classloader.resolve-order: parent-first' >> /opt/flink/conf/flink-conf.yaml

RUN chown -R flink:flink /opt/flink/lib