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

import java.time.LocalDateTime;

@Entity
@Table(name = "validaciones_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de validación es obligatoria")
    private LocalDateTime fechaValidacion;

    @NotNull(message = "El resultado de la validación es obligatorio")
    @Enumerated(EnumType.STRING)
    private ResultadoValidacion resultado;

    @NotBlank(message = "La observación es obligatoria")
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}