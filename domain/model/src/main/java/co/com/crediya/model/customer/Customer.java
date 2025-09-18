package co.com.crediya.model.customer;

import java.math.BigDecimal;
import java.util.UUID;

public record Customer(
    UUID id,
    String firstName,
    String lastName,
    String email,
    BigDecimal baseSalary
) { }
