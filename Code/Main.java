package Code;

import java.io.*;
import java.util.Scanner;


public class Main
{

    public static void main(String[] args) throws IOException
    {

        Terminal terminal = new Terminal();
        Scanner scanner = new Scanner(System.in);

        while(true)
        {

            System.out.print("> ");

            String input = scanner.nextLine();

            if(terminal.getParser().parse(input)) terminal.chooseCommandAction();
            else System.out.println("error");

        }

    }

}