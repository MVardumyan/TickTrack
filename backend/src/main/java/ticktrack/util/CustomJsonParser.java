package ticktrack.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrack.proto.Msg;

public final class CustomJsonParser {
    private final static Logger logger = LoggerFactory.getLogger(CustomJsonParser.class);

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

    public static String protobufToJson(Msg message) {
        try {
            return JsonFormat.printer().print(message);
        } catch (InvalidProtocolBufferException e) {
            logger.error("Unable to parse protobuf to json", e);
            return "{\n\"commonResponse\": {\n\"responseText\": \"Itnernal Error: Unable to parse response protobuf to json\",\n\"responseType\": \"Failure\"\n}\n}";
        }
    }

}
