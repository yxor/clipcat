package com.clipcat;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    // todo: use command line arguments and fix the interface
    // todo: add validators for the input stuff
    // some dummy example of functionality
    public static void restore(String name)
    {
        // getting the data from database
        ClipboardObject clp = DBManager.get(name);
        if (clp == null){
            System.out.println("Not found.");
            return;
        }
        ClipboardManager.setData(clp);
        System.out.println("Data put in clipboard successfully");


    }

    public static void save(String name)
    {
        // getting the data from the clipboard
        ClipboardObject p = ClipboardManager.getData();

        // putting data in database
        DBManager.insert(name, p);
    }


    public static void ls(String query)
    {
        List<String> l = (query == null) ?  DBManager.list() : DBManager.list(query);

        if(l == null)
        {
            System.out.println("Not found");
            return;
        }
        for(String s: l)
        {
            System.out.println(s);
        }
    }
    public static void printUsage()
    {
        System.out.println("usage: save [name]\n\trestore [name]\n\tls\n\tquit");
    }

    public static void main(String[] args)
    {
        DBManager.connect();
        Scanner s = new Scanner(System.in);
        String[] argv;
        String command;
        String argument;

        printUsage();
        while(true)
        {
            argv = s.nextLine().split(" ");
            command = argv[0];
            argument = String.join(" ", Arrays.copyOfRange(argv, 1, argv.length)).toLowerCase();
            if(command.equalsIgnoreCase("save")) {
                save(argument);

            }else if(command.equalsIgnoreCase("restore")){
                restore(argument);

            }else if(command.equalsIgnoreCase("ls")) {
                if(argument.length() > 0)
                    ls(argument);
                else
                    ls(null);

            }else if(command.equalsIgnoreCase("quit")) {
                break;

            }else{
                printUsage();
            }
        }
    }
}
