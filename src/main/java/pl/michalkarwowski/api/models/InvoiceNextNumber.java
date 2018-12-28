package pl.michalkarwowski.api.models;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
@Builder
public class InvoiceNextNumber {

    @Id
    @GeneratedValue
    private Long id;
    private String nextInvoiceNumber;
    private String lastInvoiceNumber;
    private Integer month;
    private Integer year;
    private Date lastUpdate;
}
