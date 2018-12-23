package pl.michalkarwowski.api.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.products.ProductCreateResponseDTO;
import pl.michalkarwowski.api.dto.products.ProductDTO;
import pl.michalkarwowski.api.dto.products.ProductDetailsDTO;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.services.ProductService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;
    private ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService,
                             ModelMapper modelMapper) {
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDetailsDTO>> getUserProducts() {
        List<ProductDetailsDTO> response = new ArrayList<>();
        List<Product> productList = productService.getUserProducts();
        for (Product product : productList) {
            response.add(modelMapper.map(product, ProductDetailsDTO.class));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductCreateResponseDTO> addProduct(@Valid @RequestBody ProductDTO product) {
        Product response = productService.createProduct(modelMapper.map(product, Product.class));
        return new ResponseEntity<>(modelMapper.map(response, ProductCreateResponseDTO.class), HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Integer id) {
        Product product2 = productService.getProduct(id);
        if (product2 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return new ResponseEntity<>(modelMapper.map(product2, ProductDTO.class), HttpStatus.OK);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Integer id,
                                                    @Valid @RequestBody ProductDTO product) {
        Product product2 = productService.updateProduct(id, product);
        if (product2 == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
//        SingleProductDto result = SingleProductDto.builder()
//                .result(product2)
//                .error(null)
//                .build();
        return new ResponseEntity<>(modelMapper.map(product2, ProductDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        boolean result = productService.deleteProduct(Integer.parseInt(id));
        return new ResponseEntity<>("{\"Deleted\": " + result + "}", HttpStatus.OK);
    }


    @PostMapping("/products/addlist")
    public ResponseEntity<List<Product>> addProductList(@RequestBody List<ProductDTO> productList) {
        Type listType = new TypeToken<List<Product>>() {}.getType();
        List<Product> products = modelMapper.map(productList, listType);
        products = productService.addProductList(products);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
