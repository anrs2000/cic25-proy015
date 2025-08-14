package es.cic.curso25.proy015.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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

    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL)
    @JsonIgnore
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

    public Vehiculo() {
    }

    public Vehiculo(String color, TipoVehiculo tipoVehiculo) {
        this.color = color;
        this.tipoVehiculo = tipoVehiculo;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vehiculo other = (Vehiculo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Vehiculo [color=" + color + ", plaza=" + plaza + ", tipoVehiculo=" + tipoVehiculo + "]";
    }

}
