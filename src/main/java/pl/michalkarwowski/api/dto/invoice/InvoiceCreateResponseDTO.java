package pl.michalkarwowski.api.dto.invoice;

import lombok.Data;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.clients.BuyerDTO;

import java.util.Date;
import java.util.List;

@Data
public class InvoiceCreateResponseDTO {

    private Long id;
    private String number;
    private Date issueDate;
    private String issuePlace;
    private Date saleDate;
    private String paymentType;
    private AppUserDetailsDTO seller;
    private BuyerDTO buyer;
    private List<InvoicePositionDTO> positions;
    private Double priceNet;        // netto
    private Double priceGross;      // brutto
    private Double priceTax;
    private boolean showPKWIUCode;
}
