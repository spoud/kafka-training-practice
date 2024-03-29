package io.spoud.training;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windows;
import org.jboss.logging.Logger;

import java.time.Duration;

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

        tempReadings.join(stations,
                        // keyValueMapper
                        (stationId1, temperature) -> stationId1,
                        // joiner
                        (temperatureReading, station) -> TemperatureReading.newBuilder(temperatureReading).setStationName(station.getName()).build())
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10))) // aggregate over a time window of 10 seconds
                .aggregate(() -> AggregationResult.newBuilder().setStationId(-1).setStationName("").build(),
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
                .to(TEMPERATURES_AGGREGATED_TOPIC);

        return builder.build();
    }


}
