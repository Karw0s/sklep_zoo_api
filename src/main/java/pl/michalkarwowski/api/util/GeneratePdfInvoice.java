package pl.michalkarwowski.api.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import pl.michalkarwowski.api.models.Client;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoicePosition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratePdfInvoice {

    private static final String FONT = "static/fonts/FreeSans.ttf";
    private static final String FONT_BOLD = "static/fonts/FreeSansBold.ttf";
    private static final String FONT_ITALIC = "static/fonts/FreeSansOblique.ttf";
    private static final String ORIGINAL = "Oryginał";
    private static final String COPY = "Kopia";
    private static final DecimalFormat DF = new DecimalFormat("#.00");
    private static String currency = "PLN";

    public static ByteArrayInputStream pdfInvoice(Invoice invoice, boolean originalPlusCopy) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream original = new ByteArrayOutputStream();
        ByteArrayOutputStream copy = new ByteArrayOutputStream();

        for (int i = 0; i < (originalPlusCopy ? 2 : 1); i++) {
            try {
                Document document = new Document(PageSize.A4, 36f,36f,60f,60f);
                PdfWriter pdfWriter = PdfWriter.getInstance(document, out);

                Header header;
                if (originalPlusCopy && i == 0) {
                    pdfWriter = PdfWriter.getInstance(document, original);
                    header = new Header();
                    header.setHeader(new Phrase(ORIGINAL, FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 10f)));
                    pdfWriter.setPageEvent(header);
                } else if (originalPlusCopy && i == 1) {
                    pdfWriter = PdfWriter.getInstance(document, copy);
                    header = new Header();
                    header.setHeader(new Phrase(COPY, FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 10f)));
                    pdfWriter.setPageEvent(header);
                }
                pdfWriter.setPageEvent(new PageNumeration());
                PdfPTable headerTable = new PdfPTable(2);
                headerTable.getDefaultCell().setBorder(0);
                headerTable.setWidthPercentage(30);

                Font headFont = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 13f);
                Font headerFont = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, true, 8f);
                Font invoiceNumberFont = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 15f);
                Font toPayFont = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 11f);
                Font italicFont = FontFactory.getFont(FONT_ITALIC, BaseFont.IDENTITY_H, true, 9f);

                PdfPCell hcell;
                hcell = new PdfPCell(new Phrase("Miejsce wystawienia", headerFont));
                hcell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(hcell);

                hcell = new PdfPCell(new Phrase(invoice.getIssuePlace(), headerFont));
                hcell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Data wystawienia", headerFont));
                hcell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(hcell);

                hcell = new PdfPCell(new Phrase(invoice.getIssueDate().toString(), headerFont));
                hcell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Data sprzedaży", headerFont));
                hcell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(hcell);

                hcell = new PdfPCell(new Phrase(invoice.getSaleDate().toString(), headerFont));
                hcell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(hcell);

                headerTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

                Paragraph invoiceNumber = new Paragraph(
                        new Phrase("Faktura nr " + invoice.getNumber(), invoiceNumberFont));

                PdfPTable buyerAndSellerTable = new PdfPTable(2);
                buyerAndSellerTable.setWidthPercentage(95);
                buyerAndSellerTable.setPaddingTop(7f);

                PdfPCell basCell = new PdfPCell();
                basCell.setMinimumHeight(10f);
                basCell.setBorder(Rectangle.NO_BORDER);
                buyerAndSellerTable.addCell(basCell);
                buyerAndSellerTable.addCell(basCell);

                basCell = new PdfPCell(new Phrase("Sprzedawca/podatnik", headFont));
                basCell.setBorder(Rectangle.BOTTOM);
                basCell.setPaddingBottom(5f);
                buyerAndSellerTable.addCell(basCell);

                basCell = new PdfPCell(new Phrase("Nabywca/płatnik", headFont));
                basCell.setBorder(Rectangle.BOTTOM);
                basCell.setPaddingBottom(5f);
                buyerAndSellerTable.addCell(basCell);

                basCell = new PdfPCell(createPersonDetailTable(invoice.getSeller()));
                basCell.setBorder(Rectangle.NO_BORDER);
                basCell.setUseBorderPadding(true);
                buyerAndSellerTable.addCell(basCell);


                basCell = new PdfPCell(createPersonDetailTable(invoice.getBuyer()));
                basCell.setBorder(Rectangle.NO_BORDER);
                basCell.setUseBorderPadding(true);
                buyerAndSellerTable.addCell(basCell);

                basCell = new PdfPCell();
                basCell.setMinimumHeight(10f);
                basCell.setBorder(Rectangle.NO_BORDER);
                buyerAndSellerTable.addCell(basCell);
                buyerAndSellerTable.addCell(basCell);

                Paragraph toPay = new Paragraph(
                        new Phrase(String.format("\nDO ZAPŁATY: %s %s", DF.format(invoice.getPriceGross()), currency), toPayFont));
                toPay.setPaddingTop(20f);
                String[] amount = DF.format(invoice.getPriceGross()).split(",");
                Paragraph amountInWords = new Paragraph(
                        new Phrase(String.format("Słownie: %s%s %d/100", NumberToWord.translate(Long.parseLong(amount[0])), currency, Long.parseLong(amount[1])), italicFont)
                );

                document.open();
                document.add(headerTable);
                document.add(invoiceNumber);
                document.add(buyerAndSellerTable);
                document.add(createDetailsTable(invoice));
                document.add(createPositionTable(invoice));
                document.add(createPricesSummaryTable(invoice));
                document.add(toPay);
                document.add(amountInWords);
                document.close();
            } catch (DocumentException ex) {
                Logger.getLogger(GeneratePdfInvoice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (originalPlusCopy) {
            try {
                Document document2 = new Document();
                PdfCopy pdfCopy = new PdfSmartCopy(document2, out);
                document2.open();
                PdfReader reader;

                reader = new PdfReader(original.toByteArray());
                pdfCopy.addDocument(reader);
                reader.close();

                reader = new PdfReader(copy.toByteArray());
                pdfCopy.addDocument(reader);
                reader.close();

                document2.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private static PdfPTable createPricesSummaryTable(Invoice invoice) throws DocumentException {
        PdfPTable positionSumUp = new PdfPTable(4);

        positionSumUp.setWidthPercentage(95);
        positionSumUp.setWidths(new int[]{18, 5, 5, 2});
        positionSumUp.setPaddingTop(7f);

        Font normalD = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 10f);

        PdfPCell basCell = new PdfPCell();
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase("Wartość netto", normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        basCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase(DF.format(invoice.getPriceNet()), normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        basCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase(currency, normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell();
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase("Wartość VAT", normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        basCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        positionSumUp.addCell(basCell);

        String totalTax;
        if(invoice.getPriceTax().equals(0d)) {
            totalTax = "0,00";
        } else {
            totalTax = DF.format(invoice.getPriceTax());
        }

        basCell = new PdfPCell(new Phrase(totalTax, normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        basCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase(currency, normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell();
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase("Wartość brutto", normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        basCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase(DF.format(invoice.getPriceGross()), normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        basCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        positionSumUp.addCell(basCell);

        basCell = new PdfPCell(new Phrase(currency, normalD));
        basCell.setMinimumHeight(10f);
        basCell.setBorder(Rectangle.NO_BORDER);
        positionSumUp.addCell(basCell);

        return positionSumUp;
    }

    private static PdfPTable createDetailsTable(Invoice invoice) throws DocumentException {
        Font bold = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 8f);
        Font normal = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, true, 8f);

        PdfPTable detailsTable = new PdfPTable(3);
        detailsTable.setWidths(new int[]{7, 15, 28});
        detailsTable.setWidthPercentage(95);
        detailsTable.setPaddingTop(7f);

        PdfPCell detailsCell = new PdfPCell(new Phrase("Sposób płatności", bold));
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsTable.addCell(detailsCell);

        detailsCell = new PdfPCell(new Phrase(invoice.getPaymentType(), normal));
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsTable.addCell(detailsCell);

        detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsTable.addCell(detailsCell);

        if (!invoice.getSeller().getBank().isEmpty() && !invoice.getSeller().getBankAccountNumber().isEmpty()) {
            detailsCell = new PdfPCell(new Phrase("Bank", bold));
            detailsCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(detailsCell);

            detailsCell = new PdfPCell(new Phrase(invoice.getSeller().getBank(), normal));
            detailsCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(detailsCell);

            detailsCell = new PdfPCell();
            detailsCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(detailsCell);

            detailsCell = new PdfPCell(new Phrase("Nr konta", bold));
            detailsCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(detailsCell);

            detailsCell = new PdfPCell(new Phrase(invoice.getSeller().getBankAccountNumber(), normal));
            detailsCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(detailsCell);

            detailsCell = new PdfPCell();
            detailsCell.setBorder(Rectangle.NO_BORDER);
            detailsTable.addCell(detailsCell);
        }

        return detailsTable;
    }

    private static PdfPTable createPersonDetailTable(Client person) {
        Font bold = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 10f);
        Font normal = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, true, 9f);
        PdfPTable personTable = new PdfPTable(1);

        PdfPCell pCell = new PdfPCell(new Phrase(person.getCompanyName(), bold));
        pCell.setBorder(Rectangle.NO_BORDER);
        pCell.setMinimumHeight(25f);
        personTable.addCell(pCell);

        pCell = new PdfPCell(new Phrase(person.getAddress().getStreet(), normal));
        pCell.setBorder(Rectangle.NO_BORDER);
        personTable.addCell(pCell);

        pCell = new PdfPCell(
                new Phrase(String.format("%s %s, %s", person.getAddress().getZipCode(), person.getAddress().getCity(), person.getAddress().getCountry()),
                        normal));
        pCell.setBorder(Rectangle.NO_BORDER);
        personTable.addCell(pCell);

        pCell = new PdfPCell(new Phrase("NIP: " + person.getNipNumber(), normal));
        pCell.setBorder(Rectangle.NO_BORDER);
        personTable.addCell(pCell);

        return personTable;
    }

    private static PdfPTable createPositionTable(Invoice invoice) throws DocumentException {
        Font bold = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 9f);
        Font normalF = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, true, 8f);
        Font normalD = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 8f);
        BaseColor cellsBorderColor = new BaseColor(192, 192, 192);

        List<String> headersList = new ArrayList<String>() {{
            add("LP");
            add("Nazwa towaru / usługi");
            if (invoice.isShowPKWIUCode()) {
                add("PKWiU");
            }
            add("Ilość");
            add("Cena netto");
            add("Wartość netto");
            add("VAT %");
            add("Wartość VAT");
            add("Wartość brutto");
        }};

        PdfPTable positionTable;
        if (invoice.isShowPKWIUCode()) {
            positionTable = new PdfPTable(9);
            positionTable.setWidths(new int[]{1, 9, 4, 2, 3, 3, 2, 3, 3});
        } else {
            positionTable = new PdfPTable(8);
            positionTable.setWidths(new int[]{1, 8, 2, 3, 3, 2, 3, 3});
        }


        positionTable.setWidthPercentage(95);
        positionTable.setPaddingTop(7f);


        headersList.stream().map(label -> new PdfPCell(new Phrase(label, bold))).forEach(headerCell -> {
            headerCell.setBorderColor(cellsBorderColor);
            headerCell.setBackgroundColor(new BaseColor(225, 225, 225));
            positionTable.addCell(headerCell);
        });

        invoice.getPositions().sort(Comparator.comparing(InvoicePosition::getOrdinalNumber));

        Map<String, Summary> summaryMap = new HashMap<>();


        for (InvoicePosition position : invoice.getPositions()) {
            PdfPCell positionCell = new PdfPCell(new Phrase(position.getOrdinalNumber().toString(), normalF));
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionCell.setBorderColor(cellsBorderColor);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(position.getName(), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionTable.addCell(positionCell);

            if (invoice.isShowPKWIUCode()) {
                positionCell = new PdfPCell(new Phrase(position.getPkwiuCode(), normalF));
                positionCell.setBorderColor(cellsBorderColor);
                positionTable.addCell(positionCell);
            }

            positionCell = new PdfPCell(new Phrase(String.format("%s %s", DF.format(position.getQuantity()), position.getUnitOfMeasure()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(DF.format(position.getPriceNetto()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(DF.format(position.getTotalPriceNetto()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(position.getTax(), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            String totalTax;
            if(position.getTotalPriceTax().equals(0d)) {
                totalTax = "0,00";
            } else {
                totalTax = DF.format(position.getTotalPriceTax());
            }

            positionCell = new PdfPCell(new Phrase(totalTax, normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(DF.format(position.getTotalPriceBrutto()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            if (!summaryMap.containsKey(position.getTax())) {
                summaryMap.put(position.getTax(), Summary.builder()
                        .totalPriceTax(position.getTotalPriceTax())
                        .totalPriceGross(position.getTotalPriceBrutto())
                        .totalPriceNet(position.getTotalPriceNetto())
                        .build());
            } else {
                Summary summaryTmp = summaryMap.get(position.getTax());
                summaryTmp.setTotalPriceGross(summaryTmp.getTotalPriceGross() + position.getTotalPriceBrutto());
                summaryTmp.setTotalPriceNet(summaryTmp.getTotalPriceNet() + position.getTotalPriceNetto());
                summaryTmp.setTotalPriceTax(summaryTmp.getTotalPriceTax() + position.getTotalPriceTax());
                summaryMap.replace(position.getTax(), summaryTmp);
            }
        }

        int col = invoice.isShowPKWIUCode() ? 4 : 3;
        for (int i = 0; i < col; i++) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            positionTable.addCell(cell);
        }


        PdfPCell summaryCell = new PdfPCell(new Phrase("W tym", normalF));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        Iterator summary = summaryMap.entrySet().iterator();

        while (summary.hasNext()) {
            Map.Entry entry = (Map.Entry) summary.next();
            Summary vatSummary = (Summary) entry.getValue();

            PdfPCell cell = new PdfPCell(new Phrase(DF.format(vatSummary.getTotalPriceNet()), normalF));
            cell.setBorderColor(cellsBorderColor);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(cell);

            cell = new PdfPCell(new Phrase((String) entry.getKey(), normalF));
            cell.setBorderColor(cellsBorderColor);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(cell);

            String totalTax;
            if(vatSummary.getTotalPriceTax().equals(0d)) {
                totalTax = "0,00";
            } else {
                totalTax = DF.format(vatSummary.getTotalPriceTax());
            }
            cell = new PdfPCell(new Phrase(totalTax, normalF));
            cell.setBorderColor(cellsBorderColor);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(cell);

            cell = new PdfPCell(new Phrase(DF.format(vatSummary.getTotalPriceGross()), normalF));
            cell.setBorderColor(cellsBorderColor);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(cell);

            if (summary.hasNext()) {
                int c = invoice.isShowPKWIUCode() ? 5 : 4;
                for (int i = 0; i < c; i++) {
                    PdfPCell pdfPCell = new PdfPCell();
                    pdfPCell.setBorder(Rectangle.NO_BORDER);
                    positionTable.addCell(pdfPCell);
                }
            } else {
                int c = invoice.isShowPKWIUCode() ? 4 : 3;
                for (int i = 0; i < c; i++) {
                    PdfPCell pdfPCell = new PdfPCell();
                    pdfPCell.setBorder(Rectangle.NO_BORDER);
                    positionTable.addCell(pdfPCell);
                }
            }
        }

        summaryCell = new PdfPCell(new Phrase("Razem", normalD));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        summaryCell = new PdfPCell(new Phrase(DF.format(invoice.getPriceNet()), normalD));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        summaryCell = new PdfPCell();
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        String totalTax;
        if(invoice.getPriceTax().equals(0d)) {
            totalTax = "0,00";
        } else {
            totalTax = DF.format(invoice.getPriceTax());
        }
        summaryCell = new PdfPCell(new Phrase(totalTax, normalD));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        summaryCell = new PdfPCell(new Phrase(DF.format(invoice.getPriceGross()), normalD));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);


        return positionTable;
    }

    static class PageNumeration extends PdfPageEventHelper {
        PdfTemplate total;

        private Font normal, normalSmall;

        public PageNumeration() {
            try {
                this.normal = new Font(BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 8);
                this.normalSmall = new Font(BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 6);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 12);
        }

        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable table = new PdfPTable(3);
            try {
                table.setWidths(new int[]{24, 24, 2});
                table.getDefaultCell().setFixedHeight(20);
                table.getDefaultCell().setBorder(Rectangle.TOP);
                PdfPCell cell = new PdfPCell();
                cell.setBorder(0);
                cell.setBorderWidthTop(1);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPhrase(new Phrase("Wygenerowano z AWPS \u00a9 2019 ", normal));
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(0);
                cell.setBorderWidthTop(1);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPhrase(new Phrase(String.format("Strona %d z", writer.getPageNumber()), normal));
                table.addCell(cell);

                cell = new PdfPCell(Image.getInstance(total));
                cell.setBorder(0);
                cell.setBorderWidthTop(1);
                table.addCell(cell);
                table.setTotalWidth(document.getPageSize().getWidth()
                        - document.leftMargin() - document.rightMargin());
                table.writeSelectedRows(0, -1, document.leftMargin(),
                        document.bottomMargin() - 6, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), normal),
                    2, 2, 0);
        }
    }

    static class Header extends PdfPageEventHelper {

        private Phrase header;

        public void setHeader(Phrase header) {
            this.header = header;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable table = new PdfPTable(1);
            table.getDefaultCell().setFixedHeight(20);

            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPhrase(header);
            table.addCell(cell);

            table.setTotalWidth(document.getPageSize().getWidth()
                    - document.leftMargin() - document.rightMargin());
            table.writeSelectedRows(0, -1, document.leftMargin(),
                    800, writer.getDirectContent());
        }
    }
}
