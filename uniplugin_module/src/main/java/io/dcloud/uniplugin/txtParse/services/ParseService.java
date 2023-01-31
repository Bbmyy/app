package io.dcloud.uniplugin.txtParse.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.dcloud.uniplugin.txtParse.format.DataBase;
import io.dcloud.uniplugin.txtParse.format.DefaultParse;
import io.dcloud.uniplugin.txtParse.format.HTTPRequest;
import io.dcloud.uniplugin.txtParse.format.HTTPResponse;
import io.dcloud.uniplugin.txtParse.format.Result;
import io.dcloud.uniplugin.txtParse.utils.AdbUtil;
import io.dcloud.uniplugin.txtParse.utils.TranscodeUtil;

public class ParseService {
    public static String HeaderPattern = "tls_(\\d){4}/(\\d){2}/(\\d){2} (\\d){2}:(\\d){2}:(\\d){2} UUID:(.)*, Name:(.)*, Type:\\d, Length:(\\d)*";
    public static String TimePattern = "tls_(\\d){4}/(\\d){2}/(\\d){2} (\\d){2}:(\\d){2}:(\\d){2} ";
    public static String HexPattern = "^[A-Fa-f0-9]{8} ";
    public static List<DataBase> ReadTxt (String path) throws IOException {
        File file = new File(path);
        BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
        //Scanner fileRead = new Scanner(file);
        Result result1 = null;
        List<DataBase> list = new ArrayList<>();
        String nextLine;
        while ((nextLine = fileRead.readLine())!=null){
            //System.out.println(nextLine);
            if(isHeader(nextLine)){
                result1 = parseHeader(nextLine,fileRead);
                list.add(result1.getDataBase());
                break;
            }
        }
        while(result1.getHeader()!=null){
            result1 = parseHeader(result1.getHeader(),fileRead);
            list.add(result1.getDataBase());
        }
        list.add(result1.getDataBase());
        showList(list);
        //DefaultParse defaultParse = (DefaultParse) list.get(100);
        //System.out.println(":" + defaultParse);
        //System.out.println("content: " + ContentParse2base64(defaultParse.getContent()));
        return list;
    }
    public static Result parseHeader(String headerStr,BufferedReader fileRead) throws IOException {
        List<String> result = new ArrayList<>();
        DataBase dataBase = null;
        Result result1 = null;
        String [] strings = headerStr.split(",");
        String [] strings1 = strings[0].split("UUID:");
        result.add(strings1[0].substring(4));
        result.add(strings1[1]);
        result.add(strings[1].substring(6));
        result.add(strings[2].substring(6));
        result.add(strings[3].substring(8));
        if(result.get(2).equals("HTTPRequest")){
            result1 = ParseHTTPRequest(result,fileRead);
        }else if(result.get(2).equals("HTTPResponse")){
            result1 = ParseHTTPResponse(result,fileRead);
        }else if (result.get(2).equals("DefaultParser")){
            result1 = ParseDefaultParse(result,fileRead);
        }
        //System.out.println(result1);
        return result1;
    }

