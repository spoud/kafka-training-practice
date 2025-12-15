# Play with record name strategy

```bash

docker compose -f docker compose.yml -f docker compose-akhq.yml up -d schema-registry akhq broker

curl localhost:8081/subjects

# Produce some messages with the RecordNameStrategy: run the producer

# check subjects again
curl localhost:8081/subjects


docker compose -f docker compose.yml -f docker compose-akhq.yml down schema-registry akhq broker
```

