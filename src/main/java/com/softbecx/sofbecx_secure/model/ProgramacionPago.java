package com.softbecx.sofbecx_secure.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "programaciones_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramacionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha programada es obligatoria")
    private LocalDate fechaProgramada;

    @NotNull(message = "El estado de la programación es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoProgramacionPago estado;

    @NotBlank(message = "La observación es obligatoria")
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}