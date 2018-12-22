package pl.michalkarwowski.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Builder
@Data
public class InvoiceListDTO {

    private Long id;
    private String number;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @JsonFormat(pattern = "dd.MM.yyyy", shape = JsonFormat.Shape.STRING)
    private Date issueDate;
    private String paymentType;
    private String buyerCompanyName;
    private Double priceNet;        // netto
    private Double priceGross;      // brutto
}
