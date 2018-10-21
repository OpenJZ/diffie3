package com.example.shi_pc.diffie3;

import com.example.shi_pc.diffie3.common.SM.SM3Digest;

//import sun.misc.BASE64Encoder;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * SM3Hash sm3= new SM3Hash();
 * byte[] hash1, hash2;
 * byte[] msg;
 * String filePath = new String("E:/Poem.txt");
 * hash1 = sm3.SM3(msg);
 * hash2 = sm3.SM3(filePath);
 *
 */
public class SM3Hash {
    public SM3Hash() {

    }

    ;

    public byte[] SM3(byte[] msg) {
        byte[] md = new byte[32];
        SM3Digest sm3 = new SM3Digest();
        sm3.update(msg, 0, msg.length);
        sm3.doFinal(md, 0);
        return md;
    }

    public byte[] SM3(String filePath) {
        byte[] bs = null;
        try {
            //读出文件
            File infile = new File(filePath);
            InputStream inputStream = new FileInputStream(infile);
            bs = new byte[(int) infile.length()];
            inputStream.read(bs);
            inputStream.close();
            //取Hash
            byte[] Hash = SM3(bs);
            return Hash;
        } catch (Exception e) {
            System.out.println("Exception " + e);
            return null;
        }
    }

    public String strSM3(String input) {
        byte[] bs = SM3(input.getBytes());
        //String cipherText = new BASE64Encoder().encode(bs);
        String cipherText = Base64.encodeToString(bs,Base64.DEFAULT);
        if (cipherText != null && cipherText.trim().length() > 0) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(cipherText);
            cipherText = m.replaceAll("");
        }
        return cipherText;
    }

}
