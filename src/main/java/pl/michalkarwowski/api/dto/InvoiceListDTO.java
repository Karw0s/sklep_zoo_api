package pl.michalkarwowski.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class InvoiceListDTO {

    private Long id;
    private String number;
    private Date issueDate;
    private String paymentType;
    private String buyerCompanyName;
    private Double priceNet;        // netto
    private Double priceGross;      // brutto
}
