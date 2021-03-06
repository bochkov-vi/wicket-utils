package com.bochkov.wicket.component.table;

import org.apache.commons.compress.utils.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.ExportToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IDataExporter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.export.IExportableColumn;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Xlsx data export link.
 */
public class XLSXDataExportLink extends ResourceLink<Void> {

    /**
     * The Table.
     */
    DataTable table;

    /**
     * Instantiates a new Xlsx data export link.
     *
     * @param id       the id
     * @param table    the table
     * @param fileName the file name
     */
    public XLSXDataExportLink(String id, DataTable table, String fileName) {
        this(id, table, new XLSXDataExporter(table), fileName);
    }

    /**
     * Instantiates a new Xlsx data export link.
     *
     * @param id       the id
     * @param table    the table
     * @param exporter the exporter
     * @param fileName the file name
     */
    protected XLSXDataExportLink(String id, DataTable table, IDataExporter exporter, String fileName) {

        super(id, new ResourceStreamResource() {
            @Override
            protected IResourceStream getResourceStream(Attributes attributes) {
                return new ExportToolbar.DataExportResourceStreamWriter(exporter, table);
            }

            @Override
            protected void configureCache(ResourceResponse data, Attributes attributes) {
                data.setCacheDuration(Duration.NONE);
                super.configureCache(data, attributes);
            }
        }.setFileName(fileName + "." + exporter.getFileNameExtension()));

       // setEscapeModelStrings(false);
       // setBody(Model.of("<span class='fa fa-file-excel-o'></span>"));
        this.table = table;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(table.getRowCount() > 0);
    }

    /**
     * The type Data export resource stream writer.
     */
    public static class DataExportResourceStreamWriter extends AbstractResourceStreamWriter {

        private final IDataExporter dataExporter;

        private final DataTable<?, ?> dataTable;

        /**
         * Instantiates a new Data export resource stream writer.
         *
         * @param dataExporter the data exporter
         * @param dataTable    the data table
         */
        public DataExportResourceStreamWriter(IDataExporter dataExporter, DataTable<?, ?> dataTable) {
            this.dataExporter = dataExporter;
            this.dataTable = dataTable;
        }

        @Override
        public void write(OutputStream output)
                throws IOException {
            exportData(dataTable, dataExporter, output);
        }

        @Override
        public String getContentType() {
            return dataExporter.getContentType();
        }

        private <T, S> void exportData(DataTable<T, S> dataTable, IDataExporter dataExporter, OutputStream outputStream)
                throws IOException {
            IDataProvider<T> dataProvider = dataTable.getDataProvider();
            List<IExportableColumn<T, ?>> exportableColumns = Lists.newArrayList();
            for (IColumn<T, S> col : dataTable.getColumns()) {
                if (col instanceof IExportableColumn) {
                    exportableColumns.add((IExportableColumn<T, ?>) col);
                }
            }
            dataExporter.exportData(dataProvider, exportableColumns, outputStream);
        }
    }
}
