package avl;

import com.amazonaws.services.dynamodbv2.datamodeling.JsonMarshaller;

import java.util.Map;

public class ExtMarshaller extends JsonMarshaller<Map> {

    public ExtMarshaller() {
        super(Map.class);
    }
}
