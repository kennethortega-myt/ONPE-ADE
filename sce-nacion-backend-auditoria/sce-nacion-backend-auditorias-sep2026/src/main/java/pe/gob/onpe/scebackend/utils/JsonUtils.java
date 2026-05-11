package pe.gob.onpe.scebackend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.scebackend.model.stae.dto.pc.PuestaCeroResponse;
import pe.gob.onpe.scebackend.model.vd.dto.pc.PuestaCeroVdResponse;

public class JsonUtils {

	private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir JSON a objeto", e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir objeto a JSON", e);
        }
    }
	
	public static PuestaCeroResponse getPuestaCeroStaeResponse(String json){
		return JsonUtils.fromJson(json, PuestaCeroResponse.class);
	}
	
	public static PuestaCeroVdResponse getPuestaCeroVdResponse(String json){
		return JsonUtils.fromJson(json, PuestaCeroVdResponse.class);
	}
	
	public static String getPuestaCeroStaeResponse(PuestaCeroResponse json){
		return JsonUtils.toJson(json);
	}
	
	public static String getPuestaCeroVdResponse(PuestaCeroVdResponse json){
		return JsonUtils.toJson(json);
	}
}
