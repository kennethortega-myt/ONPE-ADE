package pe.gob.onpe.scebackend.exeption.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BaseExcep {

    private String exceptionCategory = "";
    private String exceptionCode = "";
    private String exceptionMessage = "";
    private String exceptionDetail = "";
    private String exceptionSeverity = "";
    private Boolean success = false;
    private List<ViolationFieldConstraint> violationsFields = new ArrayList<>();
    public BaseExcep(){}
    public BaseExcep(String exceptionCategory, String exceptionCode, String exceptionMessage,
                     String exceptionDetail, String exceptionSeverity) {
        super();
        this.exceptionCategory = exceptionCategory;
        this.exceptionCode = exceptionCode;
        this.exceptionMessage = exceptionMessage;
        this.exceptionDetail = exceptionDetail;
        this.exceptionSeverity = exceptionSeverity;
    }



}