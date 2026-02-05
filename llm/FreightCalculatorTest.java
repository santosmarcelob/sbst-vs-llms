package com.sbstvsllm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class FreightCalculatorTest {

    private final FreightCalculator calculator = new FreightCalculator();

    @Test
    @DisplayName("Deve lançar exceção quando peso ou distância são inválidos")
    void shouldThrowExceptionForInvalidInputs() {
        assertAll("Validação de Entradas Negativas ou Zero",
                () -> {
                    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                            () -> calculator.calculate(0, 100, "SUL", false));
                    assertEquals("Peso e distância devem ser positivos", e.getMessage());
                },
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calculator.calculate(-5, 100, "SUL", false)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calculator.calculate(10, 0, "SUL", false)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calculator.calculate(10, -10, "SUL", false)));
    }

    @ParameterizedTest
    @DisplayName("Deve calcular tarifa base corretamente baseado na distância e região")
    @CsvSource({
            // Distância < 100 (Taxa 10.0)
            "1.0, 50.0, SUDESTE, false, 10.0", // Curta distância (Minima fixada)
            "1.0, 99.9, SUDESTE, false, 10.0", // Limite superior da taxa mínima

            // 100 <= Distância < 500 (Base * 0.1)
            "1.0, 100.0, SUDESTE, false, 10.0",
            "1.0, 200.0, SUDESTE, false, 20.0",
            "1.0, 499.9, SUDESTE, false, 49.99", // Limite superior

            // Distância >= 500 (Base * 0.08 + 15.0)
            "1.0, 500.0, SUDESTE, false, 55.0",
            "1.0, 1000.0, SUDESTE, false, 95.0",
    })
    void shouldCalculateBaseRateByDistance(double weight, double distance, String region, boolean isFragile,
            double expected) {
        assertEquals(expected, calculator.calculate(weight, distance, region, isFragile), 0.01);
    }

    @ParameterizedTest
    @DisplayName("Deve aplicar fator de peso corretamente")
    @CsvSource({
            // Peso < 5 (Fator 1.0)
            "4.9, 100, SUDESTE, false, 10.0",

            // 5 <= Peso < 20 (Fator 1.2)
            "5.0, 100, SUDESTE, false, 12.0",
            "19.9, 100, SUDESTE, false, 12.0",

            // Peso >= 20 (Fator 1.5)
            "20.0, 100, SUDESTE, false, 15.0",
            "50.0, 100, SUDESTE, false, 15.0"
    })
    void shouldApplyWeightFactors(double weight, double distance, String region, boolean isFragile, double expected) {
        assertEquals(expected, calculator.calculate(weight, distance, region, isFragile), 0.01);
    }

    @ParameterizedTest
    @DisplayName("Deve aplicar multiplicador regional corretamente")
    @CsvSource({
            "NORTE, 1.5",
            "norte, 1.5", // Case insensitive
            "SUL, 1.1",
            "SUDESTE, 1.0",
            "CENTRO-OESTE, 1.3", // Padrão
            "XYZ, 1.3" // Desconhecida (Default)
    })
    void shouldApplyRegionalMultipliers(String region, double multiplier) {
        double expected = 10.0 * multiplier;
        assertEquals(expected, calculator.calculate(1.0, 100.0, region, false), 0.01);
    }

    @Test
    @DisplayName("Deve aplicar sobretaxa de fragilidade corretamente")
    void shouldApplyFragilitySurcharge() {
        assertEquals(35.0, calculator.calculate(1.0, 100.0, "SUDESTE", true));
    }

    @Test
    @DisplayName("Deve aplicar taxa extra de manuseio para itens frágeis E pesados (> 10kg)")
    void shouldApplyHeavyFragileHandlingFee() {
        // Taxa extra de 10% para itens frágeis acima de 10kg
        assertEquals(40.7, calculator.calculate(15.0, 100.0, "SUDESTE", true), 0.01);
    }

    @Test
    @DisplayName("NÃO deve aplicar taxa extra de manuseio para itens frágeis no limite de peso (10kg)")
    void shouldNotApplyHeavyFragileFeeOnBoundary() {
        // Peso 10.0 não deve acionar a taxa extra (apenas > 10)
        assertEquals(37.0, calculator.calculate(10.0, 100.0, "SUDESTE", true));
    }

    @Test
    @DisplayName("Deve aplicar desconto para grandes distâncias (>1000) e pesos leves (<2)")
    void shouldApplyLongDistanceLightWeightDiscount() {
        // Desconto de 10% para dist > 1000 e peso < 2
        assertEquals(99.9, calculator.calculate(1.0, 1200.0, "SUDESTE", false), 0.01);
    }
}
