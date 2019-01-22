package pl.michalkarwowski.api.util;

import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoiceNextNumber;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class GenerateInvoiceNumber {

    public static InvoiceNextNumber generateNextInvoiceNumber(Date issueDate, String invoiceNumber, List<InvoiceNextNumber> invoiceNumberList, List<Invoice> userInvoices) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(issueDate);

        InvoiceNextNumber nextNumberByIssueDate = invoiceNumberList.stream()
                .filter(invoiceNextNumber -> calendar.get(Calendar.YEAR) == invoiceNextNumber.getYear() && calendar.get(Calendar.MONTH) + 1 == invoiceNextNumber.getMonth())
                .findAny().orElse(null);

        String[] numberSplit = new String[0];

        if (nextNumberByIssueDate != null) {
            // invoiceNumber is matching issueDate

            if (nextNumberByIssueDate.getLastInvoiceNumber().equals(invoiceNumber)) {   // first invoice in issueDate month

                if (nextNumberByIssueDate.getNextInvoiceNumber() == null) {
                    numberSplit = nextNumberByIssueDate.getLastInvoiceNumber().split("/");
                } else {
                    numberSplit = nextNumberByIssueDate.getNextInvoiceNumber().split("/");
                }

                numberSplit[0] = Integer.toString(Integer.parseInt(numberSplit[0]) + 1);
                String nextNumber = String.join("/", numberSplit);
                nextNumberByIssueDate.setLastInvoiceNumber(String.join("/", numberSplit));
                nextNumberByIssueDate.setNextInvoiceNumber(nextNumber);
                nextNumberByIssueDate.setLastUpdate(new Date());
                return nextNumberByIssueDate;

            } else if (nextNumberByIssueDate.getNextInvoiceNumber().equals(invoiceNumber)) {    // next invoice in issueDate month

                numberSplit = nextNumberByIssueDate.getNextInvoiceNumber().split("/");
                nextNumberByIssueDate.setLastInvoiceNumber(nextNumberByIssueDate.getNextInvoiceNumber());
                nextNumberByIssueDate.setNextInvoiceNumber(Integer.parseInt(numberSplit[0]) + 1 + "/" + numberSplit[1] + "/" + numberSplit[2]);
                return nextNumberByIssueDate;
            } else {    // invoiceNumber do not match next invoice number in issueDate month

                if (nextNumberByIssueDate.getNextInvoiceNumber() == null) {
                    numberSplit = nextNumberByIssueDate.getLastInvoiceNumber().split("/");
                } else {
                    numberSplit = nextNumberByIssueDate.getNextInvoiceNumber().split("/");
                }

                String[] invoiceNumberSplitted = invoiceNumber.split("/");
                int year = Integer.parseInt(invoiceNumberSplitted[2]);
                int month = Integer.parseInt(invoiceNumberSplitted[1]);
                int invoiceNum = Integer.parseInt(invoiceNumberSplitted[0]);
                String monthString = month < 10 ? "0" + Integer.toString(month) : Integer.toString(month);

                if (Integer.parseInt(numberSplit[0]) < invoiceNum) {
                    nextNumberByIssueDate.setLastInvoiceNumber(invoiceNumber);

                    boolean exists = true;
                    while (exists) {
                        int num = invoiceNum;
                        Invoice invoice = userInvoices.stream().filter(i -> i.getNumber().equals(num + 1 + "/" + monthString + "/" + year)).findAny().orElse(null);
                        if (invoice != null) {
                            invoiceNum += 1;
                        } else {
                            exists = false;
                        }
                    }

                    nextNumberByIssueDate.setNextInvoiceNumber(invoiceNum + 1 + "/" + monthString + "/" + year);
                    nextNumberByIssueDate.setLastUpdate(new Date());
                    return nextNumberByIssueDate;
                }

            }
        }
        return null;
    }
}
