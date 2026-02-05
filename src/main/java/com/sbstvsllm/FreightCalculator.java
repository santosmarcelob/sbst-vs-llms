package com.sbstvsllm;

public class FreightCalculator {

    public double calculate(double weight, double distance, String region, boolean isFragile) {
        if (weight <= 0 || distance <= 0) {
            throw new IllegalArgumentException("Peso e distância devem ser positivos");
        }

        double baseRate;
        if (distance < 100) {
            baseRate = 10.0;
        } else if (distance < 500) {
            baseRate = 0.1 * distance;
        } else {
            baseRate = 0.08 * distance + 15.0;
        }

        double weightFactor;
        if (weight < 5) {
            weightFactor = 1.0;
        } else if (weight < 20) {
            weightFactor = 1.2;
        } else {
            weightFactor = 1.5;
        }

        double regionalMultiplier;
        switch (region.toUpperCase()) {
            case "NORTE":
                regionalMultiplier = 1.5;
                break;
            case "SUL":
                regionalMultiplier = 1.1;
                break;
            case "SUDESTE":
                regionalMultiplier = 1.0;
                break;
            default:
                regionalMultiplier = 1.3;
        }

        double total = (baseRate * weightFactor) * regionalMultiplier;

        if (isFragile) {
            total += 25.0;
            if (weight > 10) {
                total *= 1.1; // Taxa extra de manuseio para itens frágeis e pesados
            }
        }

        // Desconto para grandes distâncias e pesos leves
        if (distance > 1000 && weight < 2) {
            total *= 0.9;
        }

        return Math.round(total * 100.0) / 100.0;
    }
}