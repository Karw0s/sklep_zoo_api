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
    private String nr;
    @OneToMany(mappedBy = "invoice")
    private List<InvoicePosition> invoicePosition = new ArrayList<>();
    private Date dateOfIssue;


}
