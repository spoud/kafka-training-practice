# Consumer Liveness and Rebalances


## How to run

* Start kafka (`docker compose up -d broker`)
* Create a topic with 9 partitions:  
  `kafka-topics --create --topic consumer-liveness --bootstrap-server localhost:9092 --partitions 9`
* Run the `Producer` to create sample messages
* Run the `Consumer` class
  * this will start a group with 3 consumer instances
  * initially each instance gets assigned 3 partitions of our topic
  * blocking calls to external systems are simulated
  * when the poll loop is blocked for too long, consumers are considered dead
  * a rebalance occurs as a result, partitions are re-distributed among the remaining consumer instances.



## Reviewing and fixing the configuration

* In the consumer logs, you should see a line like this after starting:   
  `You can inspect the consumer group 'test5797395381135542956' by running this command: 'watch kafka-consumer-groups --bootstrap-server localhost:9092  --describe --group test...'`   
  Execute the command to watch the partition assignment and offset positions
* Check the warning messages in the application log output
* review the consumer properties
* Try to change these settings to mitigate the _rebalance_ problem
* Are messages processed at-leat-once, at-most-once or exactly once?


## Consumer Group Protocol in Detail

Take a look at this post about the consumer group protocol for more details:
https://developer.confluent.io/learn-kafka/architecture/consumer-group-protocol/
