import com.mongodb.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Main {

    public static MongoClient mongoClient;
    public static DB database;
    public static DBCollection collection;

    public static void main(String[] args) throws Exception, UnknownHostException {

        String [] names = new String[]{"Peter Parker", "Bruce Wayne", "Clark Kent"};
        String[] passwords = new String[]{"Uncl3BEN<3", "N0tB4tm4n", "dailyPlanet"};
        System.setProperty("password", "Dpassword");
        String password = System.getProperty("password");
        if (password == null) {
            throw new IllegalArgumentException("Run with -Dpassword=<password>");
        }

        byte[] salt ="12345678".getBytes();

        int iterationCount = 50000;

        int keyLength = 128;
        SecretKeySpec key = getSecretKey(password.toCharArray(),
                salt, iterationCount, keyLength);


        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        database = mongoClient.getDB("Database");
        collection = database.getCollection("Collection");
        Database db = new Database();

        populateDatabase(db, names, passwords, key);
        //printEncrypted(collection);
        printDecrypted(collection, key);
    }

    private static void printEncrypted(DBCollection collection){
        System.out.println("Print the data with encrypted passwords:");
        DBCursor cursor = collection.find();
        while (cursor.hasNext()){
            DBObject next = cursor.next();
            System.out.println(next);
        }
    }

    private static void printDecrypted(DBCollection collection, SecretKeySpec key) throws GeneralSecurityException, IOException {
        System.out.println("Print the data with decrypted passwords:");
        DBCursor cursor1 = collection.find();
        while (cursor1.hasNext()){
            DBObject next = cursor1.next();
            Map map = next.toMap();
            String encrpass = (String) map.get("password");
            String decryptpass = decrypt(encrpass, key);
            map.replace("password", decryptpass);
            System.out.println(map);
        }
    }

    private static void populateDatabase(Database db, String[] names, String[] passwords, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        for(int i = 0; i < names.length; i++){
            db.setName(names[i]);
            String encryptedPassword = encrypt(passwords[i], key);
            db.setPassword(encryptedPassword);
            collection.insert(convert(db));
        }
    }

    private static SecretKeySpec getSecretKey(char[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength);
        SecretKey keyTmp = keyFactory.generateSecret(keySpec);
        return new SecretKeySpec(keyTmp.getEncoded(), "AES");
    }

    private static String encrypt(String property, SecretKeySpec key) throws GeneralSecurityException, UnsupportedEncodingException {
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters parameters = pbeCipher.getParameters();
        IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
        byte[] cryptoText = pbeCipher.doFinal(property.getBytes(StandardCharsets.UTF_8));
        byte[] iv = ivParameterSpec.getIV();
        return base64Encode(iv) + ":" + base64Encode(cryptoText);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static String decrypt(String string, SecretKeySpec key) throws GeneralSecurityException, IOException {
        String iv = string.split(":")[0];
        String property = string.split(":")[1];
        Cipher pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(base64Decode(iv)));
        return new String(pbeCipher.doFinal(base64Decode(property)), StandardCharsets.UTF_8);
    }

    private static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }

    public static DBObject convert(Database db){
        return new BasicDBObject("name", db.getName()).append("password", db.getPassword());
    }
}