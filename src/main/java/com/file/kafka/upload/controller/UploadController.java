package com.file.kafka.upload.controller;

import com.file.kafka.upload.dto.BaseResponse;
import com.file.kafka.upload.dto.ErrorsDetails;
import com.file.kafka.upload.inteceptor.ValidationException;
import com.file.kafka.upload.service.iservice.UploadService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;


@Path("/upload")
@RequestScoped
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> uploadResource(@MultipartForm MultipartFormDataInput input){
       return uploadService.breakdownFile(input.getFormDataMap())
               .onItem()
               .ifNotNull()
               .transform(r->{
                   final ErrorsDetails error = ErrorsDetails.builder()
                           .code("")
                           .title("")
                           .details("")
                           .build();
                   final BaseResponse response =  BaseResponse.builder()
                           .message("Successfully access resources")
                           .errorsDetails(error)
                           .build();
                   final Jsonb jsonb = JsonbBuilder.create();
                    System.out.println(jsonb.toJson(response));
                    return Response.status(Status.ACCEPTED).entity(jsonb.toJson(response)).build();
               })
               .onItem()
               .ifNull()
               .failWith(new ValidationException("Something went wrong"))
               .onFailure()
               .recoverWithUni(f->{
                   final ErrorsDetails error = ErrorsDetails.builder()
                           .code("400")
                           .title("Bad Request")
                           .details("Problem with resources")
                           .build();
                   final BaseResponse response =  BaseResponse.builder()
                           .message("Failed Processing")
                           .errorsDetails(error)
                           .build();
                   final Jsonb jsonb = JsonbBuilder.create();
                   System.out.println(jsonb.toJson(response));
                  return Uni.createFrom().item(Response.status(Status.BAD_REQUEST).entity(response).build());
               });
    }
}
