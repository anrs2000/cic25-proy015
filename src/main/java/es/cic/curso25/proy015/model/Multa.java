package es.cic.curso25.proy015.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private Vehiculo vehiculo;

    private double precio;

    private LocalDate fechaMaximaPago;

    public Multa() {
    }

    public Multa(double precio){
        this.precio = precio;
        this.fechaMaximaPago = LocalDate.now().plusDays(30); // Asignamos una fecha máxima de pago 30 días después de la multa
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public LocalDate getFechaMaximaPago() {
        return fechaMaximaPago;
    }

    public void setFechaMaximaPago(LocalDate fechaMaximaPago) {
        this.fechaMaximaPago = fechaMaximaPago;
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
        Multa other = (Multa) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Multa [vehiculo=" + vehiculo + ", precio=" + precio + ", fechaMaximaPago=" + fechaMaximaPago + "]";
    }

}
