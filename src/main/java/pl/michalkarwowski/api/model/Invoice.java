package pl.michalkarwowski.api.model;

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
    private String id;

    private String number;
    private Date saleDate;
    private Date issueDate;
    private String paymentType;
    @ManyToOne
    private Client seller;
    @ManyToOne
    private Client buyer;
    @OneToMany(mappedBy = "invoice")
    private List<InvoicePosition> positions = new ArrayList<>();




}
