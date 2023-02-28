package io.spoud.training;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * A bean producing random temperature data every second.
 * The values are written to a Kafka topic (temperature-values).
 * Another topic contains the name of weather stations (weather-stations).
 * The Kafka configuration is specified in the application configuration.
 */
@ApplicationScoped
public class ValuesGenerator {

    private static final Logger LOG = Logger.getLogger(ValuesGenerator.class);

    private final Random random = new Random();

    private final List<WeatherStation> stations = List.of(
            new WeatherStation(1, "Hamburg", 13),
            new WeatherStation(2, "Snowdonia", 5),
            new WeatherStation(3, "Boston", 11),
            new WeatherStation(4, "Tokio", 16),
            new WeatherStation(5, "Cusco", 12),
            new WeatherStation(6, "Svalbard", -7),
            new WeatherStation(7, "Porthsmouth", 11),
            new WeatherStation(8, "Oslo", 7),
            new WeatherStation(9, "Marrakesh", 20));

    @Outgoing("temperature-values")
    public Multi<Record<Integer, TemperatureReading>> generate() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(500))
                .onOverflow().drop()
                .map(tick -> {
                    WeatherStation station = stations.get(random.nextInt(stations.size()));
                    double temperature = BigDecimal.valueOf(random.nextGaussian() * 15 + station.getAverageTemperature())
                            .setScale(1, RoundingMode.HALF_UP)
                            .doubleValue();

                    LOG.infov("station: {0}, temperature: {1}", station.getName(), temperature);
                    return Record.of(
                            station.getId(),
                            TemperatureReading.newBuilder().setStationId(station.getId()).setTemperature(temperature).build()
                    );
                });
    }

    @Outgoing("weather-stations")
    public Multi<Record<Integer, WeatherStation>> weatherStations() {
        return Multi.createFrom().items(stations.stream().map(s -> Record.of(s.getId(), s))
        );
    }

}
