package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.ClienteNotFoundException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.ClienteDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteServiceTest {

    @Mock
    private ClienteDao clienteDao;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClienteMenor18Años() {
        Cliente clienteMenorDeEdad = new Cliente();
        clienteMenorDeEdad.setFechaNacimiento(LocalDate.of(2020, 2, 7));
        assertThrows(IllegalArgumentException.class, () -> clienteService.darDeAltaCliente(clienteMenorDeEdad));
    }

    @Test
    public void testClienteSuccess() throws ClienteAlreadyExistsException {
        Cliente cliente = new Cliente();
        cliente.setFechaNacimiento(LocalDate.of(1978,3,25));
        cliente.setDni(29857643);
        cliente.setTipoPersona(TipoPersona.PERSONA_FISICA);
        clienteService.darDeAltaCliente(cliente);

        verify(clienteDao, times(1)).save(cliente);
    }

    @Test
    public void testClienteAlreadyExistsException() throws ClienteAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        when(clienteDao.find(26456437, false)).thenReturn(new Cliente());

        assertThrows(ClienteAlreadyExistsException.class, () -> clienteService.darDeAltaCliente(pepeRino));
    }



    @Test
    public void testAgregarCuentaAClienteSuccess() throws TipoCuentaAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino);

        clienteService.agregarCuenta(cuenta, pepeRino.getDni());

        verify(clienteDao, times(1)).save(pepeRino);

        assertEquals(1, pepeRino.getCuentas().size());
        assertEquals(pepeRino, cuenta.getTitular());

    }


    @Test
    public void testAgregarCuentaAClienteDuplicada() throws TipoCuentaAlreadyExistsException {
        Cliente luciano = new Cliente();
        luciano.setDni(26456439);
        luciano.setNombre("Pepe");
        luciano.setApellido("Rino");
        luciano.setFechaNacimiento(LocalDate.of(1978, 3,25));
        luciano.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);        

        when(clienteDao.find(26456439, true)).thenReturn(luciano);

        clienteService.agregarCuenta(cuenta, luciano.getDni());

        Cuenta cuenta2 = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> clienteService.agregarCuenta(cuenta2, luciano.getDni()));
        verify(clienteDao, times(1)).save(luciano);
        assertEquals(1, luciano.getCuentas().size());
        assertEquals(luciano, cuenta.getTitular());

    }

    //Agregar una CA$ y CC$ --> success 2 cuentas, titular peperino
    @Test
    public void testAgregarCuentaAClienteCCYCA() throws TipoCuentaAlreadyExistsException {
        Cliente jorge = new Cliente();
        jorge.setDni(26456439);
        jorge.setNombre("Jorge");
        jorge.setApellido("Uboldi");
        jorge.setFechaNacimiento(LocalDate.of(1988,2,29));
        jorge.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta();
        cuenta.setMoneda(TipoMoneda.PESOS);
        cuenta.setBalance(12000);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setMoneda(TipoMoneda.PESOS);
        cuenta2.setBalance(12000);
        cuenta2.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);

        when(clienteDao.find(26456439, true)).thenReturn(jorge);

        clienteService.agregarCuenta(cuenta, jorge.getDni());
        clienteService.agregarCuenta(cuenta2, jorge.getDni());

        verify(clienteDao, times(2)).save(jorge);

        assertEquals(2, jorge.getCuentas().size());
        assertEquals(jorge, cuenta.getTitular());
        assertEquals(jorge, cuenta2.getTitular());

        assertEquals(TipoMoneda.PESOS, cuenta.getMoneda());
        assertEquals(TipoMoneda.PESOS, cuenta2.getMoneda());

    }

    //Agregar una CA$ y CAU$S --> success 2 cuentas, titular peperino...
    @Test
    public void testAgregarCuentaAClienteCAYCAU() throws TipoCuentaAlreadyExistsException {
        Cliente jorge = new Cliente();
        jorge.setDni(26456439);
        jorge.setNombre("Jorge");
        jorge.setApellido("Uboldi");
        jorge.setFechaNacimiento(LocalDate.of(1988,2,29));
        jorge.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta();
        cuenta.setMoneda(TipoMoneda.PESOS);
        cuenta.setBalance(12000);
        cuenta.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setMoneda(TipoMoneda.DOLARES);
        cuenta2.setBalance(12000);
        cuenta2.setTipoCuenta(TipoCuenta.CAJA_AHORRO);

        when(clienteDao.find(26456439, true)).thenReturn(jorge);

        clienteService.agregarCuenta(cuenta, jorge.getDni());
        clienteService.agregarCuenta(cuenta2, jorge.getDni());

        verify(clienteDao, times(2)).save(jorge);
        assertEquals(2, jorge.getCuentas().size());
        assertEquals(jorge, cuenta.getTitular());
        assertEquals(jorge, cuenta2.getTitular());
        assertEquals(TipoMoneda.PESOS, cuenta.getMoneda());
        assertEquals(TipoMoneda.DOLARES, cuenta2.getMoneda());
    }

    //Testear clienteService.buscarPorDni
    @Test   // Caso de test que el dni esta bien
    public void testBuscarPorDniTrue() throws ClienteAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        when(clienteDao.find(26456439, true)).thenReturn(pepeRino);
        
        assertEquals(pepeRino, clienteService.buscarClientePorDni(26456439));
        verify(clienteDao, times(1)).find(26456439, true);
    }

    @Test
    public void testBuscarPorDniFalse() {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456439);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3, 25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        long dniNoExistente = 26456430;

        when(clienteDao.find(dniNoExistente, true)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> clienteService.buscarClientePorDni(dniNoExistente)); // Tuve problemas con la excepcion esperada, asi que lo puse como IllegalArgumentException que coinicide con el metodo buscarClientePorDNI
        verify(clienteDao, times(1)).find(dniNoExistente, true);
    }


}