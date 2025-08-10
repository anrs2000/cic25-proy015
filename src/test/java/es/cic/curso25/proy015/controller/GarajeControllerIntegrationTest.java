package es.cic.curso25.proy015.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.TipoVehiculo;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.repository.PlazaRepository;
import es.cic.curso25.proy015.repository.VehiculoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GarajeControllerIntegrationTest {

    @Autowired
    VehiculoRepository vehiculoRepository;

    @Autowired
    PlazaRepository plazaRepository;

    @Autowired
    GarajeController garajeController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
        plaza1 = plazaRepository.save(plaza1);
        plaza2 = plazaRepository.save(plaza2);

        for (int i = 0; i < vehiculos.size(); i++) {
            garajeController.asociarVehiculoAPlaza(plaza1.getId(), vehiculos.get(i));
        }

        vehiculo7 = garajeController.asociarVehiculoAPlaza(plaza2.getId(), vehiculo7);
    }

    @Test
    void testAparcar() throws Exception {
        String vehiculo6JSON = objectMapper.writeValueAsString(vehiculo6);

        mockMvc.perform(put("/garaje/aparcar/" + plaza1.getId())
                .contentType("application/json")
                .content(vehiculo6JSON))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Vehiculo vehiculoRespuesta = objectMapper.readValue(result.getResponse().getContentAsString(), Vehiculo.class);
                    assertEquals(vehiculoRespuesta.getNumPlazaAparcada(), plaza1.getNumPlaza());
                });
    }
}
