package Code;

import java.util.ArrayList;
import java.util.Arrays;

public class Parser
{

    private String commandName;
    private String[] args;

    public boolean parse(String input)
    {

        //remove any trailing spaces
        input = input.trim();

        if(input.isEmpty()) return false;

        String[] parts = input.split(" ");

        //the command is always written first that's why we take parts first index
        commandName = parts[0];

        //check if there is arguments inserted
        if(parts.length > 1)
        {

            args = new String[parts.length - 1];
            int argIndex = 0;

            for(int i = 1; i < parts.length; i++)
            {

                if(parts[i].charAt(0) == '\'')
                {

                    //if argument name is between quotes
                    if(parts[i].charAt(parts[i].length()-1) == '\'')
                    {

                        parts[i] = parts[i].substring(1,  parts[i].length()-1); //argument without the quotes
                        args[argIndex] = parts[i];

                    }
                    else
                    {

                        int j = i;
                        ArrayList<String> quotedArguments = new ArrayList<>(); //arraylist to hold the argument between quotes

                        //add first word without its starting '
                        quotedArguments.add(parts[j].substring(1));
                        j++;

                        //add in between words
                        while(parts[j].charAt(parts[j].length()-1) != '\'')
                        {

                            quotedArguments.add(parts[j]);
                            j++;

                        }

                        //add last word without last '
                        quotedArguments.add(parts[j].substring(0, parts[j].length()-1));

                        args[argIndex] = String.join(" ", quotedArguments);

                        i = j;

                    }

                }
                else if(parts[i].charAt(0) == '"')
                {

                    //if argument name is between quotes
                    if(parts[i].charAt(parts[i].length()-1) == '"')
                    {

                        parts[i] = parts[i].substring(1,  parts[i].length()-1); //argument without the quotes
                        args[argIndex] = parts[i];

                    }
                    else
                    {

                        int j = i;
                        ArrayList<String> quotedArguments = new ArrayList<>(); //arraylist to hold the argument between quotes

                        //add first word without its starting '
                        quotedArguments.add(parts[j].substring(1));
                        j++;

                        //add in between words
                        while(parts[j].charAt(parts[j].length()-1) != '"')
                        {

                            quotedArguments.add(parts[j]);
                            j++;

                        }

                        //add last word without last '
                        quotedArguments.add(parts[j].substring(0, parts[j].length()-1));

                        args[argIndex] = String.join(" ", quotedArguments);

                        i = j;

                    }

                }
                else args[argIndex] = parts[i];

                argIndex++;

            }

            //if arguments after joining is smaller than arg og size then resize it
            if (argIndex < args.length) args = Arrays.copyOf(args, argIndex);

        }
        else args = new String[0]; //if no arguments make empty arguments array

        return true;

    }
    public String getCommandName() {return commandName;}
    public String[] getArgs() {return args;}

}