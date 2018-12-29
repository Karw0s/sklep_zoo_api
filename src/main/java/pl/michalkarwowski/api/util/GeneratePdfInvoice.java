package pl.michalkarwowski.api.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import pl.michalkarwowski.api.models.Client;
import pl.michalkarwowski.api.models.Invoice;
import pl.michalkarwowski.api.models.InvoicePosition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeneratePdfInvoice {

    private static final String FONT = "static/fonts/FreeSans.ttf";
    private static final String FONT_BOLD = "static/fonts/FreeSansBold.ttf";

    public static ByteArrayInputStream pdfInvoice(Invoice invoice, boolean orginalPlusCopy) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DecimalFormat df = new DecimalFormat("#.00");


        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setPageEvent(new PageNumeration());
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.getDefaultCell().setBorder(0);
            headerTable.setWidthPercentage(30);

            Font headFont = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 13f);
            Font headerFont = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, true, 8f);
            Font invoiceNumberFont = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 15f);
            Font toPayFont = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 11f);

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


            PdfPTable positionSumUp = new PdfPTable(3);
            positionSumUp.setWidthPercentage(95);
            if(invoice.isShowPKWIUCode())
                positionSumUp.setWidths(new int[]{16, 4, 10});
            else
                positionSumUp.setWidths(new int[]{14, 4, 7});

            positionSumUp.setPaddingTop(7f);

            basCell = new PdfPCell();
            basCell.setMinimumHeight(10f);
            basCell.setBorder(Rectangle.NO_BORDER);
            positionSumUp.addCell(basCell);

            basCell = new PdfPCell(new Phrase("Wartość netto"));
            basCell.setMinimumHeight(10f);
            basCell.setBorder(Rectangle.NO_BORDER);
            positionSumUp.addCell(basCell);

            Paragraph toPay = new Paragraph(
                    new Phrase(String.format("\nDO ZAPŁATY: %s zł", df.format(invoice.getPriceGross())), toPayFont));
            toPay.setPaddingTop(20f);

            document.open();
            document.add(headerTable);
            document.add(invoiceNumber);
            document.add(buyerAndSellerTable);
            document.add(createDetailsTable(invoice));
            document.add(createPositionTable(invoice));
            document.add(positionSumUp);
            document.add(toPay);
            document.close();
        } catch (DocumentException ex) {
            Logger.getLogger(GeneratePdfInvoice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ByteArrayInputStream(out.toByteArray());

    }

    private static PdfPTable createDetailsTable(Invoice invoice) throws DocumentException {
        Font bold = FontFactory.getFont(FONT_BOLD, BaseFont.IDENTITY_H, true, 8f);
        Font normal = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, true, 8f);

        PdfPTable detailsTable = new PdfPTable(3);
        detailsTable.setWidths(new int[]{7, 15, 28});
        detailsTable.setWidthPercentage(95);
        detailsTable.setPaddingTop(7f);

        PdfPCell detailsCell = new PdfPCell(new Phrase("Sposób zapłaty", bold));
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsTable.addCell(detailsCell);

        detailsCell = new PdfPCell(new Phrase(invoice.getPaymentType(), normal));
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsTable.addCell(detailsCell);

        detailsCell = new PdfPCell();
        detailsCell.setBorder(Rectangle.NO_BORDER);
        detailsTable.addCell(detailsCell);

        if (!invoice.getSeller().getBank().isEmpty() && !invoice.getSeller().getBankAccountNumber().isEmpty())
        {
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
        DecimalFormat df = new DecimalFormat("#.00");

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

            positionCell = new PdfPCell(new Phrase(String.format("%s %s", df.format(position.getQuantity()), position.getUnitOfMeasure()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(df.format(position.getPriceNetto()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(df.format(position.getTotalPriceNetto()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(position.getTax(), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(df.format(position.getTotalPriceTax()), normalF));
            positionCell.setBorderColor(cellsBorderColor);
            positionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(positionCell);

            positionCell = new PdfPCell(new Phrase(df.format(position.getTotalPriceBrutto()), normalF));
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

            PdfPCell cell = new PdfPCell(new Phrase(df.format(vatSummary.getTotalPriceNet()), normalF));
            cell.setBorderColor(cellsBorderColor);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(cell);

            cell = new PdfPCell(new Phrase((String) entry.getKey(), normalF));
            cell.setBorderColor(cellsBorderColor);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(cell);

            cell = new PdfPCell(new Phrase(df.format(vatSummary.getTotalPriceTax()), normalF));
            cell.setBorderColor(cellsBorderColor);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            positionTable.addCell(cell);

            cell = new PdfPCell(new Phrase(df.format(vatSummary.getTotalPriceGross()), normalF));
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

        summaryCell = new PdfPCell(new Phrase(df.format(invoice.getPriceNet()), normalD));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        summaryCell = new PdfPCell();
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        summaryCell = new PdfPCell(new Phrase(df.format(invoice.getPriceTax()), normalD));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);

        summaryCell = new PdfPCell(new Phrase(df.format(invoice.getPriceGross()), normalD));
        summaryCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        summaryCell.setBorderColor(cellsBorderColor);
        positionTable.addCell(summaryCell);


        return positionTable;
    }

//    public static void main(String[] args) {
//        Map<Integer, String> items = new HashMap<Integer, String>() {{
//            put(1, "LP");
//            put(2, "Nazwa towaru / usługi");
//            if(invoice.isShowPKWIUCode()) {
//                put(3, "Symbol PKWiU");
//            }
//            put(4, "Ilość");
//            put(5, "Cena netto");
//            put(6, "Wartość netto");
//            put(7, "VAT %");
//            put(8, "Wartość VAT");
//            put(10, "Wartość brutto");
//        }};
//
//
//        items.forEach((k, v) -> System.out.println("Item : " + k + " Count : " + v));
//    }

    static class PageNumeration extends PdfPageEventHelper {
        /**
         * The template with the total number of pages.
         */
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

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         *com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 12);
        }

        /**
         * Adds a header to every page
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
         *com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
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
                        document.bottomMargin() + 1, writer.getDirectContent());
            } catch (DocumentException de) {
                throw new ExceptionConverter(de);
            }
        }

        /**
         * Fills out the total number of pages before the document is closed.
         *
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(
         *com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                    new Phrase(String.valueOf(writer.getPageNumber()), normal),
                    2, 2, 0);
        }
    }

//    static class Header extends PdfPageEventHelper {
//        Font font;
//        PdfTemplate t;
//        Image total;
//
//        @Override
//        public void onOpenDocument(PdfWriter writer, Document document) {
//            t = writer.getDirectContent().createTemplate(30, 16);
//            try {
//                total = Image.getInstance(t);
//                total.setRole(PdfName.ARTIFACT);
//                font =  new Font(BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 10);
//            } catch (DocumentException de) {
//                throw new ExceptionConverter(de);
//            } catch (IOException ioe) {
//                throw new ExceptionConverter(ioe);
//            }
//        }
//
//        @Override
//        public void onEndPage(PdfWriter writer, Document document) {
//            PdfPTable table = new PdfPTable(3);
//            try {
//                table.setWidths(new int[]{24, 24, 2});
//                table.getDefaultCell().setFixedHeight(20);
//                table.getDefaultCell().setBorder(Rectangle.BOTTOM);
//                table.addCell(new Phrase("Wygenerowano z AWPS", font));
//                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
//                table.addCell(new Phrase(String.format("Strona %d z", writer.getPageNumber()), font));
//                PdfPCell cell = new PdfPCell(total);
//                cell.setBorder(Rectangle.BOTTOM);
//                table.addCell(cell);
//                PdfContentByte canvas = writer.getDirectContent();
//                canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
//                table.writeSelectedRows(0, -1, 36, 30, canvas);
//                canvas.endMarkedContentSequence();
//            } catch (DocumentException de) {
//                throw new ExceptionConverter(de);
//            }
//        }
//
//        @Override
//        public void onCloseDocument(PdfWriter writer, Document document) {
//            ColumnText.showTextAligned(t, Element.ALIGN_LEFT,
//                    new Phrase(String.valueOf(writer.getPageNumber()), font),
//                    2, 4, 0);
//        }
//    }
}
