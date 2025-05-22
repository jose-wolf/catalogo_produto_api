package br.com.josewolf.catalogoprodutoapi.business.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "O nome do produto não pode estar em branco.")
    @Size(min = 2, max = 200, message = "O nome do produto deve ter entre 2 e 200 caracteres.")
    private String name;

    private String description;

    @NotNull(message = "O preço do produto não pode ser nulo.")
    @DecimalMin(value = "0.01", message = "O preço do produto deve ser maior que zero.")
    private BigDecimal price;

    @NotNull(message = "A quantidade em estoque não pode ser nula.")
    @Min(value = 1, message = "A quantidade em estoque não pode ser negativa")
    private Integer stockQuantity;

    @NotNull(message = "O ID da categoria é obrigatório e não pode ser nulo.")
    private Long categoryId;

}
