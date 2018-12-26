package pl.michalkarwowski.api.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String number;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @JsonFormat(pattern = "dd.MM.yyyy", shape = JsonFormat.Shape.STRING)
    private Date issueDate;
    private String issuePlace;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @JsonFormat(pattern = "dd.MM.yyyy", shape = JsonFormat.Shape.STRING)
    private Date saleDate;
    private String paymentType;
    @ManyToOne
    private Client seller;
    @ManyToOne
    private Client buyer;
    @OneToMany(mappedBy = "invoice")
    private List<InvoicePosition> positions;
    private Double priceNet;        // netto
    private Double priceGross;      // brutto
    private Double priceTax;




}
