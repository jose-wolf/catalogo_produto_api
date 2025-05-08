package br.com.josewolf.catalogoprodutoapi.infraestrutura.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;
}

//@Id 6. Marca este campo como a chave primária da tabela.
//@GeneratedValue(strategy = GenerationType.IDENTITY)  7. Configura a geração automática do ID pelo banco (estratégia IDENTITY é comum para PostgreSQL).
//private Long id;

//@Column(nullable = false, unique = true, length = 100) // 8. Mapeia para uma coluna.
// nullable = false: não pode ser nulo.
// unique = true: o nome da categoria deve ser único.
// length = 100: define o tamanho máximo da string para a coluna (bom para otimização do banco).
//private String name;

// Relacionamento Inverso: Uma categoria pode ter muitos produtos.
// mappedBy = "category": Indica que o lado "Product" é o dono do relacionamento
//                      e o campo "category" na classe Product é usado para mapear esta associação.
// fetch = FetchType.LAZY: Os produtos não são carregados do banco junto com a categoria,
//                         apenas quando explicitamente acessados (getProducts()). Bom para performance.
// cascade = CascadeType.ALL: Operações de persistência (salvar, deletar) na Categoria
//          serão propagadas para os Produtos associados. Use com CUIDADO.
//                 Para começar, podemos omitir o cascade ou usar um mais restritivo.
//@OneToMany(mappedBy = "category", fetch = FetchType.LAZY /*, cascade = CascadeType.ALL */)
//private List<Product> products;
