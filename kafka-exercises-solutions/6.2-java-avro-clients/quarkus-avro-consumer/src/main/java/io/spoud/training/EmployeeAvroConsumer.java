package io.spoud.training;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.Year;

@ApplicationScoped
public class EmployeeAvroConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeAvroConsumer.class);

    @Incoming("employees-avro")
    public void checkSeniority(Employee employee) {
        if (hasAnniversary(employee)) {
            int seniorityYears = Year.now().getValue() - employee.getStartDate().getYear();
            LOGGER.info("Employee {} is celebrating his/her {}. anniversary today!", employee, seniorityYears);
        }
    }

    private static boolean hasAnniversary(Employee employee) {
        return employee.getStartDate().withYear(Year.now().getValue()).isEqual(LocalDate.now());
    }
}
