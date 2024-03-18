package io.spoud.training;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class EmployeeAvroConsumerTest {

    @Inject
    EmployeeAvroConsumer consumer;

    @Test
    void test() {
        Employee employee = Employee.newBuilder()
                .setStartDate(LocalDate.now().minusYears(1))
                .setFirstName("Apache")
                .setShortCode("APC")
                .setRole(JobRole.KAFKA_SPECIALIST)
                .build();
        consumer.checkSeniority(employee);
    }
}
