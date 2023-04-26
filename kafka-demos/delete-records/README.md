# Delete messages up to some offset

```bash
cat <<EOF > delete.json
{
  "partitions": [
    {
      "topic": "topic-3p",
      "partition": 1,
      "offset": 3
    }
  ],
  "version": 1
}
EOF
```

```bash
kafka-delete-records --bootstrap-server localhost:9092 --offset-json-file delete.json
```

This will delete all the messages up to offset 3 (included).

If you want to delete all messages up to the latest offset, you can use -1 as the offset.
