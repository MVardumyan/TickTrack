package common.helpers;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrack.proto.Msg;

/**
 * Class contains util methods for parsing protobuf Msg object to json body
 * and vice versa
 */
public final class CustomJsonParser {
    private final static Logger logger = LoggerFactory.getLogger(CustomJsonParser.class);

    /**
     * method parses String json body to Msg
     * @param jsonRequest json request body
     * @return Msg - success; null - failure
     */
    public static Msg jsonToProtobuf(String jsonRequest) {
        try {
            Msg.Builder builder = Msg.newBuilder();

            JsonFormat.parser().merge(jsonRequest, builder);
            return builder.build();
        } catch (InvalidProtocolBufferException e) {
            logger.error("Unable to parse json to protobuf", e);
            return null;
        }
    }

    /**
     * methods parses Msg to String json body
     * @param message Msg object
     * @return json text value - success; Msg.CommonResponse - failure
     */
    public static String protobufToJson(Msg message) {
        try {
            return JsonFormat.printer().print(message);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Unable to parse protobuf to json", e);
            return "{\n\"commonResponse\": {\n\"responseText\": \"Itnernal Error: Unable to parse response protobuf to json\",\n\"responseType\": \"Failure\"\n}\n}";
        }
    }

}
