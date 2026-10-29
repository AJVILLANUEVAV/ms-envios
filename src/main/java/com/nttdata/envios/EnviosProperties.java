package com.nttdata.envios;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Parametros de cotizacion. Deben llenarse con el bloque  envios:  del YAML.
 * En el YAML se escriben en kebab-case (tarifa-base); Spring los asocia
 * a los campos en camelCase (tarifaBase).
 */
@ConfigurationProperties(prefix = "envios")
public record EnviosProperties(
        String moneda,
        double tarifaBase,
        double costoPorKg,
        double recargoExpress,
        double envioGratisDesde,
        double pesoMaximoKg) {
}
