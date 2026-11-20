package io.spoud.training;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void unsupportedRoleThrows() {
        Employee employee = Employee.newBuilder()
                .setStartDate(LocalDate.now().minusYears(1))
                .setFirstName("Apache")
                .setShortCode("APC")
                .setRole(JobRole.ML_ENGINEER)
                .build();
        assertThrows(IllegalArgumentException.class, () -> consumer.checkSeniority(employee));
    }
}
