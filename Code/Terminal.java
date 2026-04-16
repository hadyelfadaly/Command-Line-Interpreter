package Code;

import java.io.*;
import java.util.List;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Arrays;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

public class Terminal
{


    private Parser parser;
    private String currentDirectory; //a variable to save the current directory to use in implementing cd

    Terminal()
    {

        parser = new Parser();
        currentDirectory = System.getProperty("user.dir"); //saves current path
        //into the variable when an instance of terminal is created

    }

    public Parser getParser()
    {return parser;}
    //helper method to resolve absolute vs relative paths
    private File resolvePath(String pathString)
    {

        File file = new File(pathString);

        if(!file.isAbsolute()) file = new File(currentDirectory, pathString);
        
        return file;
        
    }

    public String pwd(String[] args)
    {

        if(args.length > 0) return "This Command Takes no Arguments";

        return currentDirectory;

    }
    public void cd(String[] args)
    {

        //to handle directories with space in their name like "Operating Systems" as it should be read as 1 arg not multiple ones
        String path = String.join(" ", args);

        //if no args change dir to home dir
        if(args.length == 0) System.out.println(System.getProperty("user.home"));
        else if(args[0].equals("..")) //if arg = .. go to parent dir
        {

            //create file object from the path
            File current = new File(currentDirectory);
            String parent = current.getParent();

            if(parent != null)
            {

                currentDirectory = parent;

                System.out.println(currentDirectory);

            }
            else System.out.println("Error: Already at root directory");

        }
        else
        {

            File newPath = resolvePath(path); //create file object from the path

            if(newPath.exists() && newPath.isDirectory()) //make sure this path exists and that its a directory
            {

                currentDirectory = newPath.getAbsolutePath();

                System.out.println(currentDirectory);

            }
            else System.out.println("Error: Directory does not exist");

        }

    }
    public void mkdir(String[] args)
    {

        if(args.length == 0)
        {

            System.out.println("mkdir: missing operand");

            return;

        }

        //iterate over args
        for(int i = 0; i < args.length; i++)
        {

            //create object file from the arg
            File file = resolvePath(args[i]);

           if(file.exists() && file.isDirectory()) //if file exists
            {

                System.out.println("mkdir: cannot create directory " + args[i] + " : File exists");

                continue;

            }

            try
            {

                file.mkdir();

            }
            catch(Exception error)
            {

                    System.out.println("mkdir: cannot create directory " + args[i] + " : " + error);

            }

        }

    }
    public String ls(String[] args)
    {

        if(args.length > 0)
        {return "this command takes no arguments";}

        File dir = new File(currentDirectory); //find current directory
        String[] contents = dir.list(); //array of file names.
        String result = "";

        if(contents != null)
        {

            List<String> list = Arrays.asList(contents); //sort alphabetically

            Collections.sort(list);

            for(String item : list) result += (item + "  ");

        }
        else return "Error: Can't list directory contents";

        return result;

    }
    public void cp(String[] args)
    {

        if(args.length < 2)
        {

            System.out.println("cp: missing operand"); //validate the arguments must be cp <source> <destination>

            return;

        }

        //make file objects of given arguments
        File source = resolvePath(args[0]);
        File destination = resolvePath(args[1]);
        boolean recursive = false;

        if(args[0].equals("-r"))
        {

            if(args.length < 3) 
            {

                System.out.println("cp: -r requires a source and destination directory");

                return;

            }

            recursive = true;
            source = resolvePath(args[1]);
            destination = resolvePath(args[2]);

        }
        else
        {

            if(args.length > 2)
            {

                System.out.println("cp: too many arguments for standard copy");

                return;

            }

        }

        if(!source.exists() || !source.isFile()) //make sure source file exists
        {

            System.out.println("cp: source file does not exist");

            return;
        }


        try
        {

            if(recursive)
            {

                if(!source.isDirectory())
                {

                    System.out.println("cp: source is not a directory");

                    return;

                }

                copyDirectoryRecursive(source, destination);

            }
            else
            {

                //using toPath() function to convert file object to path object
                Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING); //copy the file

            }

        }
        catch(IOException error) //catches input or output or file operations errors
        {

            System.out.println("cp: error");

        }

    }
    //helper function for recursive copy
    private void copyDirectoryRecursive(File source, File dest) throws IOException
    {

        //create dest dir if it does not exist
        if(!dest.exists()) dest.mkdirs();

        //array of all contents in the source
        File[] files = source.listFiles();

        if(files != null)
        {

            //copy file by file
            for(File file : files)
            {

                File newDest = new File(dest, file.getName());

                //if the file is a directory call the function recursively
                if(file.isDirectory()) copyDirectoryRecursive(file, newDest);
                else Files.copy(file.toPath(), newDest.toPath(), StandardCopyOption.REPLACE_EXISTING);

            }

        }

    }
    public void touch(String[] args)
    {

        //if user didn't write anything
        if(args.length == 0)
        {

            System.out.println("touch: missing file ");

            return;

        }

         //join all args to handle file name with spaces
         String path = String.join(" ", args);
         File file = resolvePath(path);

         try
         {
             //if file already exist
             if(file.exists())
             {

                 //update to modify file timestamp
                 boolean updated = file.setLastModified(System.currentTimeMillis());

                 if(!updated) System.out.println("touch: failed to update timestamp for " + file.getName());

             }
             else
             {
                 //create file
                 boolean created = file.createNewFile();

                 if(!created) System.out.println("touch: failed to create file " + file.getName());

             }

         }
         catch(IOException error)
         {
             // exception
             System.out.println("touch: error - " + error.getMessage());
         }

    }
    public void rmdir(String[] args)
    {
        //check if user wrote any argument
        if(args.length == 0)
        {

            System.out.println("rmdir: missing operand");

            return;

        }

        //case 1: if argument is "*"
        if(args.length == 1 && args[0].equals("*"))
        {

            //get files/dirs of the dir we are in
            File currentDir = new File(currentDirectory);
            File[] contents = currentDir.listFiles();

            if(contents != null)
            {

                for(File item : contents)
                {

                    //delete only empty directories
                    if(item.isDirectory())
                    {

                        File[] inside = item.listFiles();

                        if(inside != null && inside.length == 0)
                        {

                            boolean deleted = item.delete();

                            if(!deleted)  System.out.println("rmdir: failed to delete " + item.getName());

                        }

                    }

                }

            }
            else System.out.println("rmdir: cannot access current directory");

            return;

        }

        //case 2: deleting one specific directory
        String path = String.join(" ", args); //handle names with spaces
        File dir = resolvePath(path);

        if(!dir.exists())
        {

            System.out.println("rmdir: directory does not exist");

            return;

        }
        if(!dir.isDirectory())
        {

            System.out.println("rmdir: not a directory");

            return;

        }

        File[] filesInside = dir.listFiles();

        if(filesInside != null && filesInside.length > 0)
        {

            System.out.println("rmdir: directory not empty");

            return;

        }

        //now delete
        boolean deleted = dir.delete();

        if(!deleted)  System.out.println("rmdir: failed to delete " + dir.getName());

    }
    public void rm(String[] args)
    {

        if(args.length == 0)
        {

            System.out.println("rm: missing operand");

            return;

        }

        //to handle files with space in their name
        String filename = String.join(" ", args);
        File file = resolvePath(filename);

        if(!file.exists())
        {

            System.out.println("No such a file. ");

            return;

        }

        boolean deleted = file.delete();

        if(!deleted) System.out.println(filename + " : Cannot be deleted. ");

    }
    public String cat(String[] args)
    {

        if(args.length == 0)
        {return "cat : invalid operand";}
        if(args.length > 2)
        {return "Invalid Input";}

        String result = "";

        if(args.length == 1)
        {

            //create object file from the argument
            String filename = args[0];
            File file = resolvePath(filename);
            
            if(!file.exists()) return "No such file.";
            if(!file.isFile()) return filename + " is a directory";

            //using bufferedReader class to read contents of files
            BufferedReader reader = null;

            try
            {

                reader = new BufferedReader(new FileReader(file));
                String line;

                while((line = reader.readLine()) != null) result += line + "\n";

                if(!result.isEmpty()) result = result.substring(0, result.length() - 1); //remove the last '\n'

            }
            catch(IOException error)
            {

                return "error reading file - " + error.getMessage();

            }
            finally
            {

                try
                {

                    //if reader created and read successfully then delete
                    if(reader != null) reader.close();

                }
                catch(IOException error)
                {

                    return "error closing file";

                }

            }

        }
        else if(args.length == 2)
        {

            String fileName1 = args[0], fileName2 = args[1];
            File file1 = resolvePath(fileName1);
            File file2 = resolvePath(fileName2);

            if(!file1.exists()) return fileName1 + ": no such file";
            if(!file2.exists()) return fileName2 + ": no such file";
            if(!file1.isFile()) return fileName1 + ": is not a valid file";
            if(!file2.isFile()) return fileName2 + ": is not a valid file";

            BufferedReader reader1 = null, reader2 = null;

            try
            {

                reader1 = new BufferedReader(new FileReader(file1));
                String line;

                while((line = reader1.readLine()) != null) result += line + "\n";

                reader2 = new BufferedReader(new FileReader(file2));

                while((line = reader2.readLine()) != null) result += line + "\n";

                if(!result.isEmpty()) result = result.substring(0, result.length() - 1); //remove the last '\n'

            }
            catch(IOException error)
            {

                return "error reading files: " + error.getMessage();

            }
            finally
            {

                try
                {

                    if(reader1 != null) reader1.close();
                    if(reader2 != null) reader2.close();

                }
                catch(IOException e)
                {

                    return "error closing files";

                }

            }

        }

        return result;

    }
    public String wc(String[] args)
    {

        if(args.length == 0) return "wc: missing operand";

        //to handle file with spaces in their names
        String filename = String.join(" ", args);
        File file = resolvePath(filename);

        if(!file.exists()) return filename + " no such a file. ";
        if(!file.isFile()) return filename + " is an invalid file. ";

        //variables to store needed measures
        int lines = 0, words = 0, charCount = 0;
        BufferedReader reader = null;

        try
        {

            reader = new BufferedReader(new FileReader(file));
            String line;

            while((line = reader.readLine()) != null)
            {

                lines++;

                String trimmedLine = line.trim();

                if(!trimmedLine.isEmpty())
                {

                    String[] word = trimmedLine.split("\\s+");
                    words += word.length;

                    charCount += trimmedLine.length() - word.length + 1;

                }

            }

        }
        catch(IOException error)
        {

            return "error reading file - " + error.getMessage();

        }
        finally
        {

            try
            {

                //if reader created and read successfully then delete
                if(reader != null) reader.close();

            }
            catch(IOException e)
            {

                return "error closing file";

            }

        }

        return lines + " " + words + " " + charCount + " " + filename;

    }
    public void zip(String[] args)
    {

        if(args.length < 2)
        {

            System.out.println("zip: missing operand");

            return;

        }

        //creating object files for the zip file
        String zipFileName = args[0];

        boolean recursive = false;
        int startIndex = 1;

        //check if its directory recursive
        if(args[0].equals("-r"))
        {

            recursive = true;
            startIndex = 2;
            zipFileName = args[1];

            if(args.length < 3)
            {

                System.out.println("zip: -r requires directory path");

                return;

            }

        }

        File zipFile = resolvePath(zipFileName);

        try
        {

            //both classes used to write data into a file and create new zip stream
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            //if directory recursive
            if(recursive)
            {

                //getting dir path
                String directoryPath = args[startIndex];
                File directory = resolvePath(directoryPath);

                if(!directory.exists())
                {

                    System.out.println("zip: directory does not exist: " + directoryPath);
                    zos.close();
                    fos.close();
                    zipFile.delete(); //delete the created zip file if error happens

                    return;

                }
                if(!directory.isDirectory())
                {

                    System.out.println("zip: path is not a directory: " + directoryPath);
                    zos.close();
                    fos.close();
                    zipFile.delete(); //delete the created zip file if error happens

                    return;

                }

                //call helper function to zip directories
                zipDirectory(directory, directory.getName(), zos);

            }
            else
            {

                //compress individual files
                for(int i = startIndex; i < args.length; i++)
                {

                    //create file object of the file we gonna zip
                    File fileToZip = resolvePath(args[i]);

                    //get its full path
                    if(!fileToZip.exists())
                    {

                        System.out.println("zip: file does not exist: " + args[i]);

                        continue;

                    }
                    if(fileToZip.isDirectory())
                    {

                        System.out.println("zip: " + args[i] + " is a directory. Use -r flag for directories.");

                        continue;

                    }

                    addFileToZip(fileToZip, fileToZip.getName(), zos);

                }

            }

            zos.close();
            fos.close();

            System.out.println("Successfully created: " + zipFile.getName());

        }
        catch(IOException error)
        {

            System.out.println("zip: error creating zip file: " + error.getMessage());

        }

    }
    private void addFileToZip(File file, String fileName, ZipOutputStream zos) throws IOException
    {

        //creates an input stream to read the raw data (bytes) from the file that is going to be zipped
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(fileName); //creating entry of the file we will add to the zip
        //that holds its metadata

        zos.putNextEntry(zipEntry);

        //creates a temporary byte array to efficiently move data in chunks rather than byte by byte.
        byte[] buffer = new byte[1024];
        int length;

        //reading the data of the file into the buffer then writes and compresses it
        while((length = fis.read(buffer)) >= 0) zos.write(buffer, 0, length);

        zos.closeEntry();
        fis.close();

    }
    //helper function for recursive zipping
    private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException
    {

        //listing files in the dir in an array
        File[] files = folder.listFiles();

        if(files == null) return;

        for(File file : files)
        {

            if(file.isDirectory()) zipDirectory(file, parentFolder + "/" + file.getName(), zos);
            else addFileToZip(file, parentFolder + "/" + file.getName(), zos);

        }

    }
    public void unzip(String[] args)
    {

        if(args.length == 0)
        {

            System.out.println("unzip: missing zip file argument");

            return;

        }

        String zipFileName = null;
        File extractDir = new File(currentDirectory);
        boolean hasDFlag = false;
        int dFlagIndex = -1;

        //check if the user wants to extract into different place
        for(int i = 0; i < args.length; i++)
        {

            if(args[i].equals("-d"))
            {

                hasDFlag = true;
                dFlagIndex = i;

                break;

            }

        }
        if(hasDFlag)
        {

            //check if the zip file name is given
            if(dFlagIndex == 0)
            {

                System.out.println("unzip: missing zip file argument before -d");

                return;

            }

            //getting zip file name while handling if it has any spaces in its name
            String[] zipNameParts = Arrays.copyOfRange(args, 0, dFlagIndex);
            zipFileName = String.join(" ", zipNameParts);

            //check if destination is given
            if(dFlagIndex + 1 >= args.length)
            {

                System.out.println("unzip: missing destination directory after -d");

                return;

            }

            //getting destination while handling if there is any spaces in its name
            String[] destParts = Arrays.copyOfRange(args, dFlagIndex + 1, args.length);
            String destPath = String.join(" ", destParts);
            extractDir = new File(destPath);

            //getting its full path if not given
            if(!extractDir.isAbsolute()) extractDir = new File(currentDirectory, destPath);

        }
        else zipFileName = String.join(" ", args);

        //creating file object of the zip file name
        File zipFile = resolvePath(zipFileName);

        if(!zipFile.exists())
        {

            System.out.println("unzip: file does not exist: " + zipFileName);

            return;

        }
        if(!zipFile.isFile())
        {

            System.out.println("unzip: not a file: " + zipFileName);

            return;

        }
        if(!extractDir.exists())
        {

            boolean created = extractDir.mkdirs();

            if(!created)
            {

                System.out.println("unzip: failed to create extraction directory");

                return;

            }

        }

        FileInputStream fis = null;
        ZipInputStream zis = null;

        try
        {

            fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(fis); //reads zip data
            ZipEntry entry;

            while((entry = zis.getNextEntry()) != null)
            {

                String entryName = entry.getName();
                File newFile = new File(extractDir, entryName);
                String canonicalDestPath = extractDir.getCanonicalPath(), canonicalNewFilePath = newFile.getCanonicalPath();
                //using getCanonicalPath() function to get full right path

                if(!canonicalNewFilePath.startsWith(canonicalDestPath + File.separator))
                {

                    System.out.println("unzip: invalid entry path: " + entryName);

                    zis.closeEntry();

                    continue;

                }

                //if the zipped file is a dir
                if(entry.isDirectory()) newFile.mkdirs();
                else
                {

                    File parent = newFile.getParentFile();

                    //create parent dir if not existing
                    if(parent != null && !parent.exists()) parent.mkdirs();

                    FileOutputStream fos = new FileOutputStream(newFile);
                    byte[] buffer = new byte[1024];
                    int length;

                    while((length = zis.read(buffer)) > 0) fos.write(buffer, 0, length);

                    fos.close();

                }

                zis.closeEntry();

            }

            System.out.println("Successfully extracted: " + zipFile.getName());

        }
        catch(IOException error)
        {

            System.out.println("unzip: error extracting zip file: " + error.getMessage());

        }
        finally
        {

            try
            {

                if(zis != null) zis.close();
                if(fis != null) fis.close();

            }
            catch(IOException e)
            {

                System.out.println("unzip: error closing streams");

            }

        }

    }
    public void exit(String commandName)
    {

        if(commandName.equals("exit"))
        {System.exit(0);}

    }
    public void override(String[] args, String commandName)
    {

        try
        {

            String res = "";
            String targetFileName = "";

            //pwd or ls
            if(args.length == 2)
            {

                targetFileName = args[1];
                args = new String[]{}; //no args

                //check which command is it
                if(commandName.equals("pwd")) res = pwd(args);
                else if(commandName.equals("ls")) res = ls(args);

            }
            else if(args.length == 3)
            {

                targetFileName = args[2];
                args = new String[]{args[0]}; //only first file we need in case of cat and wc

                if(commandName.equals("cat")) res = cat(args);
                else if(commandName.equals("wc")) res = wc(args);

            }
            else if(args.length == 4) //only 2nd cat case
            {

                targetFileName = args[3];
                args = new String[]{args[0], args[1]}; //only first 2 files we need in case of cat
                res = cat(args);

            }

            File targetFile = resolvePath(targetFileName);
            FileWriter fw = new FileWriter(targetFile, false); //false for override

            fw.write(res); //writes output into file, creates it if not existing
            fw.close();

        }
        catch(IOException error)
        {

            System.out.println("Error writing to file: " + error.getMessage());

        }

    }
    public void append(String[] args, String commandName)
    {

        try
        {

            String res = "";
            String targetFileName = "";

            //pwd or ls
            if(args.length == 2)
            {

                targetFileName = args[1];
                args = new String[]{}; //no args

                //check which command is it
                if(commandName.equals("pwd")) res = pwd(args);
                else if(commandName.equals("ls")) res = ls(args);

            }
            else if(args.length == 3)
            {

                targetFileName = args[2];
                args = new String[]{args[0]}; //only first file we need in case of cat and wc

                if(commandName.equals("cat")) res = cat(args);
                else if(commandName.equals("wc")) res = wc(args);

            }
            else if(args.length == 4) //only 2nd cat case
            {

                targetFileName = args[3];
                args = new String[]{args[0], args[1]}; //only first 2 files we need in case of cat

                res = cat(args);

            }

            File targetFile = resolvePath(targetFileName);
            FileWriter fw = new FileWriter(targetFile, true); //true for append

            fw.write(res); //writes output into file, creates it if not existing
            fw.close();

        }
        catch(IOException error)
        {

            System.out.println("Error writing to file: " + error.getMessage());

        }

    }

    public void chooseCommandAction() throws IOException
    {

        String[] arguments = parser.getArgs();
        boolean override = false, append = false;

        //see the arguments have '>' or '>>' and get their index
        for(int i = 0; i < arguments.length; i++)
        {

            if(arguments[i].equals(">"))
            {

                override = true;

                break;

            }
            else if(arguments[i].equals(">>"))
            {

                append = true;

                break;

            }

        }

        if(parser.getCommandName().equals("pwd"))
        {

            if(arguments.length > 2)
            {

                System.out.println("Invalid Arguments");

                return;

            }
            if(override) override(arguments, "pwd");
            else if (append) append(arguments, "pwd");
            else System.out.println(pwd(arguments));

        }
        else if(parser.getCommandName().equals("cd")) cd(arguments);
        else if(parser.getCommandName().equals("mkdir")) mkdir(arguments);
        else if(parser.getCommandName().equals("ls"))
        {

            if(arguments.length > 2)
            {

                System.out.println("Invalid Arguments");

                return;

            }
            if(override) override(arguments, "ls");
            else if(append) append(arguments, "ls");
            else System.out.println(ls(arguments));

        }
        else if(parser.getCommandName().equals("touch")) touch(arguments);
        else if(parser.getCommandName().equals("rmdir")) rmdir(arguments);
        else if(parser.getCommandName().equals("rm")) rm(arguments);
        else if(parser.getCommandName().equals("cat"))
        {

            if(arguments.length > 5)
            {

                System.out.println("Invalid Arguments");

                return;

            }
            if(override) override(arguments, "cat");
            else if(append) append(arguments, "cat");
            else System.out.println(cat(arguments));

        }
        else if(parser.getCommandName().equals("wc"))
        {

                if(arguments.length > 3)
                {

                    System.out.println("Invalid Arguments");

                    return;

                }
                if(override) override(arguments, "wc");
                else if(append) append(arguments, "wc");
                else System.out.println(wc(arguments));

            }
        else if(parser.getCommandName().equals("zip")) zip(arguments);
        else if(parser.getCommandName().equals("unzip")) unzip(arguments);
        else if(parser.getCommandName().equals("exit")) exit(parser.getCommandName());
        else if(parser.getCommandName().equals("cp")) cp(arguments);
        else System.out.println(parser.getCommandName() + ": command not found");

    }

}