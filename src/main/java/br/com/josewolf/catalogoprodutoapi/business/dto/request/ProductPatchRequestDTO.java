package br.com.josewolf.catalogoprodutoapi.business.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPatchRequestDTO {

    @Size(min = 2, max = 200, message = "O nome do produto deve ter entre 2 e 200 caracteres, se fornecido.")
    private String name;

    @Size(max = 1000, message = "A descrição do produto não pode exceder 1000 caracteres, se fornecida.")
    private String description;

    @DecimalMin(value = "0.01", message = "O preço do produto deve ser maior que zero, se fornecido.")
    private BigDecimal price;

    @Min(value = 1, message = "A quantidade em estoque deve ser de no mínimo 1 unidade, se fornecida.")
    private Integer stockQuantity;

    private Long categoryId;

}
