package org.example;

import org.junit.Before;
import org.junit.Test;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;
import junit.framework.TestCase;

public class ProducerAppTest extends TestCase {
  ConfluentKafkaContainer kafka;

  @Before
  public void setUp() throws Exception {
    kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"));
    kafka.start();
  }

  @Test
  public void testApp() {
    // TODO: create a producer and send some messages then verify that they are received
  }

}
