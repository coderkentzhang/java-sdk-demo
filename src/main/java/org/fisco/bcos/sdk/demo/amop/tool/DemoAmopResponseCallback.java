package org.fisco.bcos.sdk.demo.amop.tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.fisco.bcos.sdk.amop.AmopResponse;
import org.fisco.bcos.sdk.amop.AmopResponseCallback;

public class DemoAmopResponseCallback extends AmopResponseCallback {

    @Override
    public void onResponse(AmopResponse response) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (response.getErrorCode() == 102) {
            System.out.println(
                    "Step 3: Timeout, maybe your file is too large or your gave a short timeout. Add a timeout arg, topicName, isBroadcast: true/false, fileName, count, timeout");
        } else {
            if (response.getAmopMsgIn() != null) {
                System.out.println(
                        "Step 3: Get response, time: "
                                + df.format(LocalDateTime.now())
                                + " response: { errorCode:"
                                + response.getErrorCode()
                                + " error:"
                                + response.getErrorMessage()
                                + " seq:"
                                + response.getMessageID()
                                + " content:"
                                + new String(response.getAmopMsgIn().getContent())
                                + " }");
            } else {
                System.out.println(
                        "Step 3: Get response, time: "
                                + df.format(LocalDateTime.now())
                                + " response: { errorCode:"
                                + response.getErrorCode()
                                + " error:"
                                + response.getErrorMessage()
                                + " seq:"
                                + response.getMessageID());
            }
        }
    }
}
