package es.cic.curso25.proy015.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.service.GarajeService;

@RestController
@RequestMapping("/garaje")
public class GarajeController {
    @Autowired
    GarajeService garajeService;

    private final Logger LOGGER = LoggerFactory.getLogger(GarajeController.class);

    @PutMapping("/aparcar/{plaza}")
    public Vehiculo aparcar(@PathVariable("plaza") Long idPlaza, @RequestBody Vehiculo vehiculoAAparcar) {
        Plaza plaza = garajeService.getPlaza(idPlaza);

        LOGGER.info(String.format("Aparcando en la plaza %d con el vehículo %s mediante el método POST /aparcar/%d",
                plaza.getNumPlaza(), plaza.toString(), idPlaza));
        return garajeService.aparcar(idPlaza, vehiculoAAparcar);
    }

    @PutMapping("/asociarVehiculo/{idPlaza}")
    public Vehiculo asociarVehiculoAPlaza(@PathVariable Long idPlaza, @RequestBody Vehiculo vehiculo) {
        return garajeService.asociarVehiculoAPlaza(idPlaza, vehiculo);
    }

    


}
