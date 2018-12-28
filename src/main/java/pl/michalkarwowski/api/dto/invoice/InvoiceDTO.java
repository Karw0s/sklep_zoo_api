package pl.michalkarwowski.api.dto.invoice;

import lombok.Data;
import pl.michalkarwowski.api.dto.AppUserDetailsDTO;
import pl.michalkarwowski.api.dto.clients.BuyerDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Data
public class InvoiceDTO {

    @NotNull
    @Pattern(regexp = "^[1-9]+[0-9]*/(1[0-2]|0[1-9])/[0-9]{4}$", message = "Invalid Invoice number")
    private String number;
    @NotNull
    private Date issueDate;
    @NotNull
    private String issuePlace;
    @NotNull
    private Date saleDate;
    @NotNull
    private String paymentType;
    @NotNull
    private AppUserDetailsDTO seller;
    @NotNull
    private BuyerDTO buyer;
    @NotNull
    private List<InvoicePositionDTO> positions;
    @NotNull
    private Double priceNet;        // netto
    @NotNull
    private Double priceGross;      // brutto
    @NotNull
    private Double priceTax;
}
