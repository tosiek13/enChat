package message;

import messages.IncorrectMessageId;
import rsa.RSA;
import server.Server;
import user.ActiveUser;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tochur on 24.04.15.
 */
public class Encryption {
    public static Message decryptMessage(EncryptedMessage encryptedMessage, ActiveUser activeUser) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, UnsupportedEncodingException, SignatureException {
        List<Pack> packages = encryptedMessage.getPackages();
        List<String> strings = new LinkedList<String>();
        Key privateServerKey = Server.getInstance().getKeyContainer().getPrivateKeyInfo().getPrivateKey();
        for(Pack pack: packages){
            byte[] decrypted = RSA.decrypt(pack.getDataArray(), privateServerKey);
            RSA.checkSign(pack.getSignArray(),decrypted, activeUser.getPublicKey());
            strings.add(decrypted.toString());
        }
        new Message(encryptedMessage.getId(), encryptedMessage.getErrorId(), encryptedMessage.getPackageAmount(), strings);
        return null;
    }

    /**
     * Only packages (data) are encrypting
     * @param activeUser - receiver of the message
     * @param message - message to encrypt
     * @return encrypted message
     */
    public static EncryptedMessage encryptMessage(ActiveUser activeUser, Message message) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IncorrectMessageId, InvalidKeySpecException, SignatureException {
        List<Pack> packages = new LinkedList<Pack>();

        int stringsAmount = message.getPackageAmount();
        if (stringsAmount > 0){
            for(String string: message.getPackages()){
                byte[] data = string.getBytes();
                byte[] encrypted = RSA.encrypt(data, activeUser.getPublicKey());
                PrivateKey privateKey = Server.getInstance().getKeyContainer().getPrivateKeyInfo().getPrivateKey();
                byte[] sign = RSA.sign(data, privateKey);

                packages.add(new Pack(encrypted, sign));
            }
        }

        return new EncryptedMessage(message.getId().getIntRepresentation(), message.getErrorId(), message.getPackageAmount(), packages);
    }
}
