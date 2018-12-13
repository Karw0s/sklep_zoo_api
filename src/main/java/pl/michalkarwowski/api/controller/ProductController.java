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

    @GetMapping("/product")
    public ResponseEntity<List<Product>> getUserProducts(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        return new ResponseEntity<>(productService.getUserProducts(username), HttpStatus.OK);
    }

    @PostMapping("/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product, HttpServletRequest request) {
        Product product2 = productService.updateProduct(product, request.getUserPrincipal().getName());
        if (product2 == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
//        SingleProductDto result = SingleProductDto.builder()
//                .result(product2)
//                .error(null)
//                .build();
        return new ResponseEntity<>(product2, HttpStatus.OK);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id, HttpServletRequest request) {
        boolean result = productService.deleteProduct(Integer.parseInt(id), request.getUserPrincipal().getName());
        return new ResponseEntity<>("{\"Deleted\": " + result + "}", HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id, HttpServletRequest request) {
        Product product2 = productService.getProduct(Integer.parseInt(id), request.getUserPrincipal().getName());
        if (product2 == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(product2, HttpStatus.OK);
    }

    @PostMapping("/product/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product product, HttpServletRequest request) {
        product = productService.addNewProduct(product, request.getUserPrincipal().getName());
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping("/product/addlist")
    public ResponseEntity<List<Product>> addProductList(@RequestBody List<Product> productList, HttpServletRequest request) {
        productList = productService.addProductList(productList, request.getUserPrincipal().getName());
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}
