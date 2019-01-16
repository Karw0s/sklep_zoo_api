package pl.michalkarwowski.api.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.michalkarwowski.api.dto.ErrorMessage;
import pl.michalkarwowski.api.dto.products.ProductCreateResponseDTO;
import pl.michalkarwowski.api.dto.products.ProductDTO;
import pl.michalkarwowski.api.dto.products.ProductDetailsDTO;
import pl.michalkarwowski.api.exceptions.InvalidCSVException;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.services.ProductService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
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
        return new ResponseEntity<>(modelMapper.map(product2, ProductDTO.class), HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if(productService.deleteProduct(Integer.parseInt(id))) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/products/csv")
    public ResponseEntity<Object> addProductListFromCSV(@RequestParam("file") MultipartFile file) {
        List<Product> products = null;
        try {
            products = productService.addProductListFromCSV(file);
        } catch (IOException | InvalidCSVException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorMessage.builder()
                            .errorField("File Parsing")
                            .message(e.getMessage())
                            .build());
        }
        Type listType = new TypeToken<List<ProductDTO>>() {}.getType();
        List<ProductDTO> productsDTO = modelMapper.map(products, listType);
        return new ResponseEntity<>(productsDTO, HttpStatus.OK);
    }
}
