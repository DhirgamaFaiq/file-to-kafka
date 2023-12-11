package com.file.kafka.upload.service.iservice;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import java.util.List;
import java.util.Map;

public interface UploadService {
  Uni<String> breakdownFile(Map<String, List<InputPart>> multipartData);
}
