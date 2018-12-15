package pl.michalkarwowski.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.michalkarwowski.api.dto.InvoiceListDTO;
import pl.michalkarwowski.api.models.ApplicationUser;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoicePosition;
import pl.michalkarwowski.api.models.Product;
import pl.michalkarwowski.api.repositories.InvoiceRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceServiceImp implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ApplicationUserService applicationUserService;
    private final InvoicePositionService invoicePositionService;

    @Autowired
    public InvoiceServiceImp(InvoiceRepository invoiceRepository,
                             ApplicationUserService applicationUserService,
                             InvoicePositionService invoicePositionService) {
        this.invoiceRepository = invoiceRepository;
        this.applicationUserService = applicationUserService;
        this.invoicePositionService = invoicePositionService;
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        ApplicationUser user = applicationUserService.getCurrentUser();

        List<InvoicePosition> invPos = invoicePositionService.createInvoicePos(invoice.getPositions());
        invoice.setPositions(invPos);
        Invoice invoice1 = invoiceRepository.save(invoice);
        user.getInvoices().add(invoice);
        applicationUserService.saveAppUser(user);
        return invoice1;
    }

    @Override
    public Invoice getInvoice(Long invoiceNumber) {
        return invoiceRepository.getById(invoiceNumber);
    }

    @Override
    public List<Invoice> getUserInvoices() {
        return applicationUserService.getCurrentUser().getInvoices();
    }

    @Override
    public Invoice updateInvoice(Invoice newInvoice) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        int invoiceIndex = applicationUser.getInvoices().indexOf(invoiceRepository.getById(newInvoice.getId()));
        Invoice invoice = null;
        if (invoiceIndex != -1){
            if (!applicationUser.getInvoices().get(invoiceIndex).equals(newInvoice)){
                applicationUser.getInvoices().remove(invoiceIndex);
                invoice = invoiceRepository.save(newInvoice);
                applicationUser.getInvoices().add(invoice);
                applicationUserService.saveAppUser(applicationUser);
            }
        }
        return invoice;
    }

    @Override
    public boolean deleteInvoice(Long id) {
        ApplicationUser applicationUser = applicationUserService.getCurrentUser();
        Invoice invoice = invoiceRepository.getById(id);
        if (applicationUser.getInvoices().contains(invoice)){
            if (applicationUser.getInvoices().remove(invoice)){
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
}
