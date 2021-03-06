package org.fisco.bcos.sdk.demo.amop.tool;

import static org.fisco.bcos.sdk.demo.amop.tool.FileToByteArrayHelper.byteCat;
import static org.fisco.bcos.sdk.demo.amop.tool.FileToByteArrayHelper.getFileByteArray;
import static org.fisco.bcos.sdk.demo.amop.tool.FileToByteArrayHelper.intToByteArray;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.amop.Amop;
import org.fisco.bcos.sdk.amop.AmopMsgOut;
import org.fisco.bcos.sdk.amop.topic.TopicType;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.client.protocol.response.Peers;
import org.fisco.bcos.sdk.crypto.keystore.KeyTool;
import org.fisco.bcos.sdk.crypto.keystore.PEMKeyStore;

public class AmopPublisherPrivateFile {
    private static final int parameterNum = 6;
    private static String publisherFile =
            AmopPublisherPrivateFile.class
                    .getClassLoader()
                    .getResource("amop/config-publisher-for-test.toml")
                    .getPath();

    /**
     * @param args topicName, pubKey1, pubKey2, isBroadcast: true/false, fileName, count, timeout.
     *     if only one public key please fill pubKey2 with null
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length < parameterNum) {
            System.out.println("param: target topic total number of request");
            return;
        }
        String topicName = args[0];
        String pubkey1 = args[1];
        String pubkey2 = args[2];
        Boolean isBroadcast = Boolean.valueOf(args[3]);
        String fileName = args[4];
        Integer count = Integer.parseInt(args[5]);
        Integer timeout = 6000;
        if (args.length > 6) {
            timeout = Integer.parseInt(args[6]);
        }
        BcosSDK sdk = BcosSDK.build(publisherFile);
        Amop amop = sdk.getAmop();
        // todo setup topic

        System.out.println("3s ...");
        Thread.sleep(1000);
        System.out.println("2s ...");
        Thread.sleep(1000);
        System.out.println("1s ...");
        Thread.sleep(1000);

        if (!subscribed(sdk, topicName)) {
            System.out.println("No subscriber, exist.");
        }

        System.out.println("start test");
        System.out.println("===================================================================");
        System.out.println("set up private topic");
        List<KeyTool> kml = new ArrayList<>();
        KeyTool km1 = new PEMKeyStore(pubkey1);
        kml.add(km1);
        if (!pubkey2.equals("null")) {
            KeyTool km2 = new PEMKeyStore(pubkey2);
            kml.add(km2);
        }
        amop.publishPrivateTopic(topicName, kml);
        System.out.println("wait until finish private topic verify");
        System.out.println("3s ...");
        Thread.sleep(1000);
        System.out.println("2s ...");
        Thread.sleep(1000);
        System.out.println("1s ...");
        Thread.sleep(1000);

        int flag = -128;
        byte[] byteflag = intToByteArray(flag);
        int filelength = fileName.length();
        byte[] bytelength = intToByteArray(filelength);
        byte[] bytefilename = fileName.getBytes();
        byte[] contentfile = getFileByteArray(new File(fileName));
        byte[] content = byteCat(byteCat(byteCat(byteflag, bytelength), bytefilename), contentfile);

        for (Integer i = 0; i < count; ++i) {
            Thread.sleep(2000);
            AmopMsgOut out = new AmopMsgOut();
            out.setType(TopicType.PRIVATE_TOPIC);
            out.setContent(content);
            out.setTimeout(timeout);
            out.setTopic(topicName);
            DemoAmopResponseCallback cb = new DemoAmopResponseCallback();
            if (isBroadcast) {
                amop.broadcastAmopMsg(out);
            } else {
                amop.sendAmopMsg(out, cb);
            }
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println(
                    "Step 1: Send out msg, time: "
                            + df.format(LocalDateTime.now())
                            + " topic:"
                            + out.getTopic()
                            + " content: file "
                            + fileName);
        }
    }

    public static boolean subscribed(BcosSDK sdk, String topicName) throws InterruptedException {
        Client client = sdk.getClient(Integer.valueOf(1));
        Boolean hasSubscriber = false;
        Peers peers = client.getPeers();
        for (int i = 0; i < 10; i++) {
            for (Peers.PeerInfo info : peers.getPeers()) {
                for (String tp : info.getTopic()) {
                    if (tp.equals(topicName)) {
                        hasSubscriber = true;
                        return hasSubscriber;
                    }
                }
            }
            if (!hasSubscriber) {
                Thread.sleep(2000);
            }
        }
        return false;
    }
}
