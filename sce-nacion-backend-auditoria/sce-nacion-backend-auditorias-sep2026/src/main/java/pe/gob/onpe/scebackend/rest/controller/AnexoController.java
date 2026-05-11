package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.request.AnexosRequestDto;
import pe.gob.onpe.scebackend.model.service.IAnexosService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.io.IOException;

@RestController
@RequestMapping("/anexos")
public class AnexoController {

    private static final String ATTACHMENT = "attachment";

    private final IAnexosService anexosService;

    public AnexoController(IAnexosService anexosService) {
        this.anexosService = anexosService;
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/jee")
    public  ResponseEntity<byte[]> anexo1(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_ZIP_ANEXO1);
        return new ResponseEntity<>(this.anexosService.anexo1(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/votos")
    public ResponseEntity<byte[]> votos(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_TXT_VOTOS);
        return new ResponseEntity<>(this.anexosService.votos(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/votosCifras")
    public ResponseEntity<byte[]> votosCifras(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_TXT_VOTOS_CIFRA);
        return new ResponseEntity<>(this.anexosService.votosCifras(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/votosCifraInicial")
    public ResponseEntity<byte[]> votosCifraInicial(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_TXT_VOTOS_CIFRA);
        return new ResponseEntity<>(this.anexosService.votosCifrasInicial(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/mesasNoinstaladas")
    public ResponseEntity<byte[]> mesasNoinstaladas(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_TXT_MESAS_NO_INSTALADAS);
        return new ResponseEntity<>(this.anexosService.mesasNoinstaladas(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/tablaActas")
    public ResponseEntity<byte[]> tablaActas(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_TXT_TABLA_ACTAS);
        return new ResponseEntity<>(this.anexosService.tablaActas(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/maestraOrganizacionPolitica")
    public ResponseEntity<byte[]> maestraOrganizacionPolitica(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_ZIP_MAESTRAS_ORG);
        return new ResponseEntity<>(this.anexosService.maestraOrganizacionPolitica(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/maestroUbigeo")
    public ResponseEntity<byte[]> maestroUbigeo(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_TXT_UBIGEO);
        return new ResponseEntity<>(this.anexosService.maestroUbigeo(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/odpe")
    public ResponseEntity<byte[]> odpe(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_TXT_ODPE);
        return new ResponseEntity<>(this.anexosService.odpe(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/anexo2")
    public ResponseEntity<byte[]> anexo2(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_ZIP_ANEXO2);
        return new ResponseEntity<>(this.anexosService.anexo2(requestDto).getByteFile(), headers, HttpStatus.OK);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping(value = "/all")
    public ResponseEntity<byte[]> all(@RequestBody AnexosRequestDto requestDto) throws GenericException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, SceConstantes.NAME_ZIP_ALL);
        return new ResponseEntity<>(this.anexosService.all(requestDto).getByteFile(), headers, HttpStatus.OK);
    }
}