    public static Result ParseHTTPRequest(List<String> result,BufferedReader fileRead) throws IOException {
        Result result1 = new Result();
        HTTPRequest httpRequest = new HTTPRequest();
        httpRequest.setTime(result.get(0));
        httpRequest.setUuid(result.get(1));
        httpRequest.setType(Integer.valueOf(result.get(3)));
        httpRequest.setLength(Integer.valueOf(result.get(4)));
        String time = fileRead.readLine();
        Pattern pattern = Pattern.compile(TimePattern);

        Matcher matcher = pattern.matcher(time);
        String nextLine;
        //System.out.println(matcher.matches()+"  "+ time);
        if(matcher.matches()){
            httpRequest.setMethod(fileRead.readLine());
            httpRequest.setHost(fileRead.readLine());
            Pattern GetPattern = Pattern.compile("GET.*");
            Matcher GetMatcher = GetPattern.matcher(httpRequest.getMethod());
            Pattern PostPattern = Pattern.compile("POST.*");
            Matcher PostMatcher = PostPattern.matcher(httpRequest.getMethod());
            Pattern ReqIDPattern = Pattern.compile("X-Req-Id:.*");
            Pattern isEndPattern = Pattern.compile("tls.*");
            if(GetMatcher.find()){
                httpRequest.setData(null);
                Matcher matcher1;
                while ((nextLine = fileRead.readLine())!=null){
                    matcher1 = ReqIDPattern.matcher(nextLine);
                    if(matcher1.find()){
                        httpRequest.setRequestID(nextLine);
                    }else if (isHeader(nextLine)){
                        result1.setDataBase(httpRequest);
                        result1.setHeader(nextLine);
                        return result1;
                    }
                }
            }else if(PostMatcher.find()){
                String data="";
                Matcher matcher2;
                Matcher isEndMatcher;
                while ((nextLine = fileRead.readLine())!=null){
                    matcher2 = ReqIDPattern.matcher(nextLine);
                    if(matcher2.find()){
                        httpRequest.setRequestID(nextLine);
                    }else if(isHeader(nextLine)){
                        result1.setDataBase(httpRequest);
                        result1.setHeader(nextLine);
                        return result1;
                    }else if(nextLine.length()==0){
                        while ((nextLine = fileRead.readLine())!=null){
                            isEndMatcher = isEndPattern.matcher(nextLine);
                            if(!isEndMatcher.find()){
                                data = data+nextLine+"\n";
                            }else if (isHeader(nextLine)){
                                httpRequest.setData(data);
                                result1.setDataBase(httpRequest);
                                result1.setHeader(nextLine);
                                return result1;
                            }
                        }
                    }
                }
            }else{
                result1.setDataBase(httpRequest);
                return result1;
            }
            result1.setDataBase(httpRequest);
            return result1;
        }else if(isHeader(time)){
            result1.setDataBase(httpRequest);
            result1.setHeader(time);
        } else{
            while ((nextLine = fileRead.readLine())!=null){
                if(isHeader(nextLine)){
                    result1.setDataBase(httpRequest);
                    result1.setHeader(nextLine);
                    return result1;
                }
            }
        }
        return result1;
    }

    public static Result ParseHTTPResponse(List<String> result,BufferedReader fileRead) throws IOException {
        Result result1 = new Result();
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.setTime(result.get(0));
        httpResponse.setUuid(result.get(1));
        httpResponse.setType(Integer.valueOf(result.get(3)));
        httpResponse.setLength(Integer.valueOf(result.get(4)));
        fileRead.readLine();
        httpResponse.setStatus(fileRead.readLine());
        Pattern ReqIDPattern = Pattern.compile("Request-Id:.*");
        Pattern isEndPattern = Pattern.compile("tls.*");
        String data="";
        Matcher ReqIDmatcher;
        Matcher isEndMatcher;
        String nextLine;
        while ((nextLine = fileRead.readLine())!=null){
            ReqIDmatcher = ReqIDPattern.matcher(nextLine);
            if(ReqIDmatcher.find()){
                httpResponse.setRequestID(nextLine);
            }else if(isHeader(nextLine)){
                result1.setDataBase(httpResponse);
                result1.setHeader(nextLine);
                return result1;
            }else if(nextLine.length()==0){
                while ((nextLine = fileRead.readLine())!=null){
                    isEndMatcher = isEndPattern.matcher(nextLine);
                    if(!isEndMatcher.find()){
                        data = data+nextLine+"\n";
                        httpResponse.setData(data);
                    }else if (isHeader(nextLine)){
                        httpResponse.setData(data);
                        result1.setDataBase(httpResponse);
                        result1.setHeader(nextLine);
                        return result1;
                    }
                }
            }
        }
        result1.setDataBase(httpResponse);
        return result1;
    }

