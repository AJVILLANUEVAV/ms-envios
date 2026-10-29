package com.nttdata.envios;

import com.nttdata.envios.model.CotizacionRequest;
import com.nttdata.envios.model.CotizacionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Calcula el costo de un envio con la configuracion activa. */
@Service
public class CotizadorService {

    private final EnviosProperties props;

    public CotizadorService(EnviosProperties props) {
        this.props = props;
    }

    public CotizacionResponse cotizar(CotizacionRequest req) {
        if (req.pesoKg() <= 0 || req.pesoKg() > props.pesoMaximoKg()) {
            throw new IllegalArgumentException("El peso debe ser mayor que 0 y no superar el máximo permitido.");
        }

        if (!req.express() && req.montoCompra() >= props.envioGratisDesde()) {
            return new CotizacionResponse(0.00, props.moneda(), true, false);
        }

        double costo = props.tarifaBase() + req.pesoKg() * props.costoPorKg();

        if (req.express()) {
            costo *= 1 + props.recargoExpress();
        }

        return new CotizacionResponse(redondear(costo), props.moneda(), false, req.express());
    }

    /** Redondeo a 2 decimales, HALF_UP. (Resuelto) */
    private static double redondear(double valor) {
        return BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
