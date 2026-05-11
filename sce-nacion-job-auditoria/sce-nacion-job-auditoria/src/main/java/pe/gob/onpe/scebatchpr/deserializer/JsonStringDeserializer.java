package pe.gob.onpe.scebatchpr.deserializer;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JsonStringDeserializer extends StdDeserializer<String> {

	private static Logger logger = LoggerFactory.getLogger(JsonStringDeserializer.class);
	
	private static final long serialVersionUID = 6630612597212304159L;
	
	
	public JsonStringDeserializer() {
        this(null);
    }

    public JsonStringDeserializer(Class<?> vc) {
        super(vc);
    }


	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = p.getCodec().readTree(p);
	    ObjectMapper mapper = new ObjectMapper();
	    String json = mapper.writeValueAsString(node);
	    return json;
	}
	
}
