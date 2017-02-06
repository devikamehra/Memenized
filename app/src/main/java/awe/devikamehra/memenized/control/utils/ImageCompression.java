package awe.devikamehra.memenized.control.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

/**
 * Created by Devika on 01-02-2017.
 */

public class ImageCompression {

    public byte[] compressByteArray(byte[] bytes){

        ByteArrayOutputStream baos = null;
        Deflater dfl = new Deflater();
        dfl.setLevel(Deflater.BEST_COMPRESSION);
        dfl.setInput(bytes);
        dfl.finish();
        baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4*1024];
        try{
            while(!dfl.finished()){
                int size = dfl.deflate(tmp);
                baos.write(tmp, 0, size);
            }
        } catch (Exception ex){

        } finally {
            try{
                if(baos != null) baos.close();
            } catch(Exception ex){}
        }

        return baos.toByteArray();
    }
}
