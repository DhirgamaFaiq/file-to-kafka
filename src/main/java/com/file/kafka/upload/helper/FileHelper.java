package com.file.kafka.upload.helper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.file.kafka.broker.worker.Producer;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@ApplicationScoped
public class FileHelper {
    private Producer producer;

    public FileHelper(Producer producer) {
        this.producer = producer;
    }
    public boolean checkHeaderFile(InputPart fileData) {

        try {
            final MultivaluedMap<String, String> header = fileData.getHeaders();
            System.out.println(getFileName(header, null));
            System.out.println(getExtention(header, null));
            if (getFileName(header, null).equalsIgnoreCase("Unknown")) {
                return false;
            }
            if (getExtention(header, null).equalsIgnoreCase("Unknown")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getFileName(MultivaluedMap<String, String> header, InputPart fileData) {
        try {
            header = !Objects.isNull(fileData) ? fileData.getHeaders() : header;
            if (!Objects.isNull(header)) {
                String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
                for (String filename : contentDisposition) {
                    if (filename.trim().startsWith("filename")) {
                        String[] name = filename.split("=");
                        String finalFileName = name[1].trim().replaceAll("\"", "");
                        if (finalFileName.equalsIgnoreCase("")) {
                            return "Unknown";
                        }
                        return finalFileName;
                    }
                }
            }
            return "Unknown";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public String getExtention(MultivaluedMap<String, String> header, InputPart fileData) {
        try {
            header = !Objects.isNull(fileData) ? fileData.getHeaders() : header;
            if (!Objects.isNull(header)) {
                String[] extention = !Objects.isNull(header.getFirst("Content-Type"))
                        && !header.getFirst("Content-Type").isEmpty() ?
                        header.getFirst("Content-Type").split("/")
                        : null;
                System.out.println(Arrays.toString(extention));
                if (!Objects.isNull(extention)
                        && !Objects.isNull(extention[1])
                        && (extention[1].equalsIgnoreCase("vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        || extention[1].equalsIgnoreCase("vnd.ms-excel")
                        || extention[1].equalsIgnoreCase("csv"))) {
                    return extention[1];
                }
            }
            return "Unknown";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public boolean processFileCsv(InputPart dataCsv) {
        InputStream csvFile = null;
        CSVReader reader = null;
        try {
            csvFile = dataCsv.getBody(InputStream.class, null);
            try {
                reader = new CSVReaderBuilder(new InputStreamReader(csvFile)).withSkipLines(1).build();
                final String[] header = reader.readNext();
                try {
                    String[] nextRecord;
                    while ((nextRecord = reader.readNext()) != null) {
                        List<String> dataRow = new ArrayList<String>();
                        /**
                        *Index Started with 1,because in my sample i have number*
                        *so i remove and skip it
                        **/
                        for (int i = 1; i < header.length; i++) {
                            dataRow.add(nextRecord[i]);
                        }
                        System.out.println(String.join(", ", dataRow));
                        producer.send(String.join(", ", dataRow));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                } finally {
                    csvFile.close();
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                csvFile.close();
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean processFileExcelorSpreadsheet(InputPart dataExcel, String typeExcel) {
        InputStream excelFile = null;
        Workbook workbook = null;
        try {
            excelFile = dataExcel.getBody(InputStream.class, null);
            try {
                workbook = typeExcel.contains("excel") ? WorkbookFactory.create(excelFile) : new XSSFWorkbook(excelFile);
                for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                    Sheet sheet = workbook.getSheetAt(sheetIndex);
                    Iterator<Row> rowIterator = sheet.iterator();
                    try {
                        while (rowIterator.hasNext()) {
                            Row row = rowIterator.next();
                            if (row.getRowNum() == 0) {
                                continue;
                            }
                            List<String> dataRow = new ArrayList<String>();
                            /**
                             *Index Started with 1,because in my sample i have number*
                             *so i remove and skip it
                             **/
                            for (int cellIndex = 1; cellIndex < row.getLastCellNum(); cellIndex++) {
                                Cell cell = row.getCell(cellIndex);
                                String cellValue = cell != null ? cell.toString() : "";
                                dataRow.add(cellValue);
                            }
                            System.out.println(String.join(", ", dataRow));
                            producer.send(String.join(", ", dataRow));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    } finally {
                        excelFile.close();
                        workbook.close();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                excelFile.close();
                workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
