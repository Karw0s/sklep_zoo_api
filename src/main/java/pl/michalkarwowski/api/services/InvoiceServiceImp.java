package pl.michalkarwowski.api.services;

import com.itextpdf.text.Document;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
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
    public Invoice getInvoice(Long id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            if (applicationUserService.getCurrentUser().getInvoices().contains(invoice.get()))
                invoice.get().getPositions().sort(Comparator.comparing(InvoicePosition::getOrdinalNumber));
                return invoice.get();
        }
        return null;
    }

    @Override
    public Invoice updateInvoice(Long id, InvoiceDTO invoiceDTO) throws InvoiceExistsException {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Invoice invoiceDB = invoiceRepository.findById(id).orElse(null);
        if (invoiceDB != null) {
            if (applicationUser.getInvoices().contains(invoiceDB)) {
                InvoiceDTO invoiceDBDto = modelMapper.map(invoiceDB, InvoiceDTO.class);
                if (!invoiceDBDto.equals(invoiceDTO)) {
                    if (!invoiceDB.getNumber().equals(invoiceDTO.getNumber())) {
                        if (applicationUser.getInvoices().stream().filter(i -> i.getNumber().equals(invoiceDTO.getNumber())).findAny().orElse(null) == null) {
                            throw new InvoiceExistsException("Invoice with that number already exists");
                        }
                        invoiceDB.setNumber(invoiceDTO.getNumber());
                        invoiceDB = invoiceRepository.save(invoiceDB);
                    }

                    if (!invoiceDBDto.getBuyer().equals(invoiceDTO.getBuyer())) {
                        // update buyer
                        Address buyerDbAddress = invoiceDB.getBuyer().getAddress();
                        BuyerDTO newBuyer = invoiceDTO.getBuyer();
                        if (!invoiceDBDto.getBuyer().getAddress().equals(newBuyer.getAddress())) { // addresses are different
                            Address buyerNewAddress = modelMapper.map(newBuyer.getAddress(), Address.class);
                            buyerNewAddress.setId(invoiceDB.getBuyer().getAddress().getId());
                            buyerDbAddress = addressService.updateAddress(buyerNewAddress);
                            invoiceDB.getBuyer().setAddress(buyerDbAddress);
                            invoiceDBDto = modelMapper.map(invoiceDB, InvoiceDTO.class);
                        }
                        if (!invoiceDBDto.getBuyer().equals(invoiceDTO.getBuyer())) {   // buyer details are different
                            Client buyer = modelMapper.map(invoiceDTO.getBuyer(), Client.class);
                            buyer.setAddress(buyerDbAddress);
                            invoiceDB.setBuyer(clientRepository.save(buyer));
                        }
                    }

                    if (!invoiceDBDto.getSeller().equals(invoiceDTO.getSeller())) {
                        // update seller
                        System.out.println("update sellers");
                        AppUserDetailsDTO seller = invoiceDTO.getSeller();
                        if (!invoiceDBDto.getSeller().getAddress().equals(seller.getAddress())) {
                            Address sellerNewAddress = modelMapper.map(seller.getAddress(), Address.class);
                            sellerNewAddress.setId(invoiceDB.getSeller().getAddress().getId());
                            Address sellerDbAddress = addressService.updateAddress(sellerNewAddress);
                            invoiceDB.getSeller().setAddress(sellerDbAddress);
                            invoiceDBDto = modelMapper.map(invoiceDB, InvoiceDTO.class);
                        }
                        if (!invoiceDBDto.getSeller().equals(invoiceDTO.getSeller())) {
                            invoiceDB.getSeller().setCompanyName(seller.getCompanyName());
                            invoiceDB.getSeller().setFirstName(seller.getFirstName());
                            invoiceDB.getSeller().setLastName(seller.getLastName());
                            invoiceDB.getSeller().setNipNumber(seller.getNipNumber());

                            invoiceDB.getSeller().setBank(seller.getBank());
                            invoiceDB.getSeller().setBankAccountNumber(seller.getBankAccountNumber());
                            invoiceDB.setSeller(appUserDetailsRepository.save(invoiceDB.getSeller()));
                        }
                    }

                    if (!invoiceDBDto.getPositions().equals(invoiceDTO.getPositions())) {
                        // update positions
                        ModelMapper modelMapper2 = new ModelMapper();
                        modelMapper2.getConfiguration().setAmbiguityIgnored(true);
                        modelMapper2.createTypeMap(InvoicePositionDTO.class, InvoicePosition.class)
                                .addMappings(mapper -> mapper.map(InvoicePositionDTO::getProductId, InvoicePosition::setProduct));

                        List<InvoicePosition> positionsDTO = new ArrayList<>();

                        for (InvoicePositionDTO position : invoiceDTO.getPositions()) {
                            InvoicePosition invoicePosition = modelMapper2.map(position, InvoicePosition.class);
                            if (position.getProductId() != null) {
                                invoicePosition.setProduct(productService.getProduct(position.getProductId()));
                            }
                            positionsDTO.add(invoicePosition);
                        }

                        Map<Long, Boolean> isPresent = new HashMap<>();
                        for (InvoicePosition position : invoiceDB.getPositions()) {
                            isPresent.put(position.getId(), false);
                        }

                        for (InvoicePosition positionDTO : positionsDTO) {
                            if (isPresent.containsKey(positionDTO.getId())) {
                                isPresent.replace(positionDTO.getId(), true);
                            }
                        }

                        if (isPresent.containsValue(false)) {
                            for (InvoicePosition position : invoiceDB.getPositions()) {
                                if(!isPresent.get(position.getId())) {
                                    invoicePosRepository.deleteById(position.getId());
                                }
                            }
                        }

                        invoiceDB.setPositions(positionsDTO);
                        invoicePosRepository.saveAll(invoiceDB.getPositions());
                    }
                    // update invoice details

                    invoiceDB.setIssueDate(invoiceDTO.getIssueDate());
                    invoiceDB.setIssuePlace(invoiceDTO.getIssuePlace());
                    invoiceDB.setSaleDate(invoiceDTO.getSaleDate());
                    invoiceDB.setPaymentType(invoiceDTO.getPaymentType());
                    invoiceDB.setPriceNet(invoiceDTO.getPriceNet());
                    invoiceDB.setPriceGross(invoiceDTO.getPriceGross());
                    invoiceDB.setPriceTax(invoiceDTO.getPriceTax());
                    invoiceDB.setShowPKWIUCode(invoiceDTO.isShowPKWIUCode());
                    invoiceDB.setLastUpdated(new Date());
                    return invoiceRepository.save(invoiceDB);
                }
            }
        }
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
    public List<InvoiceListDTO> getInvoiceList() {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        List<Invoice> invoiceList = applicationUser.getInvoices();
        List<InvoiceListDTO> invoiceListDTO = new ArrayList<>();
        ModelMapper modelMapper2 = new ModelMapper();
        modelMapper2.createTypeMap(Invoice.class, InvoiceListDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getBuyer().getCompanyName(), InvoiceListDTO::setBuyerCompanyName);
                });
        for (Invoice invoice : invoiceList) {
            invoiceListDTO.add(modelMapper2.map(invoice, InvoiceListDTO.class));
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
