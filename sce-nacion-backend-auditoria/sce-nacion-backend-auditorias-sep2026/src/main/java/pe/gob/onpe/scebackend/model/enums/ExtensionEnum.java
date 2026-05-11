package pe.gob.onpe.scebackend.model.enums;

public enum ExtensionEnum {

	PDF("pdf", "application/pdf"),
	JPEG("jpeg","image/jpeg"),
	JPG("jpg", "image/jpg"),
	RAR("rar", "application/x-rar-compressed"),
	ZIP("zip", "application/zip"),
	PNG("png", "image/png"),
	DOC("doc", "application/msword"),
	DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	XLS("xls", "application/vnd.ms-excel"),
	XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	
	private final String extension;
	private final String typemime;
	
	ExtensionEnum(String extension, String typemime){
		this.extension = extension;
		this.typemime = typemime;
	}

	public String getExtension() {
		return extension;
	}

	public String getTypemime() {
		return typemime;
	}
	
}