    public static Result ParseDefaultParse(List<String> result,BufferedReader fileRead) throws IOException {
        Result result1 = new Result();
        DefaultParse defaultParse = new DefaultParse();
        defaultParse.setTime(result.get(0));
        defaultParse.setUuid(result.get(1));
        defaultParse.setType(Integer.valueOf(result.get(3)));
        defaultParse.setLength(Integer.valueOf(result.get(4)));
        fileRead.readLine();
        String data = "";
        Pattern isEndPattern = Pattern.compile("tls.*");
        Matcher isEndMatcher;
        String nextLine;
        while ((nextLine = fileRead.readLine())!=null){
            isEndMatcher = isEndPattern.matcher(nextLine);
            if(!isEndMatcher.find()){
                data = data+nextLine+"\n";
            }else if (isHeader(nextLine)){
                defaultParse.setContent(data);
                result1.setDataBase(defaultParse);
                result1.setHeader(nextLine);
                return result1;
            }
        }
        result1.setDataBase(defaultParse);
        return result1;
    }

    private static boolean isHeader(String str){
        Pattern pattern = Pattern.compile(HeaderPattern);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    private static void showList(List<DataBase> list){
        for(DataBase dataBase: list){
            if((dataBase instanceof DefaultParse)&&(((DefaultParse) dataBase).getContent().length()>=8)&&((DefaultParse) dataBase).getContent().substring(0,8).equals("00000000")){
                //System.out.println(((DefaultParse) dataBase).getContent().substring(0,8));
                //System.out.println(dataBase);
                ((DefaultParse) dataBase).setContent(ContentParse2base64(((DefaultParse) dataBase).getContent()));
            }
            System.out.println(dataBase);
        }
    }

    public static String ContentParse2base64(String content){
        String hex = "";
        String lines[] = content.split("\n");
        String data;
        Pattern pattern = Pattern.compile(HexPattern);
        for (int i=0;i<lines.length;i++){
            Matcher matcher = pattern.matcher(lines[i]);
            if(matcher.find()){
                data = lines[i].substring(10,60);
                hex = hex + data.replaceAll(" ", "");
            }
        }
        byte[] bytes = TranscodeUtil.hexStrToByteArray(hex);
        String base64 = TranscodeUtil.byteArrayToBase64Str(bytes);
        return base64;
    }


    public static void main(String[] args) throws IOException {
        //String path = "D:\\Android code\\java_pcap_file_utilities-master\\pcap\\src\\main\\java\\com\\silabs\\na\\pcap\\txtParse\\data\\data2.txt";
        //ReadTxt(path);

//        String data = "020a8e516e8bdd1f606222c281bf2e694132c3f74c59dc999f1b89af773085b4e035c9ca88d85488ad49ca45c13d2a0a5adcc7332170102aa16abafc4a2be5ae9e518a2e84a0d3f7a3e1df61cb23d9f6f6cbd70d12860d9a9499a7dcf75e6afc9f91e3e57d01ac13b034a8182749cbeca06482d76ed489d7c995c4d119317ddb3c09253b2be96b9c8cee503a302f0f0e0b68051923c27651270f5db9f4085d7505d6ff000b1c2a406b0703586268be31e7a2404df04e4db832684e0ea769321d46b031b0bc80da46c1b6c10a51a8f31b04092f4f0ed4bbaf9b231944156a497b92f08eb42ca680548155521dc6a78d609a5d93c3844c1a70d12b3f1e0478774d79f6eb9c2ba071e9096686a7b6b8cb61c68f2b852a9c14829d487082f63d921431f68d0f15e9604d3b0000b195d1c94f013280eeaf3f673acd22a71a04beea39066794198fd71a83018654caccd678ec245c329d4674c1a8a55c432a9435e049c173a670032c66ca5d30bd90909b892f7d05d5b189eb037c22829b1dc856ad364f27bb390693cd29e411256df85d1c4ae099b201886b620715eb54173bdf9021f7d3a517c71af79ea6302c50aea41481aca43703d172238e07b2ecc4814c5d3b9e45fa48a70aa9d5048f11f2ea40d91356a84ae0ec503a5943307285461b948a456198908de5fbb0f57a13d97ad528ec192c5063b9d366eb26b39b2c8bf5fa7ca0d059860f83ac4fa9846128ca2708f162e0f26a56a6d51c5d9ed9ed7dc50545b006a67da30437b8d46e0e6780296a2c5ac10449cb18660968a2d2f34f239995d048c63700221219c8dadf83a14752211b5803ca25b49ac3363ca178c328b44cda39950b5e4c1b016b8c11d24950954ea52ad166c22a79ff00a191aec52dd8eb21008541790931d89821da8e0a5b572d091f9560906249a3431225528d57bf3b2d761a4ece454a875b64f45839dff0e04eefd06cfdf8939a6081a1a069dfcf5782f6285dd6d4f5924b2c89c623d07cf49ff097edc3ba4659e8ff005fd7c1bd8c8760b10c7b7d77d746c46895218b15424d098452435be5f5b40aa350a53412232b2bceb43bd1d4d51f6c045668aea07b76f18ff90424e2a367b20ed8e3dcf980423561008eca9383a36f608648f28269713df1cb368418b333aeba5b14bc1955551ed0d26e7ad1dc1e0d7ba9abbe3fe1737e46fc99525064524420ce68bc96280a0ca3bf4590560284941a68af7cb46383abbc5d09faf9a762abcbd5bf0f406a42747af615540f103a3a0f443f18f888e5447a02fda2f7786d54e55edc303a0324b78165130f4849ef537b5b508bad605c7c729c2e0745800773860e956fbd7171e49aca21424c2c81440f01b37802f2b4e848614fc1b2186d9efc147439ec968f4226a54dd31217b8d5481b46457a74fd7d0d327dff003fcf1fdf3b05605a45260908039d2aba92642167575a924e9a003cd39569a8451cb7849cd912884e574f0708b17c1d1029e8251d543cb61a191224beb4829d0317004df820fb4e044b195c114e00eb17d70a22269844fd72292e9964a60e12a09c61b4d4a26d5d2e749d15e10294ac036460fba92a4af320c7a01eaea36b71a178eaee2658843b0a3e516100b320ec8993c9ab0b85e444ec89f50eff00c31796e40cf91839840b904c710b5f0405640b002b7c3163bb191f01140b4da0306c77d2fd4a6dd2f75d2f12918a06a3033692d48e383e805db88e2af4053e41c892318c8b0c2a62bba351398a261300558b8aebc65cbe9704f271391bc964ed160ac98807852472264aef26249a9ad5821e88527136b90822bd1973338899c16e02b00612cc07a5af225e153bc19eb74f39cec463059c16183e067eeeeffce0b0d280f53ab3f7ef8822242e0795d9694e83b4dc9728bd2d17e4f27e782298c81633cbdf475fb72c0d45239ac687859f0d947930c4caa335b2644772d4673e3423e156d36351bf62e9c805ac512d572009bd4f8323a5839f107bc8680dcbb1fc8f12b237c3429f474e0d91a54918150832ca181153b572b767982017c20ca9d140431c1e94f4d0a0ead322731d909352b0d488f0d922aa2c518368f8cd6c788bc416d90e4ded8e0d788efbdd40f0e580f5c4cd9e598a0f28de351f0b0f2316448840800206030ce7ffd9";
//        byte[] bytes= TranscodeUtil.hexStrToByteArray(data);
//        System.out.println(TranscodeUtil.byteArrayToHexStr(bytes));
//        String base64 = TranscodeUtil.byteArrayToBase64Str(bytes);
//        System.out.println(base64);
//        String str1 = TranscodeUtil.base64StrToStr(base64);
//        System.out.println(str1);
//        String hex = TranscodeUtil.strToHexStr(str1);
//        System.out.println(hex);
        String path = "data/local/tmp/ecapture1";
        String result = AdbUtil.execShell("./" + path +"/ecapture");
        System.out.println(result);
    }
}
