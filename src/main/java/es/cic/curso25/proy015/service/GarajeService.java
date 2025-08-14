package es.cic.curso25.proy015.service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.cic.curso25.proy015.exception.NotFoundException;
import es.cic.curso25.proy015.exception.PlazaOcupadaException;
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
        // vehiculo.get().getMultas().size();
        return vehiculo.get();
    }

    public Vehiculo getVehiculoConMultas(Long id) {
        Vehiculo vehiculo = this.getVehiculo(id);
        vehiculo.getMultas().size();
        return vehiculo;
    }

    public Plaza getPlaza(Long id) {
        Optional<Plaza> plaza = plazaRepository.findById(id);
        if (plaza.isEmpty()) {
            throw new NotFoundException("No existe ninguna plaza con id " + id);
        }

        // Accedemos a los vehículos de la plaza para evitar un error de
        // Lazyinitialization
        Plaza miPlaza = plaza.get();
        miPlaza.getVehiculos().size();
        return miPlaza;
    }

    public List<Plaza> getAllPlazas() {
        return plazaRepository.findAll();
    }

    public Plaza getPlazaPorNum(int numPlaza) {
        LOGGER.info(String.format("Buscando plaza con numPlaza %d", numPlaza));
        boolean plazaEncontrada = false;
        Plaza plazaBuscada = new Plaza();
        Iterator<Plaza> iterador = this.getAllPlazas().iterator();
        while (iterador.hasNext() && !plazaEncontrada) {
            Plaza plazaActual = iterador.next();
            if (plazaActual.getNumPlaza() == numPlaza) {
                plazaBuscada = plazaActual;
                plazaEncontrada = true;
            }
        }
        if (!plazaEncontrada) {
            throw new NotFoundException("No se ha encontrado ninguna plaza con el numPlaza " + numPlaza);
        }
        return plazaBuscada;
    }

    public List<Vehiculo> getAllVehiculos() {
        return vehiculoRepository.findAll();
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

    public void vaciarPlaza(Long idPlaza) {
        Plaza plaza = this.getPlaza(idPlaza);
        plaza.setOcupada(false);
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

    public Vehiculo comprobarPlazaCorrecta(Long id, int numDias) {
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
            vehiculo = this.multarVehiculo(id, numDias);
        }

        return vehiculo;
    }

    public double calcularPrecioMulta(int numDias) {
        return numDias * 5;
    }

    public Vehiculo multarVehiculo(Long id, int numDias) {
        LOGGER.info(String.format("Multando vehiculo con id %d", id));

        // if (numDias <= 0) {
        // throw new IllegalArgumentException("El número de días debe ser mayor que
        // 0.");
        // }

        double precio = this.calcularPrecioMulta(numDias);

        Multa multa = new Multa(precio);
        Vehiculo vehiculoAMultar = this.getVehiculo(id);
        vehiculoAMultar.addMulta(multa);
        // vehiculoAMultar.getMultas().size();
        return vehiculoAMultar;
        // return vehiculoRepository.save(vehiculoAMultar);
    }

    public Vehiculo aparcar(Long idPlaza, Vehiculo vehiculo) {
        Plaza plaza = this.getPlaza(idPlaza);

        // Si el vehículo no estaba registrado aun, se registra (sin plaza asignada)
        if (vehiculo.getId() == null) {
            vehiculo = this.postVehiculo(vehiculo);
        }

        Long idVehiculo = vehiculo.getId();

        // Revisamos si la plaza en la que se quiere aparcar está libre
        comprobarPlazaVacia(idPlaza);

        // En caso de que la plaza esté libre:
        // 1. Comprobamos si el coche estaba ya aparcado en alguna plaza
        if (this.getVehiculo(idVehiculo).getNumPlazaAparcada() != 0) {
            int numPlaza = this.getVehiculo(idVehiculo).getNumPlazaAparcada();
            Plaza plazaOcupada = this.getPlazaPorNum(numPlaza);
            // Vaciamos la plaza en la que estaba aparcado el coche
            plazaOcupada.setOcupada(false);
        }

        // 2. Asignamos el número de plaza aparcada al vehículo
        vehiculo.setNumPlazaAparcada(plaza.getNumPlaza());
        vehiculoRepository.save(vehiculo);

        // 3. Y marcamos la plaza como ocupada
        plaza.setOcupada(true);

        return vehiculo;

        // return comprobarPlazaCorrecta(idVehiculo);
    }

    public Vehiculo salir(Long idVehiculo, int numDias) {
        Vehiculo vehiculo = this.getVehiculo(idVehiculo);

        if (numDias <= 0) {
            throw new IllegalArgumentException("El número de días debe ser mayor que 0.");
        }

        int numPlazaPreviamenteAparcada = vehiculo.getNumPlazaAparcada();
        Plaza plaza = this.getPlazaPorNum(numPlazaPreviamenteAparcada);

        // Comprobamos si el vehículo estaba aparcado en alguna plaza
        if (numPlazaPreviamenteAparcada == 0) {
            throw new NotFoundException("El vehículo no está aparcado en ninguna plaza.");
        }

        vehiculo = this.comprobarPlazaCorrecta(idVehiculo, numDias);

        vehiculo.setNumPlazaAparcada(0);
        vehiculoRepository.save(vehiculo);

        // Y marcamos la plaza como libre
        plaza.setOcupada(false);

        return vehiculo;
    }

    public void comprobarPlazaVacia(Long idPlaza) {
        // Comprobamos que la plaza existe
        Plaza plaza = this.getPlaza(idPlaza);
        if (plaza.isOcupada()) {
            Vehiculo vehiculo = buscarVehiculoAparcado(idPlaza);
            throw new PlazaOcupadaException("La plaza está ocupada por el vehículo " + vehiculo.toString());
        }
    }

    public Vehiculo buscarVehiculoAparcado(Long idPlaza) {
        Plaza plaza = this.getPlaza(idPlaza);
        List<Vehiculo> vehiculos = this.getAllVehiculos();
        boolean encontrado = false;

        Vehiculo miVehiculo = new Vehiculo();

        Iterator<Vehiculo> iterador = vehiculos.iterator();
        while (iterador.hasNext()) {
            Vehiculo vehiculoActual = iterador.next();
            if (vehiculoActual.getNumPlazaAparcada() == plaza.getNumPlaza()) {
                miVehiculo = vehiculoActual;
                encontrado = true;
            }
        }

        if (!encontrado) {
            throw new NotFoundException(
                    "No se ha encontrado ningún vehículo aparcado en la plaza " + plaza.getNumPlaza());
        }

        return miVehiculo;
    }

}
