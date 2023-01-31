package io.dcloud.uniplugin.txtParse.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AdbUtil {
    public static String execShell(String cmd){
        StringBuilder s = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String line = null;
            while ((line = bufferedReader.readLine())!=null){
                s.append(line).append("\n");
            }
            inputStream.close();;
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

}
