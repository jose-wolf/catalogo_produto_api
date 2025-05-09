package br.com.josewolf.catalogoprodutoapi.infraestrutura.repository;

import br.com.josewolf.catalogoprodutoapi.infraestrutura.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
