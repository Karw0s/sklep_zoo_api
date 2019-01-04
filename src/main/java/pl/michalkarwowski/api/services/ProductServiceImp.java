package pl.michalkarwowski.api.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.michalkarwowski.api.dto.products.ProductDTO;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.repositories.InvoicePositionRepository;
import pl.michalkarwowski.api.repositories.ProductRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private InvoicePositionRepository invoicePositionRepository;
    private final ApplicationUserService applicationUserService;
    private ModelMapper modelMapper;

    @Autowired
    public ProductServiceImp(ProductRepository productRepository,
                             InvoicePositionRepository invoicePositionRepository,
                             ApplicationUserService applicationUserService,
                             ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.invoicePositionRepository = invoicePositionRepository;
        this.applicationUserService = applicationUserService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Product> getUserProducts() {
        List<Product> products = applicationUserService.getCurrentUser().getProducts();
        products.sort(Comparator.comparing(Product::getName));
        return products;
    }

    @Override
    public Product getProduct(Integer id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Product product = productRepository.getById(id);
        if (applicationUser.getProducts().contains(product)) {
            return product;
        } else {
            return null;
        }
//        return productRepository.getById(id);
    }

    @Override
    public Product createProduct(Product product) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        applicationUser.getProducts().add(product);
        productRepository.save(product);
        applicationUserService.saveAppUser(applicationUser);
        return product;
    }

    @Override
    public List<Product> addProductListFromCSV(MultipartFile file) throws IOException {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        InputStream in = new DataInputStream(file.getInputStream());
        Reader reader = new InputStreamReader(in);
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();
        CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();

        List<ProductDTO> productsDTO = new ArrayList<>();
        List<String[]> allData = csvReader.readAll();

        for (String[] row : allData) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setCatalogNumber(row[0]);
            productDTO.setName(row[1]);
            productDTO.setManufacturer(row[2]);
            productDTO.setUnitOfMeasure(row[3]);
            productDTO.setAmount(Double.parseDouble(row[4]));
            productDTO.setPriceNetto(Double.parseDouble(row[5].replace(",", ".")));
            productDTO.setPriceBrutto(Double.parseDouble(row[6].replace(",", ".")));
            productDTO.setTax(row[7]);
            productDTO.setPkwiuCode(row[8]);
            productDTO.setBarCode(row[9]);
            productsDTO.add(productDTO);
        }

        Type listType = new TypeToken<List<Product>>() {}.getType();
        List<Product> products = modelMapper.map(productsDTO, listType);
        applicationUser.getProducts().addAll(products);

        return (List<Product>) productRepository.saveAll(products);
    }

    @Override
    public Product updateProduct(Integer id, ProductDTO productDTO) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Product newProduct = modelMapper.map(productDTO, Product.class);
        newProduct.setId(id);
        int productIndex = applicationUser.getProducts().indexOf(productRepository.getById(id));
        Product product = null;
        if (productIndex != -1)
            if (!applicationUser.getProducts().get(productIndex).equals(newProduct)) {
                applicationUser.getProducts().remove(productIndex);

                product = productRepository.save(newProduct);
                applicationUser.getProducts().add(product);
                applicationUserService.saveAppUser(applicationUser);
            }

        return product;
    }

    @Override
    public boolean deleteProduct(Integer id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Product product = productRepository.getById(id);
        if (applicationUser.getProducts().contains(product)) {
            if (applicationUser.getProducts().remove(productRepository.getById(id))) {
                applicationUserService.saveAppUser(applicationUser);
                if (invoicePositionRepository.findAllByProductId(id).isEmpty())
                    productRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
}
