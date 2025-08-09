package es.cic.curso25.proy015.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.cic.curso25.proy015.exception.maxVehiculosException;
import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.TipoVehiculo;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.repository.MultaRepository;
import es.cic.curso25.proy015.repository.PlazaRepository;
import es.cic.curso25.proy015.repository.VehiculoRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GarajeServiceTest {
    @Autowired
    GarajeService garajeService;

    @Autowired
    PlazaRepository plazaRepository;

    @Autowired
    VehiculoRepository vehiculoRepository;

    @Autowired
    MultaRepository multaRepository;

    Vehiculo vehiculo6;
    Vehiculo vehiculo7;

    Plaza plaza1;
    Plaza plaza2;

    @BeforeEach
    void preparacion() {
        vehiculoRepository.deleteAll();
        plazaRepository.deleteAll();
        List<Vehiculo> vehiculos = List.of(
                new Vehiculo("azul", TipoVehiculo.MOTO),
                new Vehiculo("rojo", TipoVehiculo.COCHE),
                new Vehiculo("verde", TipoVehiculo.CAMION),
                new Vehiculo("amarillo", TipoVehiculo.TRACTOR),
                new Vehiculo("morado", TipoVehiculo.BICICLETA));

        vehiculo6 = new Vehiculo("granate", TipoVehiculo.COCHE);
        vehiculo7 = new Vehiculo("dorado", TipoVehiculo.COCHE);

        plaza1 = new Plaza();
        plaza2 = new Plaza();

        // Dar un número de plaza + guardar en el repositorio la plaza:
        plaza1 = garajeService.crearPlaza(plaza1);
        plaza2 = garajeService.crearPlaza(plaza2);

        for (int i = 0; i < vehiculos.size(); i++) {
            garajeService.asociarVehiculoAPlaza(plaza1.getId(), vehiculos.get(i));
        }

        vehiculo7 = garajeService.asociarVehiculoAPlaza(plaza2.getId(), vehiculo7);
        assertTrue(true);
    }

    @Test
    void testCrearYAsociarPlazaVehiculo() {
        Plaza plazaDesdeBD = garajeService.getPlaza(plaza1.getId());
        assertTrue(plazaDesdeBD.getVehiculos().size() >= 5);

        assertThrows(maxVehiculosException.class, () -> {
            garajeService.asociarVehiculoAPlaza(plaza1.getId(), vehiculo6);
        });
    }

    @Test
    void testAparcar() {
        // Prueba 1: un coche no registrado (no tiene inicialmente ni ID ni plaza
        // asignada aparca en alguna plaza)
        garajeService.aparcar(plaza1.getId(), vehiculo6);
        Vehiculo vehiculoDesdeBD = garajeService.getVehiculo(vehiculo6.getId());
        assertEquals(1, vehiculoDesdeBD.getMultas().size());

        // Prueba 2: un coche con plaza asignada aparca donde le corresponde
        Vehiculo vehiculo7DesdeBD = garajeService.getVehiculo(vehiculo7.getId());
        int multasInicialesVehiculo7 = vehiculo7DesdeBD.getMultas().size();
        garajeService.aparcar(plaza2.getId(), vehiculo7);
        vehiculo7DesdeBD = garajeService.getVehiculo(vehiculo7.getId());
        assertEquals(multasInicialesVehiculo7, vehiculo7DesdeBD.getMultas().size());

        // Prueba 3: un coche con plaza asignada aparca donde no le corresponde
        garajeService.aparcar(plaza1.getId(), vehiculo7);
        vehiculo7DesdeBD = garajeService.getVehiculo(vehiculo7.getId());
        assertEquals(multasInicialesVehiculo7 + 1, vehiculo7DesdeBD.getMultas().size());
    }

}
