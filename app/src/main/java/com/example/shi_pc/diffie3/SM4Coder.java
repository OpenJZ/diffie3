package com.example.shi_pc.diffie3;

import com.example.shi_pc.diffie3.common.SM.SM4;
import com.example.shi_pc.diffie3.common.SM.SM4_Context;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
import android.util.Base64;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * SM4Coder sm4 = new SM4Coder(keyBytes, ivBytes);
 *
 * byte[] cipherText = sm4.encode(plainText);
 * byte[] plainText = sm4.decode(cipherText);
 *
 * sm4.encode(plainFile, cipherFile);
 * sm4.decode(cipherFile, plainFile);
 */
public class SM4Coder {
    private byte[] keyBytes;
    private byte[] ivBytes;

    public SM4Coder(byte[] keyBytes, byte[] ivBytes) {
        this.keyBytes = keyBytes;
        this.ivBytes = ivBytes;
    }

    public SM4Coder(String keyBytes, String ivBytes) {
        this.keyBytes = keyBytes.getBytes();
        this.ivBytes = ivBytes.getBytes();
    }

    public byte[] encode(byte[] plainText) {
        return encodeOrDecode(plainText, 1);
    }

    public String encode(String plainText) throws UnsupportedEncodingException {
        byte[] bs = encode(plainText.getBytes("GBK"));
        //String cipherText = new BASE64Encoder().encode(bs);
        String cipherText = Base64.encodeToString(bs,Base64.DEFAULT);
        if (cipherText != null && cipherText.trim().length() > 0) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(cipherText);
            cipherText = m.replaceAll("");
        }
        return cipherText;
        //return new String(bs);
    }

    public void encode(String inPath, String outPath) {
        byte[] bs = null;
        try {
            //读出文件
            File infile = new File(inPath);
            InputStream inputStream = new FileInputStream(infile);
            bs = new byte[(int) infile.length()];
            inputStream.read(bs);
            inputStream.close();
            //加密
            byte[] cipher = encode(bs);

            //写入文件
            File outfile = new File(outPath);
            OutputStream outputStream = new FileOutputStream(outfile);
            outputStream.write(cipher);
            outputStream.close();
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }

    }

    public byte[] decode(byte[] plainText) {
        return encodeOrDecode(plainText, 0);
    }

    public String decode(String cipherText1) throws IOException {
        //byte[] bs = decode(new BASE64Decoder().decodeBuffer(cipherText1));
        byte[] bs = decode(Base64.decode(cipherText1,Base64.DEFAULT));

        String cipherText = new String(bs, "GBK");
        return cipherText;
		/*byte[] bs=cipherText1.getBytes();
		byte[] mw=decode(bs);
		return new String(mw);*/
    }

    public void decode(String inPath, String outPath) {
        byte[] bs = null;
        try {
            //读出文件
            File infile = new File(inPath);
            InputStream inputStream = new FileInputStream(infile);
            bs = new byte[(int) infile.length()];
            inputStream.read(bs);
            inputStream.close();
            //解密
            byte[] cipher = decode(bs);

            //写入文件
            File outfile = new File(outPath);
            OutputStream outputStream = new FileOutputStream(outfile);
            outputStream.write(cipher);
            outputStream.close();
        } catch (Exception e) {
            System.out.println("Exception " + e);
        }

    }

    private byte[] encodeOrDecode(byte[] plainText, int mode) {
        try {
            SM4_Context ctx = new SM4_Context();
            ctx.isPadding = true;
            ctx.mode = mode;
            ctx.sk = new long[32];
            SM4 sm4 = new SM4();
            if (mode == 1) sm4.sm4_setkey_enc(ctx, keyBytes);
            else sm4.sm4_setkey_dec(ctx, keyBytes);
            //此处生成iv的副本，因为iv在加密时会发生改变
            byte[] ivcopy = new byte[16];
            System.arraycopy(ivBytes, 0, ivcopy, 0, 16);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivcopy, plainText);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


