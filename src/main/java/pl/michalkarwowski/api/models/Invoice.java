package pl.michalkarwowski.api.models;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
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
    private Date issueDate;
    private String issuePlace;
    @Temporal(TemporalType.DATE)
    private Date saleDate;
    private String paymentType;
    @ManyToOne
    private AppUserDetails seller;
    @ManyToOne
    private Client buyer;
    private Integer originalBuyerId;
    @OneToMany
    private List<InvoicePosition> positions = new ArrayList<>();
    private Double priceNet;        // netto
    private Double priceGross;      // brutto
    private Double priceTax;
    private Date created;
    private Date lastUpdated;
    private boolean showPKWIUCode;




}
