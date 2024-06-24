# Flink Demo

## Prepare Environment

1. start the flink cluster and its dependencies

```bash
      docker-compose up -d broker connect schema-registry ksql-datagen
      docker-compose -f docker-compose-flink.yml up -d
```

2. open the flink dashboard

http://localhost:8090

3. start the ksql-datagen to produce data

```bash
      docker-compose exec ksql-datagen ksql-datagen quickstart=users format=json topic=users maxInterval=1000 msgRate=4 iterations=100 bootstrap-server=broker:29092
      
      docker-compose exec ksql-datagen ksql-datagen quickstart=pageviews format=json topic=pageviews maxInterval=1000 msgRate=4 iterations=1000000 bootstrap-server=broker:29092
```

4. check the data in the topics

```bash
      docker-compose exec broker kafka-console-consumer --bootstrap-server broker:29092 --topic users --from-beginning --property print.key=true --property key.separator=,
      
      docker-compose exec broker kafka-console-consumer --bootstrap-server broker:29092 --topic pageviews --from-beginning --property print.key=true --property key.separator=,
```

5. enter flink sql container

```bash
      docker-compose exec sql-client bin/sql-client.sh
```

6. run the flink sql queries

```sql
-- specify the idle timeout in ms otherwise watermarks are not emitted from users table
SET 'table.exec.source.idle-timeout' = '10000';
    
CREATE TABLE users
(
    userid       STRING PRIMARY KEY NOT ENFORCED,
    registertime BIGINT,
    registertime_ts AS TO_TIMESTAMP_LTZ(registertime, 3),
    regionid     STRING,
    gender       STRING,
    updated_at   TIMESTAMP_LTZ(3) METADATA FROM 'timestamp',
    WATERMARK FOR updated_at AS updated_at
) WITH (
      'connector' = 'upsert-kafka',
      'topic' = 'users',
      'properties.bootstrap.servers' = 'broker:29092',
      'properties.group.id' = 'testGroup',
      'key.format' = 'raw',
      'value.format' = 'json'
      );

CREATE TABLE pageviews
(
    userid   STRING,
    pageid   STRING,
    viewtime BIGINT,
    viewtime_ts AS TO_TIMESTAMP_LTZ(viewtime, 3),
    proc_time AS PROCTIME(),
    WATERMARK FOR viewtime_ts AS viewtime_ts - INTERVAL '5' SECOND
) WITH (
      'connector' = 'kafka',
      'scan.startup.mode' = 'earliest-offset',
      'topic' = 'pageviews',
      'properties.bootstrap.servers' = 'broker:29092',
      'properties.group.id' = 'testGroup',
      'format' = 'json'
      );

CREATE TABLE pageviews_per_region
(
    regionid     STRING,
    view_count   BIGINT,
    window_start TIMESTAMP(3),
    window_end   TIMESTAMP(3),
    WATERMARK FOR window_end AS window_end - INTERVAL '5' SECOND,
    PRIMARY KEY (regionid, window_start, window_end) NOT ENFORCED

) WITH (
      'connector' = 'upsert-kafka',
      'topic' = 'pageviews_per_region',
      'properties.bootstrap.servers' = 'broker:29092',
      'properties.group.id' = 'testGroup',
      'key.format' = 'json',
      'value.format' = 'json'
      );

INSERT INTO pageviews_per_region
    SELECT IF(users.regionid IS NULL, 'unknown', users.regionid)    as regionid,
           count(*)                                                 as view_count,
           TUMBLE_START(pageviews.viewtime_ts, INTERVAL '1' MINUTE) as window_start,
           TUMBLE_END(pageviews.viewtime_ts, INTERVAL '1' MINUTE)   as window_end
    FROM (pageviews LEFT JOIN users 
        FOR SYSTEM_TIME AS OF pageviews.viewtime_ts 
        ON pageviews.userid = users.userid)
    GROUP BY users.regionid, TUMBLE(pageviews.viewtime_ts, INTERVAL '1' MINUTE);

```
