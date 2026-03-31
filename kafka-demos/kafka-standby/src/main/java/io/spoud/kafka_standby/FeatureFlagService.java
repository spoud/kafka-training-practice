package io.spoud.kafka_standby;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.util.UnleashConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureFlagService {
    private final Unleash unleash;

    public FeatureFlagService(@Value("${unleash.url}") String unleashUrl,
                              @Value("${unleash.apiKey}") String unleashApiKey,
                              @Value("${unleash.appName}") String unleashAppName,
                              @Value("${unleash.instanceId}") String unleashInstanceId) {
        var unleashConfig = UnleashConfig.builder()
                .unleashAPI(unleashUrl)
                .apiKey(unleashApiKey)
                .instanceId(unleashInstanceId)
                .appName(unleashAppName)
                .synchronousFetchOnInitialisation(true)
                .fetchTogglesInterval(5L) // 5s
                .build();
        unleash = new DefaultUnleash(unleashConfig);
    }

    public boolean isStandbyModeEnabled() {
        return unleash.isEnabled("standby");
    }
}
