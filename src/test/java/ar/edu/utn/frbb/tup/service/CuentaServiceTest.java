package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.TipoCuenta;
import ar.edu.utn.frbb.tup.model.TipoMoneda;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaUnsupported;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.management.RuntimeErrorException;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteDao clienteDao;

    @InjectMocks
    private CuentaService cuentaService;

    @InjectMocks 
    private ClienteService clienteService;

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

    // @Test
    // public void testClienteTieneCuentaDeEseTipo() throws TipoCuentaAlreadyExistsException {
    //     // Creamos una cuenta existente del mismo tipo para el cliente
    //     Cuenta cuentaExistente = new Cuenta();
    //     cuentaExistente.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
    //     cuentaExistente.setMoneda(TipoMoneda.PESOS);
    //     cuentaExistente.setNumeroCuenta(123);
        
    //     Cliente cliente = new Cliente();
    //     cliente.setDni(44882709);
    //     cuentaExistente.setTitular(cliente);

    //     // Simulamos que el cliente ya tiene una cuenta de ese tipo
    //     when(cuentaDao.find(cuentaExistente.getNumeroCuenta()))
    //             .thenReturn(cuentaExistente);

    //     // Creamos una cuenta nueva con el mismo tipo para el cliente
    //     Cuenta cuentaNueva = new Cuenta();
    //     cuentaNueva.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
    //     cuentaNueva.setMoneda(TipoMoneda.PESOS);
    //     cuentaNueva.setNumeroCuenta(456);

    //     // Al intentar dar de alta la cuenta nueva, debería lanzar TipoCuentaAlreadyExistsException
    //     assertThrows(TipoCuentaAlreadyExistsException.class, () -> {
    //         clienteService.agregarCuenta(cuentaNueva, 44882709);
    //     });

    //     // Verificamos que se haya llamado al método agregarCuenta en ClienteService
    //     verify(clienteService, times(1)).agregarCuenta(cuentaNueva, 44882709);
    // }
    

    // @Test
    // public void testCreateCuentaExitosa() throws CuentaAlreadyExistsException, TipoCuentaUnsupported, TipoCuentaAlreadyExistsException {

    //     Cliente cliente = new Cliente();
    //     cliente.setDni(44882709);

        
    //     when(clienteDao.find(cliente.getDni(), true )).thenReturn(cliente);
    //     // Llamar al método darDeAltaCuenta de cuentaService

    //     Cuenta cuenta = new Cuenta();
    //     cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
    //     cuenta.setMoneda(TipoMoneda.PESOS);
    //     cuenta.setNumeroCuenta(1234);
        
    //     when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(cuenta);
    //     clienteService.agregarCuenta(cuenta, 44882709);
    
    //     // Verificar que la cuentaDao.save haya sido invocada una vez
        
    // verify(cuentaDao, times(1)).save(cuenta);
    // }
}
    

// }
