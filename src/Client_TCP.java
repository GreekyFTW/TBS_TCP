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
        String String1 = Arrays.toString(bytes).
                replaceAll(",","").
                replaceAll(" ","").
                replace("[","").
                replace("]","");

        System.out.println(Arrays.toString(bytes));
        System.out.println(String1);

        ////This fucking thing has binary mixed with hex (i lied)
        ////We replace 14(space) with 00100000(space) ( we actually dont anymore)
        //Terrible replacing of values because why not
        String FixedString = String1.
                replaceAll("99","00100000").
                replaceAll("2","S").
                replaceAll("3","E");


        System.out.println("Fixed String: " + FixedString);

        // Removing markers
        String string =  StringBetweenMarkers(FixedString, StartFlag, EndFlag).
                replaceAll("S","").
                replaceAll("E","");;

        System.out.println("String: "+string);

        //Just says the word penis for testing
        //String test = "0111000001100101011011100110100101110011";

        ////Finally decoding the binary
        System.out.println(Arrays
                .stream(string.split("(?<=\\G.{8})"))
                .map(s -> Character.toString((char)Integer.parseInt(s, 2)))
                .collect(Collectors.joining())
        );


        //Close the socket why not
        socket.close();

    }

    public static List<Integer> GetStartAndEndPoint (byte[] bytes, int StartFlag, int EndFlag){
        int iStart=-1;
        int iEnd=-1;
        int i=0;

        List list = new ArrayList();

        while(i <bytes.length){
            if(bytes[i]==StartFlag){ iStart=i; System.out.println("Transmission Start Byte : "+ iStart); list.add(i); }
            if(bytes[i]==EndFlag){ iEnd=i;  System.out.println("Transmission End Byte : "+ iEnd); list.add(i);}

            //Take care of spaces
            if(bytes[i]== 20) bytes[i]=99;

            i++;
        }
        return list;
    }

    //flags are useless for now
    public static String StringBetweenMarkers (String string , char flag1, char flag2) {
        String string1 = "01000110 01100001 01101001 01101100 01100101 01100100 00100000 01110100 01101111 00100000 01100111 01100101 01110100 00100000 01110000 01101111 01101001 01101110 01110100 01110011 ";

        int startIndex = string.indexOf('S');
        int endIndex = string.indexOf('E', startIndex + 1);

        if (startIndex != -1 && endIndex != -1) {

            string1 = string.substring(startIndex + 1, endIndex);

        }
        return string1;
    }

}

