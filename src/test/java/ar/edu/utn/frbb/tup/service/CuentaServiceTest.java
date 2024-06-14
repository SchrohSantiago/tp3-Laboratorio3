package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.TipoPersona;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaUnsupported;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;

import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCuentaExiste() throws CuentaAlreadyExistsException {
        Cuenta cuenta = new Cuenta();
        Cliente cliente = new Cliente();

        cliente.setDni(44882709);

        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);
        cuenta.setNumeroCuenta(123);
        cuenta.setTitular(cliente);

        
        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(new Cuenta());

        assertThrows(CuentaAlreadyExistsException.class, () -> {
            cuentaService.darDeAltaCuenta(cuenta, 44882709);
        });
    }


    @Test
    public void testCuentaNoSoportada() throws TipoCuentaAlreadyExistsException {
    
        Cuenta cuentaNoSoportada = new Cuenta();
        cuentaNoSoportada.setTipoCuenta(TipoCuenta.CUENTA_NO_SOPORTADA);
        cuentaNoSoportada.setMoneda(TipoMoneda.PESOS);
        cuentaNoSoportada.setNumeroCuenta(123123);  

        when(cuentaDao.find(cuentaNoSoportada.getNumeroCuenta())).thenReturn(null);  

        assertThrows(TipoCuentaUnsupported.class, () -> {
            cuentaService.darDeAltaCuenta(cuentaNoSoportada, 44882709);
        });
        
    }

       @Test
        public void TipoCuentaIgual() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, TipoCuentaUnsupported {
        Cliente cliente = new Cliente();
        Cuenta cuenta = new Cuenta();
        Cuenta cuenta2 = new Cuenta();

        cliente.setDni(44882709);
        cliente.setNombre("Santiago");

        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);

        cuenta2.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta2.setMoneda(TipoMoneda.PESOS);
        

        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null); // no existe

        cuentaService.darDeAltaCuenta(cuenta, cliente.getDni());

        doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta2, cliente.getDni());

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta2, cliente.getDni()));

        verify(clienteService, times(1)).agregarCuenta(cuenta2, cliente.getDni());
        verify(cuentaDao, times(1)).save(cuenta);
        
    }

    @Test
    public void testCuentaCreadaConExito() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, TipoCuentaUnsupported {
        Cliente cliente = new Cliente();
        cliente.setDni(44882709);
        cliente.setNombre("Santiago");
        
        Cuenta cuenta = new Cuenta();
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);
        cuenta.setNumeroCuenta(123123);

        cuentaService.darDeAltaCuenta(cuenta, cliente.getDni());

        verify(clienteService, times(1)).agregarCuenta(cuenta, cliente.getDni());
        verify(cuentaDao, times(1)).save(cuenta);
    }
}
    

// }
