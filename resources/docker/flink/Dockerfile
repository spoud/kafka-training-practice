FROM flink:2.1.1-java17

ENV FLINK_VERSION=2.1.1
ENV KAFKA_VERSION=4.0.1
ENV KAFKA_FLINK_VERSION=4.0.1-2.0
ENV FLINK_FAKER_VERSION=0.5.3

RUN wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-kafka/${KAFKA_FLINK_VERSION}/flink-connector-kafka-${KAFKA_FLINK_VERSION}.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-kafka
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-avro-confluent-registry/${FLINK_VERSION}/flink-sql-avro-confluent-registry-${FLINK_VERSION}.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-avro-confluent-registry
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-protobuf/${FLINK_VERSION}/flink-protobuf-${FLINK_VERSION}.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-protobuf
    wget -P https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-protobuf/${FLINK_VERSION}/flink-sql-protobuf-${FLINK_VERSION}.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-protobuf
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/kafka/kafka-clients/${KAFKA_VERSION}/kafka-clients-${KAFKA_VERSION}.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/kafka/kafka-clients
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-json/${FLINK_VERSION}/flink-json-${FLINK_VERSION}.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-json
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-csv/${FLINK_VERSION}/flink-csv-${FLINK_VERSION}.jar; \
    # https://repo.maven.apache.org/maven2/org/apache/flink/flink-csv
    wget -P /opt/flink/lib/ https://github.com/knaufk/flink-faker/releases/download/v${FLINK_FAKER_VERSION}/flink-faker-${FLINK_FAKER_VERSION}.jar;
    # https://github.com/knaufk/flink-faker/releases/

RUN echo 'classloader.resolve-order: parent-first' >> /opt/flink/conf/flink-conf.yaml

RUN chown -R flink:flink /opt/flink/lib
