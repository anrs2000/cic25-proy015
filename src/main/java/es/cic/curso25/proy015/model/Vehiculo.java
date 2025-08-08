package es.cic.curso25.proy015.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String color;

    @ManyToOne(optional = true)
    @JoinColumn(name = "plaza_id")
    private Plaza plaza;

    @OneToMany(mappedBy = "vehiculo")
    private List<Multa> multas = new ArrayList<>();

    private int numPlazaAparcada;

    private TipoVehiculo tipoVehiculo;

    public void addMulta(Multa multa) {
        this.multas.add(multa);
        multa.setVehiculo(this);
    }

    public void deleteMulta(Multa multa) {
        this.multas.remove(multa);
        multa.setVehiculo(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Plaza getPlaza() {
        return plaza;
    }

    public void setPlaza(Plaza plaza) {
        this.plaza = plaza;
    }

    public List<Multa> getMultas() {
        return multas;
    }

    public void setMultas(List<Multa> multas) {
        this.multas = multas;
    }

    public int getNumPlazaAparcada() {
        return numPlazaAparcada;
    }

    public void setNumPlazaAparcada(int numPlazaAparcada) {
        this.numPlazaAparcada = numPlazaAparcada;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

}
