# Read avro message form file

If you dump messages into a file using e.g. `kafka-avro-console-consumer` you can read them back using the right schema.

This is in no way a complete solution, but it should show whit what issues you have to deal with.

One issue is that you have to know the schema of the messages you are reading. This is not a problem if you are the one who wrote the messages, but if you are reading messages from a topic you don't know the schema of, you have to find it out first.

Within the message you will find the schema id, which you can use to get the schema from the schema registry. If you have access to it.

Otherwise, you can use a file with the schema if this was given along with the messages.

In any case you have to get rid of the magic byte and the schema id from the message before you can decode it.
