package es.cic.curso25.proy015.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.cic.curso25.proy015.exception.NotFoundException;
import es.cic.curso25.proy015.exception.maxVehiculosException;
import es.cic.curso25.proy015.model.Multa;
import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.repository.MultaRepository;
import es.cic.curso25.proy015.repository.PlazaRepository;
import es.cic.curso25.proy015.repository.VehiculoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class GarajeService {
    @Autowired
    VehiculoRepository vehiculoRepository;

    @Autowired
    MultaRepository multaRepository;

    @Autowired
    PlazaRepository plazaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(GarajeService.class);

    public Vehiculo getVehiculo(Long id) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(id);
        if (vehiculo.isEmpty()) {
            throw new NotFoundException("No existe ningún vehículo con id " + id);
        }
        vehiculo.get().getMultas().size();
        return vehiculo.get();
    }

    public Plaza getPlaza(Long id) {
        int numVehiculos = plazaRepository.findById(id).get().getVehiculos().size();
        Optional<Plaza> plaza = plazaRepository.findById(id);
        if (plaza.isEmpty()) {
            throw new NotFoundException("No existe ninguna plaza con id " + id);
        }

        // Accedemos a los vehículos de la plaza para evitar un error de
        // Lazyinitialization
        Plaza miPlaza = plaza.get();
        numVehiculos = miPlaza.getVehiculos().size();
        return miPlaza;
    }

    public Plaza crearPlaza(Plaza plaza) {
        LOGGER.info(String.format("Guardando una nueva plaza: %s", plaza));
        plaza = calcularNumPlaza(plaza);

        return plazaRepository.save(plaza);
    };

    public Plaza calcularNumPlaza(Plaza nuevaPlaza) {
        int max = plazaRepository.findMaxNumPlaza();
        LOGGER.info(String.format("Calculando numPlaza para nueva plaza, max actual es %d", max));
        nuevaPlaza.setNumPlaza(max + 1);
        return nuevaPlaza;
    }

    public Vehiculo postVehiculo(Vehiculo vehiculo) {
        LOGGER.info(String.format("Guardando nuevo vehículo: %s", vehiculo));
        return vehiculoRepository.save(vehiculo);
    }

    public void verificarHuecosEnPlaza(Long idPlaza) {
        Plaza plaza = this.getPlaza(idPlaza);
        if (!(plaza.getVehiculos().size() < Plaza.getMaxVehiculos())) {
            throw new maxVehiculosException(String.format(
                    "No se puede asignar el vehiculo a la plaza %d porque está asociada al máximo de vehículos, %d",
                    idPlaza, Plaza.getMaxVehiculos()));
        }
    }

    public Vehiculo asociarVehiculoAPlaza(Long idPlaza, Vehiculo vehiculo) {
        // Guarda el vehículo primero para que se le asigne un ID
        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);

        // Obtiene la plaza y verifica huecos
        Plaza plaza = this.getPlaza(idPlaza);
        this.verificarHuecosEnPlaza(idPlaza);

        // Asocia el vehículo guardado (con ID) a la plaza
        vehiculoGuardado.setPlaza(plaza);
        plaza.addVehiculo(vehiculoGuardado);

        // Guarda la plaza, lo que actualiza la relación en la BD
        plazaRepository.save(plaza);

        // Devuelve el vehículo con su ID asignado
        return vehiculoGuardado;
    }

    public Vehiculo comprobarPlazaCorrecta(Long id) {
        LOGGER.info(String.format("Comprobando plaza correcta para vehiculo id %d", id));
        Vehiculo vehiculo = this.getVehiculo(id);

        // Condición para multar:
        boolean debeMultar = false;

        if (vehiculo.getPlaza() == null) {
            // Caso 1: El vehículo no tiene una plaza asignada.
            LOGGER.info(
                    String.format("El vehículo %s no tiene plaza asignada. Se procede a multar.", vehiculo.toString()));
            debeMultar = true;
        } else if (!(vehiculo.getPlaza().getNumPlaza() == (vehiculo.getNumPlazaAparcada()))) {
            // Caso 2: El vehículo tiene plaza asignada, pero ha aparcado en la incorrecta.
            LOGGER.info(String.format(
                    "El vehículo %s ha aparcado en una plaza que no le corresponde (en la plaza %d, cuando debería haber aparcado en la plaza %d). Se procede a multar.",
                    vehiculo.toString(), vehiculo.getNumPlazaAparcada(), vehiculo.getPlaza().getNumPlaza()));
            debeMultar = true;
        }

        if (debeMultar) {
            vehiculo = this.multarVehiculo(id);
        }

        return vehiculo;
    }

    public Vehiculo multarVehiculo(Long id) {
        LOGGER.info(String.format("Multando vehiculo con id %d", id));
        Multa multa = new Multa();
        Vehiculo vehiculoAMultar = this.getVehiculo(id);
        vehiculoAMultar.addMulta(multa);
        // vehiculoAMultar.getMultas().size();
        return vehiculoRepository.save(vehiculoAMultar);
    }

    public Vehiculo aparcar(Long idPlaza, Vehiculo vehiculo) {
        Plaza plaza = this.getPlaza(idPlaza);

        // Si el vehículo no estaba registrado aun, se registra (sin plaza asignada)
        if (vehiculo.getId() == null) {
            vehiculo = this.postVehiculo(vehiculo);
        }

        Long idVehiculo = vehiculo.getId();

        // Asignamos el número de plaza aparcada al vehículo
        vehiculo.setNumPlazaAparcada(plaza.getNumPlaza());
        vehiculoRepository.save(vehiculo);

        return comprobarPlazaCorrecta(idVehiculo);
    }
}
