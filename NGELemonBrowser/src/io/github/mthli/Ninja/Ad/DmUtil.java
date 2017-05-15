package io.github.mthli.Ninja.Ad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

/**
 * Created by Tommy on 15/3/23.
 */
public class DmUtil {

    public static void exec(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean findDaemon(String processName) {
        boolean z = false;
        Pattern compile = Pattern.compile("(\\d+).+?\\s+[DNRSTZ]\\s+(.+)$");
        Map hashMap = new HashMap();
        try {
            Process exec = Runtime.getRuntime().exec("ps");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            while (true) {
                CharSequence readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                Matcher matcher = compile.matcher(readLine);
                if (matcher.find()) {
                    hashMap.put(matcher.group(2), null);
                }
            }
            if (hashMap.containsKey(processName)) {
                z = true;
            }
            bufferedReader.close();
            exec.destroy();
        } catch (IOException e) {
//            e.printStackTrace();
        }

        return z;
    }


    public static List<Integer> getDaemons(String str) {
        BufferedReader bufferedReader = null;
        Process process = null;
        List<Integer> arrayList = new ArrayList<Integer>();
        try {
            process = Runtime.getRuntime().exec("ps");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            try {
                int pidIndex = 1;
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    readLine = readLine.toUpperCase(Locale.ENGLISH);
                    if (readLine.contains("PID") && readLine.contains("PPID")) {
                        StringTokenizer stringTokenizer = new StringTokenizer(readLine, " ", false);
                        int countTokens = stringTokenizer.countTokens();
                        for (int i = 0; i < countTokens; i++) {
                            String nextToken = stringTokenizer.nextToken();
                            if ("PID".equals(nextToken)) {
                                pidIndex = i;
                                break;
                            }
                        }
                    }

                    while (true) {
                        readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        if (readLine.contains(str)) {
                            StringTokenizer stringTokenizer = new StringTokenizer(readLine, " ", false);
                            int countTokens = stringTokenizer.countTokens();
                            for (int i = 0; i < countTokens; i++) {
                                String nextToken = stringTokenizer.nextToken();
                                if (i == pidIndex) {
                                    arrayList.add(Integer.valueOf(Integer.parseInt(nextToken)));
                                }
                            }
                        }
                    }

                }
            } catch (IOException e) {
//                e.printStackTrace();
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e2) {
//                e2.printStackTrace();
            }
        }
        if (process != null) {
            process.destroy();
        }

        return arrayList;
    }

    public static void killDaemon(List<Integer> list) {
        for (Integer num : list) {
            android.os.Process.killProcess(num.intValue());
        }
    }

    public static void saveDaemon(Context context, File destFile, int rawId, boolean overwrite) {
        if (destFile.exists() && !overwrite) {
            return;
        }

        try {
            InputStream inputStream = context.getResources().openRawResource(rawId);
            FileOutputStream fileOutputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int count = 0;
            while((count = inputStream.read(buffer)) > 0){
                fileOutputStream.write(buffer, 0 ,count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            DmUtil.exec("chmod 755 " + destFile);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
