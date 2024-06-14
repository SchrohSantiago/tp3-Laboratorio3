package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaUnsupported;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CuentaService {

    @Autowired
    private CuentaDao cuentaDao; // Cambiado para ser inyectado

    @Autowired
    private ClienteService clienteService;

    //Generar casos de test para darDeAltaCuenta
    //    1 - cuenta existente
    //    2 - cuenta no soportada
    //    3 - cliente ya tiene cuenta de ese tipo
    //    4 - cuenta creada exitosamente
    public void darDeAltaCuenta(Cuenta cuenta, long dniTitular) throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException, TipoCuentaUnsupported {
        if(cuentaDao.find(cuenta.getNumeroCuenta()) != null) {
            throw new CuentaAlreadyExistsException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe.");
        }

        //Chequear cuentas soportadas por el banco CA$ CC$ CAU$S
        if (!tipoCuentaEstaSoportada(cuenta.getTipoCuenta())) {
            throw new TipoCuentaUnsupported("El tipo de cuenta no es soportada: " + cuenta.getTipoCuenta());
        }

        //Añadir la cuenta al cliente
        clienteService.agregarCuenta(cuenta, dniTitular);
        cuentaDao.save(cuenta);
        System.out.println("Cuenta creada exitosamente");
    }

    public Cuenta find(long id) {
        return cuentaDao.find(id);
    }

    public boolean tipoCuentaEstaSoportada(TipoCuenta tipoCuentaIngresada) {
        if (tipoCuentaIngresada == TipoCuenta.CAJA_AHORRO || tipoCuentaIngresada == TipoCuenta.CUENTA_CORRIENTE || tipoCuentaIngresada == TipoCuenta.CAJA_AHORRO_DOLARES) {
            return true;
        }
        return false;
    }
}


