package pt.bamol.atl.json2ecore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileContentReplacer {

    private final Pattern pattern;
    private final String replaceMent;

    public FileContentReplacer(String pattern, String replaceMent) {
        this.pattern = Pattern.compile(pattern);
        this.replaceMent = replaceMent;
    }

    public String matchAndReplace(String line) {
        return pattern.matcher(line).replaceAll(replaceMent);
    }

    public void matchAndReplace(File inFile, File outFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inFile));
        if (!outFile.exists()) {
            outFile.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outFile));

        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(matchAndReplace(line));
                bufferedWriter.newLine();
            }
        } finally {
            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    public void execute(String inFileName, String outFileName) throws IOException {
            File inFile = new File(inFileName);
            File outFile = new File(outFileName);
            matchAndReplace(inFile, outFile);
    }    

    public static void main(String[] args) throws IOException {
        if (args.length >= 4) {
            FileContentReplacer replacer = new FileContentReplacer(args[0], args[1]);
            File inFile = new File(args[2]);
            File outFile = new File(args[3]);
            replacer.matchAndReplace(inFile, outFile);
        } else {
            System.out.println("Please enter 4 args");
        }
    }
}
