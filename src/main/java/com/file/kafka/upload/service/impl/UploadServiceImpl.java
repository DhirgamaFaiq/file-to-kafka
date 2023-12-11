package com.file.kafka.upload.service.impl;

import com.file.kafka.upload.helper.FileHelper;
import com.file.kafka.upload.inteceptor.ValidationException;
import com.file.kafka.upload.service.iservice.UploadService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class UploadServiceImpl implements UploadService {
    private final FileHelper fileHelper;

    public UploadServiceImpl(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    @Override
    public Uni<String> breakdownFile(Map<String, List<InputPart>> multipartData) {
        try {
            for (Map.Entry<String, List<InputPart>> dataMap : multipartData.entrySet()) {
                try {
                    if (!fileHelper.checkHeaderFile(dataMap.getValue().get(0))) {
                        System.out.println("Invalid file provided");
                    }
                    if (fileHelper.getExtention(null, dataMap.getValue().get(0)).equalsIgnoreCase("csv")) {
                        fileHelper.processFileCsv(dataMap.getValue().get(0));
                        System.out.println("Is Csv");
                    }
                    if (fileHelper.getExtention(null, dataMap.getValue().get(0)).equalsIgnoreCase("vnd.ms-excel")) {
                        fileHelper.processFileExcelorSpreadsheet(dataMap.getValue().get(0),"vnd.ms-excel");
                        System.out.println("Is Xls");
                    }
                    if (fileHelper.getExtention(null, dataMap.getValue().get(0)).equalsIgnoreCase("vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                        fileHelper.processFileExcelorSpreadsheet(dataMap.getValue().get(0),"vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        System.out.println("Is Xlsx");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return Uni.createFrom().failure(new ValidationException("Failed processing request check the service log"));
                }
            }
            return Uni.createFrom().item("Done Processing");
        } catch (Exception e) {
            return Uni.createFrom().failure(new ValidationException("Failed processing request check the service log"));
        }
    }
}
