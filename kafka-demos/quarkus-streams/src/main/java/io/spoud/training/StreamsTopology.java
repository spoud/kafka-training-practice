package io.spoud.training;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientConfig;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClientFactory;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.smallrye.common.annotation.Identifier;
import jakarta.inject.Inject;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class StreamsTopology {

    static final String WEATHER_STATIONS_TOPIC = "weather-stations";
    static final String TEMPERATURE_VALUES_TOPIC = "temperature-values";
    static final String TEMPERATURES_AGGREGATED_TOPIC = "temperatures-aggregated";

    private static final Logger LOG = Logger.getLogger(StreamsTopology.class);


    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        GlobalKTable<Integer, WeatherStation> stations = builder.globalTable(WEATHER_STATIONS_TOPIC);
        KStream<Integer, TemperatureReading> tempReadings = builder.stream(TEMPERATURE_VALUES_TOPIC);

        Serde<AggregationResult> valueSerde = new SpecificAvroSerde<>();
        valueSerde.configure(Map.of("schema.registry.url", "http://localhost:8081"), false);

        tempReadings.join(stations,
                        // keyValueMapper
                        (stationId1, temperature) -> stationId1,
                        // joiner
                        (temperatureReading, station) -> TemperatureReading.newBuilder(temperatureReading).setStationName(station.getName()).build())
                .groupByKey()
                // if you want to use a window enable the following line and also switch the serde in the to()
                //.windowedBy(TimeWindows.of(Duration.ofSeconds(10))) // aggregate over a time window of 10 seconds
                .aggregate(
                        () -> AggregationResult.newBuilder().setStationId(-1).setStationName("").build(),
                        (stationId, value, aggregation) -> {
                            aggregation.setStationName(value.getStationName());
                            aggregation.setStationId(value.getStationId());
                            aggregation.setCount(aggregation.getCount() + 1);
                            aggregation.setSum(aggregation.getSum() + value.getTemperature());
                            aggregation.setAvg(aggregation.getSum() / aggregation.getCount());
                            aggregation.setMin(Math.min(value.getTemperature(), aggregation.getMin()));
                            aggregation.setMax(Math.max(value.getTemperature(), aggregation.getMax()));
                            return aggregation;
                        })
                .toStream()
                .peek((stationId, aggregation) -> LOG.infov("station: {0}, aggregation: {1}", stationId, aggregation))
                .to(TEMPERATURES_AGGREGATED_TOPIC); // without windowing
                //.to(TEMPERATURES_AGGREGATED_TOPIC, Produced.with(WindowedSerdes.timeWindowedSerdeFrom(Integer.class, Duration.ofSeconds(10).toMillis()), valueSerde));
        return builder.build();
    }


}
