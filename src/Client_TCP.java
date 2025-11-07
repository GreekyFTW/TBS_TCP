import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.String;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class Client_TCP {

    static String IP = "167.99.24.91";
    static String PORT = "2018";
    static int TIMEOUT = 10000;

    static byte[] bytes = new byte[4096];

    //flags are useless for now

    static char StartFlag = 2;
    static char EndFlag = 3;


    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(String.valueOf(IP), Integer.parseInt(PORT)));
            System.out.println("Connection Successful!");
        } catch (IOException e) {
            System.out.println("Connection UnSuccessful!");
            throw new RuntimeException(e);
        }

        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        dataInputStream.readFully(bytes,0,bytes.length);

        ////Get Transmission Key Points
        List<Integer> list = GetStartAndEndPoint(bytes,StartFlag,EndFlag);
        //StringBetweenMarkers(bytes,list.get(1).toString().to)

        //String Hexadecimal = HexFormat.of().formatHex(bytes);
        String Hexadecimal = Arrays.toString(bytes).
                replaceAll(",","").
                replaceAll(" ","").
                replace("[","").
                replace("]","");

        System.out.println(Arrays.toString(bytes));
        System.out.println(Hexadecimal);

        ////This fucking thing has binary mixed with hex (i lied)
        //We replace 14(space) with 00100000(space)
        String FixedString = Hexadecimal.
                replaceAll("99","00100000").
                replaceAll("2","S").
                replaceAll("3","E");


        System.out.println("Fixed String: " + FixedString);

        String string =  StringBetweenMarkers(FixedString, StartFlag, EndFlag).
                replaceAll("S","").
                replaceAll("E","");;

        System.out.println("String: "+string);

        //String test = "0111000001100101011011100110100101110011";

        System.out.println(Arrays
                .stream(string.split("(?<=\\G.{8})"))
                .map(s -> Character.toString((char)Integer.parseInt(s, 2)))
                .collect(Collectors.joining())
        );

        socket.close();

    }

    public static List<Integer> GetStartAndEndPoint (byte[] bytes, int StartFlag, int EndFlag){
        int iStart=-1;
        int iEnd=-1;
        int i=0;

        List list = new ArrayList();

        while(i <bytes.length){
            if(bytes[i]==StartFlag){ iStart=i; System.out.println("Transmition Start Byte : "+ iStart); list.add(i); }
            if(bytes[i]==EndFlag){ iEnd=i;  System.out.println("Transmition End Byte : "+ iEnd); list.add(i);}

            //Take care of spaces
            if(bytes[i]== 20) bytes[i]=99;

            i++;

        }
        return list;
    }

    //flags are useless for now
    public static String StringBetweenMarkers (String string , char flag1, char flag2) {
//        int i=0;
//
//        int iByte=0;
//
//        byte[] bytes1 = new byte[4096];
//        int stringSize = 0;
//
//        while(i<list.size()-1) {
//            if ((list.get(i + 1) - list.get(i)) == 1) {
//            }else{
//                stringSize = list.get(i + 1) - list.get(i);
//
//                while (iByte<stringSize && iByte<4096){
//                    bytes1[iByte] = bytes[i];
//                    iByte= iByte + 1;
//                    //System.out.println(iByte-1);
//                }
//            }
//            i++;
//        }
//
//        System.out.println(stringSize);
//
//
//        String Hexadecimal = HexFormat.of().formatHex(bytes1);
//
//        System.out.println(Hexadecimal);
//
//        String FixedString = Hexadecimal.
//                replaceAll("14","00100000").
//                replaceAll("3","0010000000100000").
//                replaceAll("2","0010000000100000");
//
//        return FixedString;

        String result = "failed";

        int startIndex = string.indexOf('S');

        // to find the index of the end character after the start character
        int endIndex = string.indexOf('E', startIndex + 1);

        // check if both start and end characters are found
        if (startIndex != -1 && endIndex != -1) {
            // extract the substring between the start and end characters
             result = string.substring(startIndex + 1, endIndex);

        }
        return result;
    }

}

