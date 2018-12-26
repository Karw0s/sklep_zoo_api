package pl.michalkarwowski.api.services;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.AddressDTO;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.dto.clients.BuyerDTO;
import pl.michalkarwowski.api.dto.clients.ClientDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.dto.invoice.InvoicePositionDTO;
import pl.michalkarwowski.api.models.*;
import pl.michalkarwowski.api.repositories.AppUserDetailsRepository;
import pl.michalkarwowski.api.repositories.ClientRepository;
import pl.michalkarwowski.api.repositories.InvoicePositionRepository;
import pl.michalkarwowski.api.repositories.InvoiceRepository;

import javax.print.attribute.standard.Destination;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImp implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ApplicationUserService applicationUserService;
    private final InvoicePositionRepository invoicePosRepository;
    private AppUserDetailsRepository appUserDetailsRepository;
    private ClientRepository clientRepository;
    private AddressService addressService;
    private ClientService clientService;
    private ModelMapper modelMapper;
    private ProductService productService;


    @Autowired
    public InvoiceServiceImp(InvoiceRepository invoiceRepository,
                             ApplicationUserService applicationUserService,
                             InvoicePositionRepository invoicePosRepository,
                             AppUserDetailsRepository appUserDetailsRepository,
                             ClientRepository clientRepository,
                             AddressService addressService,
                             ClientService clientService,
                             ModelMapper modelMapper,
                             ProductService productService) {
        this.invoiceRepository = invoiceRepository;
        this.applicationUserService = applicationUserService;
        this.invoicePosRepository = invoicePosRepository;
        this.appUserDetailsRepository = appUserDetailsRepository;
        this.clientRepository = clientRepository;
        this.addressService = addressService;
        this.clientService = clientService;
        this.modelMapper = modelMapper;
        this.productService = productService;
    }

    @Override
    public Invoice createInvoice(InvoiceDTO invoiceDTO) {
        ApplicationUser user = applicationUserService.getCurrentUser();

        ModelMapper modelMapper2 = new ModelMapper();
        TypeMap<InvoiceDTO, Invoice> typeMap = modelMapper2.createTypeMap(InvoiceDTO.class, Invoice.class);
        typeMap.addMappings(new PropertyMap<InvoiceDTO, Invoice>() {
            @Override
            protected void configure() {
                skip(destination.getPositions());
            }
        });

        modelMapper2.getConfiguration().setAmbiguityIgnored(true);

        modelMapper2.createTypeMap(InvoicePositionDTO.class, InvoicePosition.class)
                .addMappings(mapper -> {
                            mapper.map(InvoicePositionDTO::getProductId, InvoicePosition::setProduct);
                        }
                );

        Invoice invoiceTmp = modelMapper2.map(invoiceDTO, Invoice.class);

        for (InvoicePositionDTO position : invoiceDTO.getPositions()) {
            InvoicePosition invoicePosition = modelMapper2.map(position, InvoicePosition.class);
            if (position.getProductId() != null) {
                invoicePosition.setProduct(productService.getProduct(position.getProductId()));
            }
            invoiceTmp.getPositions().add(invoicePosition);
        }

        AppUserDetails sellerDTO = modelMapper.map(invoiceDTO.getSeller(), AppUserDetails.class);
        sellerDTO.setAddress(addressService.createAddress(invoiceDTO.getSeller().getAddress()));
        AppUserDetails seller = appUserDetailsRepository.save(modelMapper.map(sellerDTO, AppUserDetails.class));
        invoiceTmp.setSeller(seller);

//        invoiceTmp.setBuyer(clientService.createClient(modelMapper.map(invoiceDTO.getBuyer(), ClientDTO.class)));

        if (invoiceDTO.getBuyer().getId() != null) {
            Client buyerDB = clientService.getClient(invoiceDTO.getBuyer().getId());
            if (buyerDB != null) {
                if (invoiceDTO.getBuyer().equals(modelMapper.map(buyerDB, BuyerDTO.class))) {
                    Client buyer = modelMapper.map(invoiceDTO.getBuyer(), Client.class);
                    buyer.setAddress(addressService.createAddress(modelMapper.map(invoiceDTO.getBuyer().getAddress(), AddressDTO.class)));
                    invoiceTmp.setBuyer(clientRepository.save(buyer));
                } else {
                    invoiceTmp.setBuyer(clientService.createClient(modelMapper.map(invoiceDTO.getBuyer(), ClientDTO.class)));
                }
            } else {
                invoiceTmp.setBuyer(clientService.createClient(modelMapper.map(invoiceDTO.getBuyer(), ClientDTO.class)));
            }
        } else {
            invoiceTmp.setBuyer(clientService.createClient(modelMapper.map(invoiceDTO.getBuyer(), ClientDTO.class)));
        }

        invoicePosRepository.saveAll(invoiceTmp.getPositions());
        Invoice invoice = invoiceRepository.save(invoiceTmp);
        user.getInvoices().add(invoice);
        applicationUserService.saveAppUser(user);

        return invoice;
    }

    @Override
    public Invoice getInvoice(Long invoiceNumber) {
        return invoiceRepository.getById(invoiceNumber);
    }

    @Override
    public Invoice updateInvoice(Long id, InvoiceDTO invoice) {
//        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
//        int invoiceIndex = applicationUser.getInvoices().indexOf(invoiceRepository.getById(newInvoice.getId()));
//        Invoice invoice = null;
//        if (invoiceIndex != -1){
//            if (!applicationUser.getInvoices().get(invoiceIndex).equals(newInvoice)){
//                applicationUser.getInvoices().remove(invoiceIndex);
//                invoice = invoiceRepository.save(newInvoice);
//                applicationUser.getInvoices().add(invoice);
//                applicationUserService.saveAppUser(applicationUser);
//            }
//        }
//        return invoice;
        return null;
    }

    @Override
    public boolean deleteInvoice(Long id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Invoice invoice = invoiceRepository.getById(id);
        if (applicationUser.getInvoices().contains(invoice)) {
            if (applicationUser.getInvoices().remove(invoice)) {
                applicationUserService.saveAppUser(applicationUser);
                invoiceRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    @Override
    public Invoice addNewPosition(Product product, String invoiceID, Integer amount) {
//        Invoice invoice = invoiceRepository.findByNr(invoiceID);
//        InvoicePosition invoicePosition = new InvoicePosition();
//        invoicePosition.setInvoice(invoice);
//        invoicePosition.setProduct(product);
//        invoicePosition.setAmount(amount);
//        invoicePosition = invoicePositionRepository.save(invoicePosition);
//        invoice.getInvoicePosition().add(invoicePosition);
        return null;
    }

    @Override
    public List<InvoiceListDTO> getInvoiceList() {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        List<Invoice> invoiceList = applicationUser.getInvoices();
        List<InvoiceListDTO> invoiceListDTO = new ArrayList<>();
        for (Invoice invoice : invoiceList) {
            invoiceListDTO.add(InvoiceListDTO.builder()
                    .id(invoice.getId())
                    .number(invoice.getNumber())
                    .issueDate(invoice.getIssueDate())
                    .paymentType(invoice.getPaymentType())
                    .buyerCompanyName(invoice.getBuyer().getCompanyName())
                    .priceNet(invoice.getPriceNet())
                    .priceGross(invoice.getPriceGross())
                    .build());
        }
        return invoiceListDTO;
    }

    @Override
    public String nextInvoiceNumber() {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        List<Invoice> invoiceList = applicationUser.getInvoices();
        return null;
    }
}
