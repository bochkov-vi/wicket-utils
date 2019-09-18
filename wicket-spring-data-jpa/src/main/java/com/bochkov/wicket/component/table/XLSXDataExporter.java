package com.bochkov.wicket.component.table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.wicket.Application;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.AbstractDataExporter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * The type Xlsx data exporter.
 */
@Accessors(chain = true)
public class XLSXDataExporter extends AbstractDataExporter {

    @Getter
    @Setter
    private boolean exportHeadersEnabled = true;

    /**
     * Instantiates a new Xlsx data exporter.
     */
    public XLSXDataExporter() {
        this(Model.of("XLSX"));
    }

    /**
     * Creates a new instance with the data format name model, content type and file name extensions provided.
     *
     * @param dataFormatNameModel The model of the exported data format name.
     */


    public XLSXDataExporter(IModel<String> dataFormatNameModel) {
        super(dataFormatNameModel, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
    }

    @Override
    public <T> void exportData(IDataProvider<T> dataProvider, List<IExportableColumn<T, ?>> iExportableColumns, OutputStream outputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        writeHeaders(iExportableColumns, sheet, workbook);
        writeData(dataProvider, iExportableColumns, sheet, workbook);

        for (int i = 0; i < iExportableColumns.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        workbook.write(outputStream);
    }

    private <T> void writeHeaders(List<IExportableColumn<T, ?>> columns, XSSFSheet grid, XSSFWorkbook workbook) throws IOException {
        if (isExportHeadersEnabled()) {
            XSSFRow row = Optional.ofNullable(grid.getRow(0)).orElseGet(() -> grid.createRow(0));
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            XSSFFont font = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeight(10);
            font.setBold(true);
            cellStyle.setFont(font);
            int colNum = 0;
            for (IExportableColumn<T, ?> col : columns) {
                IModel<String> displayModel = col.getDisplayModel();
                String display = wrapModel(displayModel).getObject();
                int finalColNum = colNum;
                XSSFCell cell = Optional.ofNullable(row.getCell(colNum)).orElseGet(() -> row.createCell(finalColNum));
                cell.setCellValue(display);
                cell.setCellStyle(cellStyle);
                colNum++;
            }
        }
    }

    /**
     * Wrap model model.
     *
     * @param <T>   the type parameter
     * @param model the model
     * @return the model
     */
    protected <T> IModel<T> wrapModel(IModel<T> model) {
        return model;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> void writeData(IDataProvider<T> dataProvider, List<IExportableColumn<T, ?>> columns, XSSFSheet grid, XSSFWorkbook workbook) throws IOException {
        long numberOfRows = dataProvider.size();
        Iterator<? extends T> rowIterator = dataProvider.iterator(0, numberOfRows);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight(10);
        cellStyle.setFont(font);


        DataFormat format = workbook.createDataFormat();
        XSSFCellStyle numberCellStyle = workbook.createCellStyle();
        numberCellStyle.cloneStyleFrom(cellStyle);
        numberCellStyle.setDataFormat(format.getFormat("#0.000"));

        XSSFCellStyle dateCellStyle = null;
        int rowNum = 1;
        while (rowIterator.hasNext()) {
            T data = rowIterator.next();
            int finalRowNum = rowNum;
            XSSFRow row = Optional.ofNullable(grid.getRow(rowNum)).orElseGet(() -> grid.createRow(finalRowNum));
            int colNum = 0;
            for (IExportableColumn<T, ?> col : columns) {
                IModel<?> dataModel = col.getDataModel(dataProvider.model(data));
                int finalColNum = colNum;
                XSSFCell cell = Optional.ofNullable(row.getCell(colNum)).orElseGet(() -> row.createCell(finalColNum));
                Object value = dataModel.getObject();
                cell.setCellStyle(cellStyle);


                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Double) {
                    cell.setCellValue((Double) value);
                    cell.setCellStyle(numberCellStyle);
                } else if (value instanceof LocalDate) {
                    cell.setCellValue(Date.valueOf((LocalDate) value));
                    if (dateCellStyle == null) {
                        dateCellStyle = workbook.createCellStyle();
                        dateCellStyle.cloneStyleFrom(cellStyle);
                        dateCellStyle.setDataFormat(14);
                    }
                    cell.setCellStyle(dateCellStyle);
                } else {
                    if (value != null) {
                        Class<?> c = value.getClass();

                        String s;

                        IConverter converter = getConverterLocator().getConverter(c);

                        if (converter == null) {
                            s = value.toString();
                        } else {
                            s = converter.convertToString(value, Session.get().getLocale());
                        }
                        cell.setCellValue(String.format("%s", s));
                    }
                }

                colNum++;

            }
            rowNum++;
        }
        if (columns.size() > 0 && rowNum > 1) {
            grid.setAutoFilter(new CellRangeAddress(0, rowNum, 0, columns.size() - 1));
        }
    }

    /**
     * Gets converter locator.
     *
     * @return the converter locator
     */
    protected IConverterLocator getConverterLocator() {
        return Application.get().getConverterLocator();
    }

}
