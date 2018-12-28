package pl.michalkarwowski.api.services;

import com.itextpdf.text.Document;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.dto.clients.BuyerDTO;
import pl.michalkarwowski.api.dto.clients.ClientDTO;
import pl.michalkarwowski.api.dto.invoice.InvoiceDTO;
import pl.michalkarwowski.api.dto.invoice.InvoicePositionDTO;
import pl.michalkarwowski.api.exceptions.InvoiceExistsException;
import pl.michalkarwowski.api.models.*;
import pl.michalkarwowski.api.repositories.*;

import java.util.*;

@Service
public class InvoiceServiceImp implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private InvoiceNextNumberRepository invoiceNumberRepository;
    private final ApplicationUserService applicationUserService;
    private final InvoicePositionRepository invoicePosRepository;
    private AppUserDetailsRepository appUserDetailsRepository;
    private ProductRepository productRepository;
    private ClientRepository clientRepository;
    private AddressService addressService;
    private ClientService clientService;
    private ModelMapper modelMapper;
    private ProductService productService;


    @Autowired
    public InvoiceServiceImp(InvoiceRepository invoiceRepository,
                             InvoicePositionRepository invoicePosRepository,
                             InvoiceNextNumberRepository invoiceNumberRepository,
                             AppUserDetailsRepository appUserDetailsRepository,
                             ClientRepository clientRepository,
                             ApplicationUserService applicationUserService,
                             ProductRepository productRepository,
                             AddressService addressService,
                             ClientService clientService,
                             ProductService productService,
                             ModelMapper modelMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceNumberRepository = invoiceNumberRepository;
        this.applicationUserService = applicationUserService;
        this.invoicePosRepository = invoicePosRepository;
        this.appUserDetailsRepository = appUserDetailsRepository;
        this.productRepository = productRepository;
        this.clientRepository = clientRepository;
        this.addressService = addressService;
        this.clientService = clientService;
        this.modelMapper = modelMapper;
        this.productService = productService;
    }

    @Override
    public Invoice createInvoice(InvoiceDTO invoiceDTO) throws InvoiceExistsException {
        ApplicationUser user = applicationUserService.getCurrentUser();

        Optional<Invoice> invoiceExists = user.getInvoices().stream()
                .filter(invoice -> invoice.getNumber().equals(invoiceDTO.getNumber()))
                .findAny();

        if (invoiceExists.isPresent()) {
            throw new InvoiceExistsException("Invoice with that number already exists");
        }


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

        if (invoiceDTO.getBuyer().getId() != null) { // czy id klienta występuje w DTO
            Client buyerDB = clientService.getClient(invoiceDTO.getBuyer().getId());

            if (buyerDB == null) { // stwórz bo nie ma w bazie
                Client buyer = clientService.createClient(modelMapper.map(invoiceDTO.getBuyer(), ClientDTO.class));
                invoiceTmp.setOriginalBuyerId(buyer.getId());
                invoiceTmp.setBuyer(clientService.createCopy(buyer.getId()));
            } else { // jest w bazie
                if (invoiceDTO.getBuyer().equals(modelMapper.map(buyerDB, BuyerDTO.class))) { // taki sam, zapisz kopię
                    invoiceTmp.setOriginalBuyerId(buyerDB.getId());
                    invoiceTmp.setBuyer(clientService.createCopy(buyerDB.getId()));
                } else { // update, kopia
                    ClientDTO buyer = modelMapper.map(invoiceDTO.getBuyer(), ClientDTO.class);
                    Client buyerCopy = clientService.updateClient(buyerDB.getId(), buyer);
                    invoiceTmp.setOriginalBuyerId(buyerCopy.getId());
                    invoiceTmp.setBuyer(clientService.createCopy(buyerDB.getId()));
                }
            }
        } else { //stworz klienta, kopia
            Client buyer = clientService.createClient(modelMapper.map(invoiceDTO.getBuyer(), ClientDTO.class));
            invoiceTmp.setOriginalBuyerId(buyer.getId());
            invoiceTmp.setBuyer(clientService.createCopy(buyer.getId()));
        }

        invoiceTmp.setCreated(new Date());

        invoicePosRepository.saveAll(invoiceTmp.getPositions());
        Invoice invoice = invoiceRepository.save(invoiceTmp);
        user.getInvoices().add(invoice);
        applicationUserService.saveAppUser(user);

        generateNextInvoiceNumber(invoice.getIssueDate(), invoice.getNumber());

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
            for (InvoicePosition position : invoice.getPositions()) {
                if (position.getProduct() != null) {
                    if (!applicationUser.getProducts().contains(position.getProduct())) {
                        productRepository.deleteById(position.getProduct().getId());
                    }
                }
                invoicePosRepository.deleteById(position.getId());
            }
            invoice.getPositions().removeAll(invoice.getPositions());

            if (!applicationUser.getClients().contains(invoice.getBuyer())) {
                Integer id1 = invoice.getBuyer().getId();
                clientRepository.deleteById(id1);
            }

            appUserDetailsRepository.deleteById(invoice.getSeller().getId());

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
        invoiceListDTO.sort(Comparator.comparing(InvoiceListDTO::getIssueDate));
        Collections.reverse(invoiceListDTO);
        return invoiceListDTO;
    }

    @Override
    public String nextInvoiceNumber(Date issueDate) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        List<InvoiceNextNumber> invoiceNumberList = applicationUser.getInvoiceNextNumber();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(issueDate);

        InvoiceNextNumber res = invoiceNumberList.stream()
                .filter(invoiceNextNumber -> calendar.get(Calendar.YEAR) == invoiceNextNumber.getYear() && calendar.get(Calendar.MONTH) + 1 == invoiceNextNumber.getMonth())
                .findAny().orElse(null);

        if (res == null) {
            String month = calendar.get(Calendar.MONTH) + 1 < 10 ? "0" + Integer.toString(calendar.get(Calendar.MONTH) + 1) : Integer.toString(calendar.get(Calendar.MONTH) + 1);
            InvoiceNextNumber nextNumber = invoiceNumberRepository.save(InvoiceNextNumber.builder()
                    .lastInvoiceNumber("1/" + month + "/" + calendar.get(Calendar.YEAR))
                    .nextInvoiceNumber(null)
                    .month(calendar.get(Calendar.MONTH) + 1)
                    .year(calendar.get(Calendar.YEAR))
                    .lastUpdate(new Date())
                    .build());
            applicationUser.getInvoiceNextNumber().add(nextNumber);
            applicationUserService.saveAppUser(applicationUser);
            return nextNumber.getLastInvoiceNumber();
        }

        if (res.getNextInvoiceNumber() == null) {
            return res.getLastInvoiceNumber();
        } else {
            return res.getNextInvoiceNumber();
        }
    }

    @Override
    public Document generateInvoicePDF(Long id) {
        return null;
    }

    private InvoiceNextNumber generateNextInvoiceNumber(Date issueDate, String invoiceNumber) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        List<InvoiceNextNumber> invoiceNumberList = applicationUser.getInvoiceNextNumber();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(issueDate);

        InvoiceNextNumber nextNumberByIssueDate = invoiceNumberList.stream()
                .filter(invoiceNextNumber -> calendar.get(Calendar.YEAR) == invoiceNextNumber.getYear() && calendar.get(Calendar.MONTH) + 1 == invoiceNextNumber.getMonth())
                .findAny().orElse(null);

        String[] numberSplit = new String[0];

        if (nextNumberByIssueDate != null) {    // invoiceNumber is matching issueDate

            if (nextNumberByIssueDate.getLastInvoiceNumber().equals(invoiceNumber)) {   // first invoice in issueDate month

                if (nextNumberByIssueDate.getNextInvoiceNumber() == null) {
                    numberSplit = nextNumberByIssueDate.getLastInvoiceNumber().split("/");
                } else {
                    numberSplit = nextNumberByIssueDate.getNextInvoiceNumber().split("/");
                }

                numberSplit[0] = Integer.toString(Integer.parseInt(numberSplit[0]) + 1);
                String nextNumber = String.join("/", numberSplit);
                nextNumberByIssueDate.setLastInvoiceNumber(nextNumberByIssueDate.getNextInvoiceNumber());
                nextNumberByIssueDate.setNextInvoiceNumber(nextNumber);
                nextNumberByIssueDate.setLastUpdate(new Date());
                return invoiceNumberRepository.save(nextNumberByIssueDate);

            } else if (nextNumberByIssueDate.getNextInvoiceNumber().equals(invoiceNumber)) {    // next invoice in issueDate month

                numberSplit = nextNumberByIssueDate.getNextInvoiceNumber().split("/");
                nextNumberByIssueDate.setLastInvoiceNumber(nextNumberByIssueDate.getNextInvoiceNumber());
                nextNumberByIssueDate.setNextInvoiceNumber(Integer.parseInt(numberSplit[0]) + 1 + "/" + numberSplit[1] + "/" + numberSplit[2]);
                return invoiceNumberRepository.save(nextNumberByIssueDate);
            } else {    // invoiceNumber do not match next invoice number in issueDate month

                if (nextNumberByIssueDate.getNextInvoiceNumber() == null) {
                    numberSplit = nextNumberByIssueDate.getLastInvoiceNumber().split("/");
                } else {
                    numberSplit = nextNumberByIssueDate.getNextInvoiceNumber().split("/");
                }

                String[] invoiceNumberSplitted = invoiceNumber.split("/");
                int year = Integer.parseInt(invoiceNumberSplitted[2]);
                int month = Integer.parseInt(invoiceNumberSplitted[1]);
                int invoiceNum = Integer.parseInt(invoiceNumberSplitted[0]);
                String monthString = month < 10 ? "0" + Integer.toString(month) : Integer.toString(month);

                if (Integer.parseInt(numberSplit[0]) < invoiceNum) {
                    nextNumberByIssueDate.setLastInvoiceNumber(invoiceNumber);

                    boolean exists = true;
                    while (exists) {
                        Optional<Invoice> invoice = invoiceRepository.findByNumber(invoiceNum + 1 + "/" + monthString + "/" + year);  //zamienic na szukanie w fakturach usera
                        if (invoice.isPresent()) {
                            invoiceNum += 1;
                        } else {
                            exists = false;
                        }
                    }

                    nextNumberByIssueDate.setNextInvoiceNumber(invoiceNum + 1 + "/" + monthString + "/" + year);
                    nextNumberByIssueDate.setLastUpdate(new Date());
                    return invoiceNumberRepository.save(nextNumberByIssueDate);
                }

            }
        } else {
//            String[] invoiceNumberSplitted = invoiceNumber.split("/");
//            int year = Integer.parseInt(invoiceNumberSplitted[2]);
//            int month = Integer.parseInt(invoiceNumberSplitted[1]);
//            int invoiceNum = Integer.parseInt(invoiceNumberSplitted[0]);
//            String monthString = month < 10 ? "0" + Integer.toString(month) : Integer.toString(month);
//            InvoiceNextNumber result = invoiceNumberList.stream()
//                    .filter(invoiceNextNumber -> year == invoiceNextNumber.getYear() && month == invoiceNextNumber.getMonth())
//                    .findAny().orElse(null);
//            if (result == null) {
//
//                InvoiceNextNumber nextNumber = invoiceNumberRepository.save(InvoiceNextNumber.builder()
//                        .lastInvoiceNumber(invoiceNum + "/" + monthString + "/" + year)
//                        .nextInvoiceNumber(invoiceNum + 1 + "/" + monthString + "/" + year)
//                        .month(month)
//                        .year(year)
//                        .lastUpdate(new Date())
//                        .build());
//                applicationUser.getInvoiceNextNumber().add(nextNumber);
//                applicationUserService.saveAppUser(applicationUser);
//                return nextNumber;
//            } else {
//                String[] resultSplitedNumber;
//                if (result.getNextInvoiceNumber() == null) {
//                    resultSplitedNumber = result.getLastInvoiceNumber().split("/");
//                } else {
//                    resultSplitedNumber = result.getNextInvoiceNumber().split("/");
//                }
//                if (Integer.parseInt(resultSplitedNumber[0]) < invoiceNum) {
//                    result.setLastInvoiceNumber(invoiceNumber);
//                    result.setNextInvoiceNumber(invoiceNum + 1 + "/" + monthString + "/" + year);
//                    result.setLastUpdate(new Date());
//                    return invoiceNumberRepository.save(result);
//                }
//            }
        }
        return null;
    }
}
