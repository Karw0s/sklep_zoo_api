package pl.michalkarwowski.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoiceNextNumber;
import pl.michalkarwowski.api.util.GenerateInvoiceNumber;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class GenerateInvoiceNumberTests {

    private Date issueDate;
    private String invoiceNumber = "1/01/2019";
    private List<InvoiceNextNumber> invoiceNumberList = new ArrayList<>();
    private List<Invoice> userInvoices = new ArrayList<>();

    @Before
    public void setUp() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2019, Calendar.JANUARY, 14);
        InvoiceNextNumber invoiceNextNumber = InvoiceNextNumber.builder()
                .id(1L)
                .nextInvoiceNumber(null)
                .lastInvoiceNumber("1/01/2019")
                .year(2019)
                .month(1)
                .lastUpdate(calendar.getTime())
                .build();


        invoiceNumberList.add(invoiceNextNumber);

        invoiceNextNumber = InvoiceNextNumber.builder()
                .id(2L)
                .nextInvoiceNumber("2/02/2019")
                .lastInvoiceNumber("1/02/2019")
                .year(2019)
                .month(2)
                .lastUpdate(calendar.getTime())
                .build();

        invoiceNumberList.add(invoiceNextNumber);
        issueDate = calendar.getTime();
    }

    @Test
    public void firstInvoiceNumber() {
        InvoiceNextNumber invoiceNextNumber = GenerateInvoiceNumber.generateNextInvoiceNumber(issueDate, invoiceNumber, invoiceNumberList, userInvoices);
        assertEquals("2/01/2019", invoiceNextNumber.getNextInvoiceNumber());
    }

    @Test
    public void nextInvoiceNumber() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2019, Calendar.FEBRUARY, 14);
        InvoiceNextNumber invoiceNextNumber = GenerateInvoiceNumber.generateNextInvoiceNumber(
                calendar.getTime(),
                "2/02/2019",
                invoiceNumberList,
                userInvoices);

        assertEquals("3/02/2019", invoiceNextNumber.getNextInvoiceNumber());
        assertEquals("2/02/2019", invoiceNextNumber.getLastInvoiceNumber());
    }

    @Test
    public void nextInvoiceNumberGraterThanLast() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(2019, Calendar.FEBRUARY, 14);
        InvoiceNextNumber invoiceNextNumber = GenerateInvoiceNumber.generateNextInvoiceNumber(
                calendar.getTime(),
                "31/02/2019",
                invoiceNumberList,
                userInvoices);

        assertEquals("32/02/2019", invoiceNextNumber.getNextInvoiceNumber());
        assertEquals("31/02/2019", invoiceNextNumber.getLastInvoiceNumber());
    }
}
