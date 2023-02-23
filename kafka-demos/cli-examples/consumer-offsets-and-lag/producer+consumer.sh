# produce 1000 records
echo "" > numbers.txt
for x in {1..1000}; do echo $x >> numbers.txt; done
kafka-console-producer --bootstrap-server localhost:9092 --topic numbers < numbers.txt

# create a console consumer with a group.id which reads only 10 of these messages
kafka-console-consumer --bootstrap-server localhost:9092 --topic numbers --max-messages 10 --group slow-consumer --from-beginning

# display the offset position for the partition and the currently committed offset of the group
# the difference between latest offset and group offset = lag
# increasing lag could mean that our consumer is too slow or stuck/stopped -> important for monitoring
kafka-consumer-groups --bootstrap-server localhost:9092  --group slow-consumer --describe

# we can manually change the offset for this group
kafka-consumer-groups --bootstrap-server localhost:9092  --group slow-consumer --reset-offsets --to-offset 999 --execute --topic numbers

# when starting the console consumer with the same group.id, we should continue reading from offset 999
kafka-console-consumer --bootstrap-server localhost:9092 --topic numbers --group slow-consumer --property "print.offset=true" --max-messages 1
