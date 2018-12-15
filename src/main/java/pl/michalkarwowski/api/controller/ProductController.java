package pl.michalkarwowski.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalkarwowski.api.dto.SingleProductDto;
import pl.michalkarwowski.api.model.Product;
import pl.michalkarwowski.api.service.ProductService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getUserProducts() {
        return new ResponseEntity<>(productService.getUserProducts(), HttpStatus.OK);
    }

    @PostMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Product product2 = productService.updateProduct(product);
        if (product2 == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
//        SingleProductDto result = SingleProductDto.builder()
//                .result(product2)
//                .error(null)
//                .build();
        return new ResponseEntity<>(product2, HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        boolean result = productService.deleteProduct(Integer.parseInt(id));
        return new ResponseEntity<>("{\"Deleted\": " + result + "}", HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Product product2 = productService.getProduct(Integer.parseInt(id));
        if (product2 == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product2, HttpStatus.OK);
    }

    @PostMapping("/products/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, HttpServletRequest request) {
        product = productService.addNewProduct(product, request.getUserPrincipal().getName());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/products/addlist")
    public ResponseEntity<List<Product>> addProductList(@RequestBody List<Product> productList) {
        productList = productService.addProductList(productList);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
