package products.mapper;

import org.mapstruct.Mapper;
import products.model.Product;
import webpos.rest.dto.ProductDto;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Collection<ProductDto> toProductsDto(Collection<Product> products);

    ProductDto toProductDto(Product product);
}
